package configs;

import java.lang.reflect.Constructor;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import graph.Agent;
import graph.ParallelAgent;
import graph.TopicManagerSingleton;

public class GenericConfig implements Config {

    private List<Agent> agents;
    private final int capacity = 50;
    private String file;

    public GenericConfig(){
        agents = new ArrayList<>();
        this.file = "";
    }

    // Set config file to read from
    public void setConfFile(String file){
        if (!this.file.equals(file)) {
            this.file = file;
            this.close();
        }
    }

    // Create system agents and topics from file
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

        if (lines.size() % 3 != 0){ // If not multyple of 3
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
                    Agent parallelAgent = new ParallelAgent(agent, capacity); // Create parallelAgent from agent

                    if (this.agents.stream().noneMatch(parallelAgent::equals)) { // Add if not in list
                        agents.add(parallelAgent);
                    } else {
                        parallelAgent.close(); // If exist close the new one
                        agent = null;
                        parallelAgent = null;
                    }
                } catch (Exception e) {
                    System.out.println("Cant make constructor for line " + (i+1) + ": " + lines.get(i) + " in file" + this.file);
                    return;
                }
            } catch (ClassNotFoundException e) {
                System.out.println("No class match to line " + (i+1) + ": " + lines.get(i) + " in file" + this.file);
                return;
            }
        }
    }

    public void close() {
        for (Agent a: this.agents) { // Close all agents in List
            a.close();
        }
        TopicManagerSingleton.get().clear();
    }

    @Override
    public String getName() {
        return "GenericConfig";
    }


    @Override
    public int getVersion() {
        return 1;
    }
    
}
