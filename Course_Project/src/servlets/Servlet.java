package servlets;

import java.io.IOException;
import java.io.OutputStream;
import server.RequestParser.RequestInfo;

/**
 * Defines the interface for handling HTTP requests in a servlet-like manner.
 */
public interface Servlet {

    /**
     * Handles an HTTP request by processing the request information and sending a response to the client.
     *
     * @param ri the request information containing details of the HTTP request
     * @param toClient the output stream to send the response to the client
     * @throws IOException if an I/O error occurs while handling the request or sending the response
     */
    void handle(RequestInfo ri, OutputStream toClient) throws IOException;

    /**
     * Closes any resources used by the servlet. Implementations should ensure that resources are properly released.
     *
     * @throws IOException if an I/O error occurs while closing resources
     */
    void close() throws IOException;
}
