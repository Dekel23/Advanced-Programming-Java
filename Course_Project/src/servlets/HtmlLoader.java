package servlets;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import server.RequestParser.RequestInfo;

/**
 * Handles HTTP GET requests for serving static HTML, CSS, and JavaScript files from a specified directory.
 */
public class HtmlLoader implements Servlet {

    private final String htmlDirectory;

    /**
     * Constructs an HtmlLoader instance with the specified directory for serving HTML files.
     *
     * @param htmlDirectory the directory where HTML files are stored
     */
    public HtmlLoader(String htmlDirectory) {
        this.htmlDirectory = htmlDirectory;
    }

    /**
     * Handles HTTP GET requests to retrieve and serve static files.
     *
     * @param ri the request information containing URI segments
     * @param toClient the output stream to send the response to the client
     * @throws IOException if an I/O error occurs while handling the request
     */
    @Override
    public void handle(RequestInfo ri, OutputStream toClient) throws IOException {
        if (!"GET".equals(ri.getHttpCommand())) {
            sendResponse(toClient, "HTTP/1.1 405 Method Not Allowed\r\n\r\n");
            return;
        }

        String[] uriSegments = ri.getUriSegments();
        if (uriSegments.length < 2) {
            sendResponse(toClient, "HTTP/1.1 400 Bad Request\r\n\r\nInvalid request");
            return;
        }

        String fileName = uriSegments[1];
        Path filePath = Paths.get(htmlDirectory, fileName);

        if (Files.exists(filePath) && !Files.isDirectory(filePath)) {
            byte[] content = Files.readAllBytes(filePath);
            String contentType = getContentType(fileName);

            String response = "HTTP/1.1 200 OK\r\n" +
                              "Content-Type: " + contentType + "\r\n" +
                              "Content-Length: " + content.length + "\r\n" +
                              "\r\n";

            toClient.write(response.getBytes());
            toClient.write(content);
            toClient.flush();
        } else {
            sendErrorResponse(toClient, "File not found: " + fileName);
        }
    }

    /**
     * Sends an HTTP 404 error response when a requested file is not found.
     *
     * @param toClient the output stream to send the response to
     * @param message the error message to include in the response body
     * @throws IOException if an I/O error occurs while sending the response
     */
    private void sendErrorResponse(OutputStream toClient, String message) throws IOException {
        String errorContent = "<html><body><h1>404 Not Found</h1><p>" + message + "</p></body></html>";
        String response = "HTTP/1.1 404 Not Found\r\n" +
                          "Content-Type: text/html\r\n" +
                          "Content-Length: " + errorContent.length() + "\r\n" +
                          "\r\n" +
                          errorContent;
        sendResponse(toClient, response);
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
     * Determines the content type of a file based on its extension.
     *
     * @param fileName the name of the file
     * @return the MIME type of the file
     */
    private String getContentType(String fileName) {
        if (fileName.endsWith(".html") || fileName.endsWith(".htm")) {
            return "text/html";
        } else if (fileName.endsWith(".css")) {
            return "text/css";
        } else if (fileName.endsWith(".js")) {
            return "application/javascript";
        } else {
            return "application/octet-stream";
        }
    }

    /**
     * Closes any resources used by the HtmlLoader. This implementation does not require any resources to close.
     *
     * @throws IOException if an I/O error occurs while closing resources
     */
    @Override
    public void close() throws IOException {
        // No resources to close
    }
}