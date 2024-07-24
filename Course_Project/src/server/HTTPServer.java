package server;

import java.io.IOException;

import servlets.Servlet;

/**
 * An interface representing an HTTP server.
 * It provides methods for adding and removing servlets, starting and stopping the server,
 * and handling HTTP requests.
 */
public interface HTTPServer extends Runnable {

    /**
     * Adds a servlet for a specific HTTP command and URI.
     * The servlet will handle requests matching the specified command and URI.
     *
     * @param httpCommand the HTTP command (e.g., GET, POST, DELETE)
     * @param uri the URI for which the servlet should handle requests
     * @param s the {@link Servlet} to handle requests
     */
    void addServlet(String httpCommand, String uri, Servlet s);

    /**
     * Removes a servlet for a specific HTTP command and URI.
     *
     * @param httpCommand the HTTP command (e.g., GET, POST, DELETE)
     * @param uri the URI for which the servlet should be removed
     */
    void removeServlet(String httpCommand, String uri);

    /**
     * Starts the server.
     * This method should be called to begin accepting and handling client connections.
     */
    void start();

    /**
     * Stops the server and cleans up resources.
     *
     * @throws IOException if an I/O error occurs while closing resources
     */
    void close() throws IOException;
}