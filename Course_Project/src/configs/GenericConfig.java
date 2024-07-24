package configs;

import java.lang.reflect.Constructor;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import graph.Agent;
import graph.ParallelAgent;
import graph.TopicManagerSingleton;

/**
 * A generic configuration class for setting up agents and topics from a configuration file.
 * It reads agent configurations from a file, creates agents, and manages them.
 */
public class GenericConfig implements Config {

    private List<Agent> agents;
    private final int capacity = 50;
    private String file;

    /**
     * Constructs a GenericConfig instance, initializing the list of agents and setting the configuration file to an empty string.
     */
    public GenericConfig(){
        agents = new ArrayList<>();
        this.file = "";
    }

    /**
     * Sets the configuration file to read from. If the file is different from the current one, it updates the file and closes the existing configuration.
     *
     * @param file the path to the configuration file
     */
    public void setConfFile(String file){
        if (!this.file.equals(file)) {
            this.file = file;
            this.close();
        }
    }

    /**
     * Reads the configuration file, creates agents based on the file contents, and adds them to the list.
     * The file should contain agent class names followed by their subscriber and publisher lists in a specific format.
     */
    @Override
    public void create() {
        List<String> lines;
        try {
            lines = Files.readAllLines(Paths.get(this.file)); // Read all lines
        } catch(Exception e) {
            e.printStackTrace();
            System.out.println("Can't read file " + this.file + " in config " + this.getName());
            return;
        }

        if (lines.size() % 3 != 0){ // If not a multiple of 3
            System.out.println("Invalid number of lines in file " + this.file);
            return;
        }

        for (int i = 0; i < lines.size(); i+=3){
            String[] subs = lines.get(i + 1).split(","); // Get list of subs and pubs of the agent
            String[] pubs = lines.get(i + 2).split(",");

            try {
                Class<?> agentClass = Class.forName(lines.get(i)); // Create new agent from line
                try {
                    Constructor<?> constructor = agentClass.getConstructor(subs.getClass(), pubs.getClass());
                    Agent agent = (Agent) constructor.newInstance(subs, pubs);
                    Agent parallelAgent = new ParallelAgent(agent, capacity); // Create ParallelAgent from agent

                    if (this.agents.stream().noneMatch(parallelAgent::equals)) { // Add if not in list
                        agents.add(parallelAgent);
                    } else {
                        parallelAgent.close(); // If exists, close the new one
                        agent = null;
                        parallelAgent = null;
                    }
                } catch (Exception e) {
                    System.out.println("Can't create constructor for line " + (i+1) + ": " + lines.get(i) + " in file " + this.file);
                    return;
                }
            } catch (ClassNotFoundException e) {
                System.out.println("No class matches line " + (i+1) + ": " + lines.get(i) + " in file " + this.file);
                return;
            }
        }
    }

    /**
     * Closes all agents and clears all topics from the TopicManagerSingleton.
     */
    public void close() {
        for (Agent a: this.agents) { // Close all agents in List
            a.close();
        }
        TopicManagerSingleton.get().clear();
    }

    /**
     * Gets the name of this configuration.
     *
     * @return the name of the configuration
     */
    @Override
    public String getName() {
        return "GenericConfig";
    }

    /**
     * Gets the version of this configuration.
     *
     * @return the version number
     */
    @Override
    public int getVersion() {
        return 1;
    }
}
