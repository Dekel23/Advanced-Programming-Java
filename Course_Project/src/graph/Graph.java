package graph;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Graph extends ArrayList<Node>{
    
    // Check if graph as cycles
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

    // Create graph from all topics in system
    public void createFromTopics() {
        // Get all topics from the TopicManager
        Collection<Topic> topics = TopicManagerSingleton.get().getTopics();
        
        // Create a map to store agents and their corresponding nodes
        Map<Agent, Node> agentNodeMap = new HashMap<>();
        
        // Iterate through all topics
        for (Topic topic : topics) {
            // Create a node for the topic
            Node topicNode = new Node("T" + topic.name);
            topicNode.setMessage(topic.getMessage());
            this.add(topicNode);
            
            // Process publishers
            for (Agent publisher : topic.getPubs()) {
                Node publisherNode = getOrCreateAgentNode(publisher, agentNodeMap);
                publisherNode.addEdge(topicNode);
            }
            
            // Process subscribers
            for (Agent subscriber : topic.getSubs()) {
                Node subscriberNode = getOrCreateAgentNode(subscriber, agentNodeMap);
                topicNode.addEdge(subscriberNode);
            }
        }
    }

    private Node getOrCreateAgentNode(Agent agent, Map<Agent, Node> agentNodeMap) {
        return agentNodeMap.computeIfAbsent(agent, a -> {
            Node node = new Node("A" + a.getName());
            this.add(node);
            return node;
        });
    }
}
