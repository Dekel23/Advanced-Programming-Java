package configs;

import java.lang.reflect.Constructor;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import graph.Agent;
import graph.ParallelAgent;

public class GenericConfig implements Config {

    private List<Agent> agents;
    private final int capacity = 50;
    private String file;

    public GenericConfig(){
        agents = new ArrayList<>();
    }

    public void setConfFile(String file){
        this.file = file;
    }

    @Override
    public void create() {
        List<String> lines;
        try {
            lines = Files.readAllLines(Paths.get(this.file));
        } catch(Exception e) {
            e.printStackTrace();
            System.out.println("Can't read file " + this.file + " in config " + this.getName());
            return;
        }

        if (lines.size() % 3 != 0){
            System.out.println("Invalid number of lines in file " + this.file);
            return;
        }

        for (int i = 0; i < lines.size(); i+=3){
            String[] subs = lines.get(i + 1).split(",");
            String[] pubs = lines.get(i + 2).split(",");

            try {
                Class<?> agentClass = Class.forName(lines.get(i));
                try {
                    Constructor<?> constructor = agentClass.getConstructor(subs.getClass(), pubs.getClass());
                    Agent agent = (Agent) constructor.newInstance(subs, pubs);
                    Agent parallelAgent = new ParallelAgent(agent, capacity);
                    agents.add(parallelAgent);
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
        for (Agent a: this.agents){
            a.close();
        }
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
