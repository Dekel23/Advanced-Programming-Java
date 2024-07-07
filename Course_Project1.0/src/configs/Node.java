package configs;

import java.util.ArrayList;
import java.util.List;
import graph.Message;
import java.util.HashSet;
import java.util.Set;
import java.util.Stack;

public class Node {
    private String name;
    private List<Node> edges;
    private Message msg;

    public Node(String name) {
        this.name = name;
        this.edges = new ArrayList<>();
    }

    public String getName() {return this.name;}
    public List<Node> getEdges() {return this.edges;}
    public Message getMessage() {return this.msg;}

    public void setName(String name) {this.name = name;}
    public void setEdges(List<Node> edges) {this.edges = edges;}
    public void setMessage(Message msg) {this.msg = msg;}

    public void addEdge(Node edge) {
        this.edges.add(edge);
    }

    public boolean hasCycles(){
        Set<Node> visited = new HashSet<>();
        Set<Node> inStack = new HashSet<>();
        return this.hasCycles(visited, inStack);
    }

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