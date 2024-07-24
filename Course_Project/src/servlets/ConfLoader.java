package servlets;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;
import java.nio.file.Files;
import java.nio.file.Paths;
import server.RequestParser.RequestInfo;
import views.HtmlGraphWriter;
import configs.GenericConfig;
import graph.Graph;
import graph.Topic;
import graph.TopicManagerSingleton;

public class ConfLoader implements Servlet {

    private final String directory;
    private final String graphPath;
    private final String tablePath;
    private GenericConfig config;

    public ConfLoader(String htmlDirectory) {
        this.config = new GenericConfig();
        this.directory = htmlDirectory;
        this.graphPath = htmlDirectory + "/graph.html";
        this.tablePath = htmlDirectory + "/table.html";
    }

    public void createTable() {
        StringBuilder html = new StringBuilder();
        html.append("<html>\n\t<body>\n");
        html.append("\t\t<table border='1'>\n\t\t\t<tr><th>Topic</th><th>Message</th></tr>\n");

        for (Topic topic : TopicManagerSingleton.get().getTopics()) {
            html.append("\t\t\t<tr><td>").append(topic.name).append("</td>");
            html.append("<td>").append(topic.getMessage().asText).append("</td></tr>\n");
        }

        html.append("\t\t</table>\n\t</body>\n</html>");

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
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }
    
    public void createGraph() {
        // Create Graph instance
        Graph graph = new Graph();
        graph.createFromTopics();
        if (graph.hasCycles()){
            return;
        }
        // Generate HTML representation of the graph
        String graphHtml = String.join("\n", HtmlGraphWriter.getGraphHTML(graph, this.directory + "/graphTemplate.html"));

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
            Files.write(Paths.get(fileName), fileContent);

            // Load GenericConfig
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