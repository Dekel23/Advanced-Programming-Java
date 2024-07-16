package servlets;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;
import server.RequestParser.RequestInfo;
import graph.TopicManagerSingleton;
import graph.Topic;
import graph.Message;

public class TopicDisplayer implements Servlet {

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

        String response = "HTTP/1.1 200 OK\r\n" +
                    "\r\n";

        sendResponse(toClient, response);
    }

    private void sendResponse(OutputStream toClient, String response) throws IOException {
        toClient.write(response.getBytes());
        toClient.flush();
    }

    @Override
    public void close() throws IOException {
        TopicManagerSingleton.get().clear();
    }
}