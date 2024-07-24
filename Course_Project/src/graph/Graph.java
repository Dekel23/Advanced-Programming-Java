package graph;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Represents a directed graph where nodes are topics and agents.
 * <p>
 * This class extends {@code ArrayList<Node>} to manage nodes in the graph and
 * provides methods to check for cycles and to create a graph representation from
 * topics and agents.
 * </p>
 */
public class Graph extends ArrayList<Node> {

    /**
     * Constructs an empty {@code Graph} instance.
     * <p>
     * This default constructor initializes the graph as an empty list of nodes.
     * </p>
     */
    public Graph() {
        super(); // Calls the constructor of ArrayList<Node>
    }

    /**
     * Checks if the graph contains any cycles.
     * <p>
     * This method performs a depth-first search to detect cycles in the graph.
     * </p>
     * 
     * @return {@code true} if the graph contains cycles, {@code false} otherwise
     */
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

    /**
     * Creates a graph from the current topics in the system.
     * <p>
     * This method retrieves all topics from the {@code TopicManagerSingleton} and
     * constructs nodes for each topic and agent. Edges are created based on
     * the publisher-subscriber relationships between topics and agents.
     * </p>
     */
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

    /**
     * Retrieves or creates a node for the specified agent.
     * <p>
     * If a node for the agent already exists, it is returned; otherwise, a new node
     * is created, added to the graph, and then returned.
     * </p>
     * 
     * @param agent the agent for which to retrieve or create a node
     * @param agentNodeMap a map storing existing nodes for agents
     * @return the node associated with the specified agent
     */
    private Node getOrCreateAgentNode(Agent agent, Map<Agent, Node> agentNodeMap) {
        return agentNodeMap.computeIfAbsent(agent, a -> {
            Node node = new Node("A" + a.getName());
            this.add(node);
            return node;
        });
    }
}