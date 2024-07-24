package servlets;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;
import server.RequestParser.RequestInfo;
import graph.TopicManagerSingleton;
import graph.Topic;
import graph.Message;

/**
 * Servlet that handles HTTP requests related to displaying and managing topics.
 * This servlet publishes messages to specified topics based on the request parameters.
 */
public class TopicDisplayer implements Servlet {

    /**
     * Constructs a {@code TopicDisplayer} instance.
     * <p>
     * This default constructor is provided to allow instantiation of the servlet.
     * </p>
     */
    public TopicDisplayer() {
        // Default constructor
    }

    /**
     * Handles an HTTP request by publishing a message to a specified topic if both the topic and message are provided.
     * Responds with a 200 OK status for successful operations.
     *
     * @param ri the request information containing details of the HTTP request
     * @param toClient the output stream to send the response to the client
     * @throws IOException if an I/O error occurs while handling the request or sending the response
     */
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

    /**
     * Sends an HTTP response to the client.
     *
     * @param toClient the output stream to send the response to the client
     * @param response the HTTP response message
     * @throws IOException if an I/O error occurs while sending the response
     */
    private void sendResponse(OutputStream toClient, String response) throws IOException {
        toClient.write(response.getBytes());
        toClient.flush();
    }

    /**
     * Closes the servlet and performs cleanup operations.
     * In this implementation, it clears all topics from the TopicManagerSingleton.
     *
     * @throws IOException if an I/O error occurs while closing resources
     */
    @Override
    public void close() throws IOException {
        TopicManagerSingleton.get().clear();
    }
}
