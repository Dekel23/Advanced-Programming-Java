package servlets;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;
import server.RequestParser.RequestInfo;
import configs.GenericConfig;
import graph.Graph;
import graph.Node;

public class ConfLoader implements Servlet {
    @Override
    public void handle(RequestInfo ri, OutputStream toClient) throws IOException {
        if (!"POST".equals(ri.getHttpCommand())) {
            sendResponse(toClient, "HTTP/1.1 405 Method Not Allowed\r\n\r\n");
            return;
        }

        Map<String, String> parameters = ri.getParameters();
        String fileName = parameters.get("filename");
        byte[] fileContent = ri.getContent();

        if (fileName != null && fileContent != null && fileContent.length > 0) {
            // Save the file content
            Files.write(Paths.get(fileName), fileContent);

            // Load GenericConfig
            GenericConfig config = new GenericConfig();
            config.setConfFile(fileName);
            config.create();

            // Create Graph instance
            Graph graph = new Graph();
            graph.createFromTopics();

            // Generate HTML representation of the graph
            String graphHtml = generateGraphHtml(graph);

            String response = "HTTP/1.1 200 OK\r\n" +
                              "Content-Type: text/html\r\n" +
                              "Content-Length: " + graphHtml.length() + "\r\n" +
                              "\r\n" +
                              graphHtml;

            sendResponse(toClient, response);
        } else {
            sendResponse(toClient, "HTTP/1.1 400 Bad Request\r\n\r\nMissing file name or content");
        }
    }

    private String generateGraphHtml(Graph graph) {
        StringBuilder html = new StringBuilder();
        html.append("<html><head><style>");
        html.append("table { border-collapse: collapse; width: 100%; }");
        html.append("th, td { border: 1px solid black; padding: 8px; text-align: left; }");
        html.append("th { background-color: #f2f2f2; }");
        html.append("</style></head><body>");
        html.append("<h1>Computational Graph</h1>");
        html.append("<table><tr><th>Node</th><th>Edges</th><th>Message</th></tr>");
        
        for (Node node : graph) {
            html.append("<tr><td>").append(node.getName()).append("</td><td>");
            for (Node edge : node.getEdges()) {
                html.append(edge.getName()).append("<br>");
            }
            html.append("</td><td>");
            if (node.getMessage() != null) {
                html.append(node.getMessage().asText);
            }
            html.append("</td></tr>");
        }
        
        html.append("</table>");
        if (graph.hasCycles()) {
            html.append("<p style='color: red;'><strong>Warning: The graph contains cycles!</strong></p>");
        }
        html.append("</body></html>");
        return html.toString();
    }

    private void sendResponse(OutputStream toClient, String response) throws IOException {
        toClient.write(response.getBytes());
        toClient.flush();
    }

    @Override
    public void close() throws IOException {
        // No resources to close
    }
}