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
            String graphHtml = String.join("\n", HtmlGraphWriter.getGraphHTML(graph));

            String filePath = "../html_files/graph.html";

            // Remove existing content
            File file = new File(filePath);
            if (file.exists()) {
                file.delete();
            }

            // Write new content
            try {
                FileWriter myWriter = new FileWriter(filePath);
                myWriter.write(graphHtml);
                myWriter.close();
                System.out.println("Successfully wrote to the file.");
            } catch (IOException e) {
                System.out.println("An error occurred.");
                e.printStackTrace();
            }

            String response = "HTTP/1.1 205 Reset Content\r\n" +
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
        // No resources to close
    }
}