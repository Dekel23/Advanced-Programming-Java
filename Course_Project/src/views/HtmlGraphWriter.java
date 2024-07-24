package views;

import java.util.ArrayList;
import java.util.List;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.io.IOException;

import graph.Graph;
import graph.Node;

/**
 * A utility class for generating HTML representations of graphs.
 * <p>
 * This class reads an HTML template and replaces placeholders with graph data
 * to create a visual representation of the graph.
 * </p>
 */
public class HtmlGraphWriter {

    /**
     * Constructs an {@code HtmlGraphWriter} instance.
     * <p>
     * This default constructor is provided to allow instantiation of the class.
     * </p>
     */
    public HtmlGraphWriter() {
        // Default constructor
    }

    /**
     * Generates the HTML content for visualizing a graph based on the provided
     * graph and HTML template.
     *
     * @param graph the {@link Graph} to be visualized
     * @param pathTemplate the path to the HTML template file
     * @return a {@link List} of strings representing the HTML content for the graph
     */
    public static List<String> getGraphHTML(Graph graph, String pathTemplate) {
        List<String> htmlContent = new ArrayList<>();
        
        try {
            // Read the static HTML template
            String template = new String(Files.readAllBytes(Paths.get(pathTemplate)));
            
            // Generate nodes and edges representation
            StringBuilder nodesBuilder = new StringBuilder();
            StringBuilder edgesBuilder = new StringBuilder();
            
            for (Node node : graph) {
                if (node.getName().charAt(0) == 'T'){ // If Topic then color blue
                    nodesBuilder.append(String.format("{ id: '%s', message: '%s', color: 'blue'},", node.getName().substring(1), node.getMessage().asText));
                } else if (node.getName().charAt(0) == 'A'){ // If Agent then color red
                    nodesBuilder.append(String.format("{ id: '%s', message: '%s', color: 'red'},", node.getName().substring(1), node.getMessage().asText));
                }
                
                for (Node edge : node.getEdges()) {
                    edgesBuilder.append(String.format("{ source: '%s', target: '%s' },", node.getName().substring(1), edge.getName().substring(1)));
                }
            }
            
            // Remove the last comma
            if (nodesBuilder.length() > 0) {
                nodesBuilder.setLength(nodesBuilder.length() - 1);
            }
            if (edgesBuilder.length() > 0) {
                edgesBuilder.setLength(edgesBuilder.length() - 1);
            }
            
            // Change strings format
            String graphData = String.format("createGraph([%s], [%s]);", nodesBuilder.toString(), edgesBuilder.toString());
            
            // Change template to current graph
            String filledTemplate = template.replace("// createGraph({{NODES}}, {{EDGES}});", graphData);

            // Split content to lines
            String[] lines = filledTemplate.split("\n");
            for (String line : lines) {
                htmlContent.add(line);
            }

        } catch (IOException e) {
            e.printStackTrace();
            htmlContent.add("<p>Error generating graph visualization.</p>");
        }
        
        return htmlContent;
    }
}