package servlets;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import server.RequestParser.RequestInfo;

public class HtmlLoader implements Servlet {
    private final String htmlDirectory;

    public HtmlLoader(String htmlDirectory) {
        this.htmlDirectory = htmlDirectory;
    }

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

    private void sendErrorResponse(OutputStream toClient, String message) throws IOException {
        String errorContent = "<html><body><h1>404 Not Found</h1><p>" + message + "</p></body></html>";
        String response = "HTTP/1.1 404 Not Found\r\n" +
                          "Content-Type: text/html\r\n" +
                          "Content-Length: " + errorContent.length() + "\r\n" +
                          "\r\n" +
                          errorContent;
        sendResponse(toClient, response);
    }

    private void sendResponse(OutputStream toClient, String response) throws IOException {
        toClient.write(response.getBytes());
        toClient.flush();
    }

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

    @Override
    public void close() throws IOException {
        // No resources to close
    }
}