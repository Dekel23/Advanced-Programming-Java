package servlets;

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

    @Override
    public void handle(RequestInfo ri, OutputStream toClient) throws IOException {
        if (!"GET".equals(ri.getHttpCommand())) {
            sendResponse(toClient, "HTTP/1.1 405 Method Not Allowed\r\n\r\n");
            return;
        }

        // Create Graph instance
        Graph graph = new Graph();
        graph.createFromTopics();

        Map<String, String> parameters = ri.getParameters();
        String topicName = parameters.get("topic");
        String messageContent = parameters.get("message");

        if (topicName != null && messageContent != null) {
            Topic topic = TopicManagerSingleton.get().getTopic(topicName);
            Message message = new Message(messageContent);
            topic.publish(message);
        }

        StringBuilder html = new StringBuilder();
        html.append("<html><body>");
        html.append("<h1>Topic Information</h1>");
        html.append("<table border='1'><tr><th>Topic</th><th>Message</th></tr>");

        for (Node node : graph) {
            if (node.getName().charAt(0) == 'T'){
                html.append("<tr><td>").append(node.getName()).append("</td>");
                html.append("<td>").append(node.getMessage()).append("</td></tr>");
            }
        }

        html.append("</table></body></html>");
    
        String htmlContent = html.toString();
        String response = String.format("HTTP/1.1 200 OK\r\nContent-Type: text/html\r\nContent-Length: %d\r\n\r\n%s",
                                        htmlContent.length(), htmlContent);

        sendResponse(toClient, response);
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