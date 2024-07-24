package graph;

import java.util.ArrayList;
import java.util.List;
import java.util.HashSet;
import java.util.Set;
import java.util.Stack;

/**
 * Represents a node in a graph with a name, a list of edges to other nodes, and a message.
 * <p>
 * This class provides functionality to manage edges between nodes and check for cycles
 * in the graph starting from this node.
 * </p>
 */
public class Node {
    private String name;
    private List<Node> edges;
    private Message msg;

    /**
     * Constructs a {@code Node} with the specified name.
     * <p>
     * Initializes the list of edges as an empty list and the message with an empty string.
     * </p>
     * 
     * @param name the name of this node
     */
    public Node(String name) {
        this.name = name;
        this.edges = new ArrayList<>();
        this.msg = new Message("");
    }

    /**
     * Returns the name of this node.
     * 
     * @return the name of this node
     */
    public String getName() { return this.name; }

    /**
     * Returns the list of edges (connected nodes) for this node.
     * 
     * @return the list of edges for this node
     */
    public List<Node> getEdges() { return this.edges; }

    /**
     * Returns the message associated with this node.
     * 
     * @return the message associated with this node
     */
    public Message getMessage() { return this.msg; }

    /**
     * Sets the name of this node.
     * 
     * @param name the new name for this node
     */
    public void setName(String name) { this.name = name; }

    /**
     * Sets the list of edges (connected nodes) for this node.
     * 
     * @param edges the new list of edges for this node
     */
    public void setEdges(List<Node> edges) { this.edges = edges; }

    /**
     * Sets the message associated with this node.
     * 
     * @param msg the new message for this node
     */
    public void setMessage(Message msg) { this.msg = msg; }

    /**
     * Adds an edge (connection) from this node to the specified node.
     * 
     * @param edge the node to be added as an edge
     */
    public void addEdge(Node edge) {
        this.edges.add(edge);
    }

    /**
     * Checks if there is a cycle in the graph starting from this node.
     * <p>
     * This method uses a depth-first search approach to detect cycles.
     * </p>
     * 
     * @return {@code true} if a cycle exists, {@code false} otherwise
     */
    public boolean hasCycles() {
        Set<Node> visited = new HashSet<>();
        Set<Node> inStack = new HashSet<>();
        return this.hasCycles(visited, inStack);
    }

    /**
     * Helper method to check for cycles in the graph using depth-first search.
     * <p>
     * This method uses a stack to keep track of nodes in the current path and
     * detects cycles if a node is revisited within the same path.
     * </p>
     * 
     * @param visited a set of nodes that have been visited
     * @param inStack a set of nodes currently in the stack
     * @return {@code true} if a cycle is detected, {@code false} otherwise
     */
    public boolean hasCycles(Set<Node> visited, Set<Node> inStack) {
        Stack<Node> stack = new Stack<>();
        stack.push(this);

        while (!stack.isEmpty()) {
            Node current = stack.peek();

            if (!visited.contains(current)) {
                visited.add(current);
                inStack.add(current);
            }

            boolean hasUnvisitedNeighbors = false;
            for (Node neighbor : current.getEdges()) {
                if (!visited.contains(neighbor)) {
                    stack.push(neighbor);
                    hasUnvisitedNeighbors = true;
                } else if (inStack.contains(neighbor)) {
                    return true;
                }
            }

            if (!hasUnvisitedNeighbors) {
                inStack.remove(current);
                stack.pop();
            }
        }
        return false;
    }
}