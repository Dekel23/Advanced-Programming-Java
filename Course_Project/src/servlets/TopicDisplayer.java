package servlets;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;
import server.RequestParser.RequestInfo;
import graph.TopicManagerSingleton;
import graph.Topic;
import graph.Message;
import graph.Agent;

public class TopicDisplayer implements Servlet {
    private final TopicManagerSingleton.TopicManager topicManager = TopicManagerSingleton.get();

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
            Topic topic = topicManager.getTopic(topicName);
            Message message = new Message(messageContent);
            topic.publish(message);
        }

        StringBuilder htmlContent = new StringBuilder();
        htmlContent.append("<html><body>");
        htmlContent.append("<h1>Topic Information</h1>");
        htmlContent.append("<table border='1'><tr><th>Topic</th><th>Subscribers</th><th>Publishers</th></tr>");

        for (Topic topic : topicManager.getTopics()) {
            htmlContent.append("<tr><td>").append(topic.name).append("</td><td>");
            for (Agent sub : topic.getSubs()) {
                htmlContent.append(sub.getName()).append("<br>");
            }
            htmlContent.append("</td><td>");
            for (Agent pub : topic.getPubs()) {
                htmlContent.append(pub.getName()).append("<br>");
            }
            htmlContent.append("</td></tr>");
        }

        htmlContent.append("</table></body></html>");

        String response = "HTTP/1.1 200 OK\r\n" +
                          "Content-Type: text/html\r\n" +
                          "Content-Length: " + htmlContent.length() + "\r\n" +
                          "\r\n" +
                          htmlContent.toString();

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