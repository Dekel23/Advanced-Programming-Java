package servlets;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;
import graph.Graph;
import graph.Topic;
import graph.TopicManagerSingleton;
import configs.GenericConfig;
import views.HtmlGraphWriter;
import server.RequestParser.RequestInfo;

/**
 * Handles HTTP requests for loading configuration files and generating corresponding HTML representations.
 * It creates tables and graphs based on the topics and agents defined in the configuration.
 */
public class ConfLoader implements Servlet {

    private final String directory;
    private final String graphPath;
    private final String tablePath;
    private GenericConfig config;

    /**
     * Constructs a ConfLoader instance with the specified directory for HTML files.
     *
     * @param htmlDirectory the directory where HTML files will be saved
     */
    public ConfLoader(String htmlDirectory) {
        this.config = new GenericConfig();
        this.directory = htmlDirectory;
        this.graphPath = htmlDirectory + "/graph.html";
        this.tablePath = htmlDirectory + "/table.html";
    }

    /**
     * Creates an HTML table representing the current topics and their messages.
     * The table is saved to the file specified by {@code tablePath}.
     */
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
        try (FileWriter myWriter = new FileWriter(this.tablePath)) {
            myWriter.write(htmlContent);
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }

    /**
     * Creates an HTML graph representation of the current system state.
     * The graph is saved to the file specified by {@code graphPath}.
     */
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
        try (FileWriter myWriter = new FileWriter(this.graphPath)) {
            myWriter.write(graphHtml);
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }

    /**
     * Handles HTTP POST requests to upload a configuration file, process it, and generate the graph and table HTML files.
     *
     * @param ri the request information containing parameters and content
     * @param toClient the output stream to send the response to the client
     * @throws IOException if an I/O error occurs while handling the request
     */
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

            File file = new File(fileName);
            file.delete();

            this.createGraph();
            this.createTable();

            String response = "HTTP/1.1 200 OK\r\n" +
                              "\r\n";

            sendResponse(toClient, response);
        } else {
            sendResponse(toClient, "HTTP/1.1 400 Bad Request\r\n\r\nMissing file name or content");
        }
    }

    /**
     * Sends an HTTP response to the client.
     *
     * @param toClient the output stream to send the response to
     * @param response the HTTP response string
     * @throws IOException if an I/O error occurs while sending the response
     */
    private void sendResponse(OutputStream toClient, String response) throws IOException {
        toClient.write(response.getBytes());
        toClient.flush();
    }

    /**
     * Closes the configuration by closing all agents and deleting the HTML files.
     *
     * @throws IOException if an I/O error occurs while closing the configuration
     */
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