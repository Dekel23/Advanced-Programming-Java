package views;

import java.util.ArrayList;
import java.util.List;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.io.IOException;

import graph.Graph;
import graph.Node;

public class HtmlGraphWriter {
    public static List<String> getGraphHTML(Graph graph) {
        List<String> htmlContent = new ArrayList<>();
        
        try {
            // Read the static HTML template
            String template = new String(Files.readAllBytes(Paths.get("files_html/graph.html")));
            
            // Generate nodes and edges representation
            StringBuilder nodesBuilder = new StringBuilder();
            StringBuilder edgesBuilder = new StringBuilder();
            
            for (Node node : graph) {
                String nodeType = node.getClass().getSimpleName().toLowerCase();
                nodesBuilder.append(String.format("{ id: '%s', label: '%s', type: '%s' },", 
                                    node.getName(), node.getName(), nodeType));
                
                for (Node edge : node.getEdges()) {
                    edgesBuilder.append(String.format("{ from: '%s', to: '%s' },", 
                                        node.getName(), edge.getName()));
                }
            }
            
            // Remove the last comma
            if (nodesBuilder.length() > 0) {
                nodesBuilder.setLength(nodesBuilder.length() - 1);
            }
            if (edgesBuilder.length() > 0) {
                edgesBuilder.setLength(edgesBuilder.length() - 1);
            }
            
            // Create the function call with actual data
            String graphData = String.format("createGraph([%s], [%s]);", nodesBuilder.toString(), edgesBuilder.toString());
            
            // Replace placeholder in the template
            String filledTemplate = template.replace("// createGraph({{NODES}}, {{EDGES}});", graphData);
            
            // Split the filled template into lines
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