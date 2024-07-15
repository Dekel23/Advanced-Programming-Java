package servlets;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;
import server.RequestParser.RequestInfo;
import views.HtmlGraphWriter;
import configs.GenericConfig;
import graph.Graph;
import graph.Topic;
import graph.TopicManagerSingleton;

public class ConfLoader implements Servlet {

    private final String graphPath = "../html_files/graph.html";
    private final String tablePath = "../html_files/table.html";
    private GenericConfig config;

    public ConfLoader() {
        this.config = new GenericConfig();
    }

    public void createTable() {
        StringBuilder html = new StringBuilder();
        html.append("<html><body>\n");
        html.append("<h1>Topic Information</h1>\n");
        html.append("<table border='1'><tr><th>Topic</th><th>Message</th></tr>\n");

        for (Topic topic : TopicManagerSingleton.get().getTopics()) {
            html.append("<tr><td>").append("T" + topic.name).append("</td>\n");
            html.append("<td>").append(topic.export.asText).append("</td></tr>\n");
        }

        html.append("</table></body></html>");

        String htmlContent = html.toString();

        // Remove existing content
        File file = new File(this.tablePath);
        if (file.exists()) {
            file.delete();
        }

        // Write new content
        try {
            FileWriter myWriter = new FileWriter(this.tablePath);
            myWriter.write(htmlContent);
            myWriter.close();
            System.out.println("Successfully wrote to the file.");
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }
    
    public void createGraph() {
        // Create Graph instance
        Graph graph = new Graph();
        graph.createFromTopics();

        // Generate HTML representation of the graph
        String graphHtml = String.join("\n", HtmlGraphWriter.getGraphHTML(graph));

        // Remove existing content
        File file = new File(this.graphPath);
        if (file.exists()) {
            file.delete();
        }

        // Write new content
        try {
            FileWriter myWriter = new FileWriter(this.graphPath);
            myWriter.write(graphHtml);
            myWriter.close();
            System.out.println("Successfully wrote to the graph.");
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }

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
            config.close();
            config.setConfFile(fileName);
            config.create();

            this.createGraph();
            this.createTable();

            String response = "HTTP/1.1 200 OK\r\n" +
                              "\r\n";

            sendResponse(toClient, response);
        } else {
            sendResponse(toClient, "HTTP/1.1 400 Bad Request\r\n\r\nMissing file name or content");
        }
    }

    private void sendResponse(OutputStream toClient, String response) throws IOException {
        toClient.write(response.getBytes());
        toClient.flush();
    }

    @Override
    public void close() throws IOException {
        config.close();
        // Remove existing content
        File file = new File(this.graphPath);
        if (file.exists()) {
            file.delete();
        }
        file = new File(this.tablePath);
        if (file.exists()) {
            file.delete();
        }
    }
}