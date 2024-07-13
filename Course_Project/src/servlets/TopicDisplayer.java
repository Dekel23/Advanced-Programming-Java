package servlets;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;
import server.RequestParser.RequestInfo;
import graph.TopicManagerSingleton;
import graph.Topic;
import graph.Message;
import graph.Node;
import graph.Graph;

public class TopicDisplayer implements Servlet {
    private final String filePath = "../html_files/table.html";

    @Override
    public void handle(RequestInfo ri, OutputStream toClient) throws IOException {
        if (!"GET".equals(ri.getHttpCommand())) {
            sendResponse(toClient, "HTTP/1.1 405 Method Not Allowed\r\n\r\n");
            return;
        }

        Map<String, String> parameters = ri.getParameters();
        String topicName = parameters.get("topic");
        String messageContent = parameters.get("message");

        if (topicName != null && messageContent != null) {
            Topic topic = TopicManagerSingleton.get().getTopic(topicName);
            Message message = new Message(messageContent);
            topic.publish(message);
        }

        // Create Graph instance
        Graph graph = new Graph();
        graph.createFromTopics();

        StringBuilder html = new StringBuilder();
        html.append("<html><body>\n");
        html.append("<h1>Topic Information</h1>\n");
        html.append("<table border='1'><tr><th>Topic</th><th>Message</th></tr>\n");

        for (Node node : graph) {
            if (node.getName().charAt(0) == 'T'){
                html.append("<tr><td>").append(node.getName()).append("</td>\n");
                if (node.getMessage() != null)
                    html.append("<td>").append(node.getMessage().asDouble).append("</td></tr>\n");
                else{
                    html.append("<td>").append("null value").append("</td></tr>\n");
                }
            }
        }

        html.append("</table></body></html>");
    
        String htmlContent = html.toString();

        // Remove existing content
        File file = new File(this.filePath);
        if (file.exists()) {
            file.delete();
        }

        // Write new content
        try {
            FileWriter myWriter = new FileWriter(this.filePath);
            myWriter.write(htmlContent);
            myWriter.close();
            System.out.println("Successfully wrote to the file.");
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }

        String response = "HTTP/1.1 205 Reset Content\r\n" +
                    "\r\n";

        sendResponse(toClient, response);
    }

    private void sendResponse(OutputStream toClient, String response) throws IOException {
        toClient.write(response.getBytes());
        toClient.flush();
    }

    @Override
    public void close() throws IOException {
        // Remove existing content
        File file = new File(this.filePath);
        if (file.exists()) {
            file.delete();
        }
    }
}