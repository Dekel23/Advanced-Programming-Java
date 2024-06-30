package config;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import graph.Agent;
import graph.Topic;
import graph.TopicManagerSingleton;

public class Graph extends ArrayList<Node>{
    
    public boolean hasCycles() {
        Set<Node> visited = new HashSet<>();
        Set<Node> inStack = new HashSet<>();

        for (Node node : this) {
            if (!visited.contains(node)) {
                if (node.hasCycles(visited, inStack)) {
                    return true;
                }
            }
        }
        return false;
    }

    public void createFromTopics(){
        for (Topic t: TopicManagerSingleton.get().getTopics()){
            Node node_t = new Node("T" + t.name);
            this.add(node_t);
            t.getSubs().forEach(a -> {
                Node node_a = this.getAgent(a);
                node_t.addEdge(node_a);
            });
            t.getPubs().forEach(a -> {
                Node node_a = this.getAgent(a);
                node_a.addEdge(node_t);
            });
        }
    }

    private Node getAgent(Agent a){
        for (Node n: this){
            if (n.getName().equals("A" + a.getName()))
                return n;
        }
        Node node_a = new Node("A" + a.getName());
        this.add(node_a);
        return node_a;
    } 
}
