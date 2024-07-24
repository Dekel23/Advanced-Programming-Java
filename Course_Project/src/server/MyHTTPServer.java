package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import server.RequestParser.RequestInfo;
import servlets.Servlet;

/**
 * An HTTP server implementation that handles incoming HTTP requests.
 * The server supports handling multiple HTTP commands (GET, POST, DELETE) and 
 * utilizes a thread pool to manage concurrent client connections.
 */
public class MyHTTPServer extends Thread implements HTTPServer {
    private final int port;
    private final int maxThreads;
    private final ExecutorService threadPool;
    private final ConcurrentHashMap<String, Servlet> getServlets = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, Servlet> postServlets = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, Servlet> deleteServlets = new ConcurrentHashMap<>();
    private volatile boolean running = true;
    private ServerSocket serverSocket;

    /**
     * Constructs a new {@code MyHTTPServer} instance.
     *
     * @param port the port number on which the server will listen for incoming connections
     * @param maxThreads the maximum number of threads to use for handling client requests
     */
    public MyHTTPServer(int port, int maxThreads) {
        this.port = port;
        this.maxThreads = maxThreads;
        this.threadPool = Executors.newFixedThreadPool(this.maxThreads);
    }

    /**
     * Adds a servlet for a specific HTTP command and URI.
     * The servlet will handle requests matching the specified command and URI.
     *
     * @param httpCommand the HTTP command (e.g., GET, POST, DELETE)
     * @param uri the URI for which the servlet should handle requests
     * @param s the {@link Servlet} to handle requests
     */
    @Override
    public void addServlet(String httpCommand, String uri, Servlet s) {
        switch (httpCommand) {
            case "GET":
                getServlets.put(uri, s);
                break;
            case "POST":
                postServlets.put(uri, s);
                break;
            case "DELETE":
                deleteServlets.put(uri, s);
                break;
            default:
                throw new IllegalArgumentException("Unsupported HTTP command: " + httpCommand);
        }
    }

    /**
     * Removes a servlet for a specific HTTP command and URI.
     *
     * @param httpCommand the HTTP command (e.g., GET, POST, DELETE)
     * @param uri the URI for which the servlet should be removed
     */
    @Override
    public void removeServlet(String httpCommand, String uri) {
        switch (httpCommand.toUpperCase()) {
            case "GET":
                getServlets.remove(uri);
                break;
            case "POST":
                postServlets.remove(uri);
                break;
            case "DELETE":
                deleteServlets.remove(uri);
                break;
            default:
                throw new IllegalArgumentException("Unsupported HTTP command: " + httpCommand);
        }
    }

    /**
     * Starts the server in a separate thread.
     */
    @Override
    public void start() {
        super.start();
    }

    /**
     * Stops the server and cleans up resources.
     *
     * @throws IOException if an I/O error occurs while closing resources
     */
    @Override
    public void close() throws IOException {
        running = false;
        // Close all servlets
        for (Servlet servlet: this.getServlets.values()){
            servlet.close();
        }
        for (Servlet servlet: this.postServlets.values()){
            servlet.close();
        }
        for (Servlet servlet: this.deleteServlets.values()){
            servlet.close();
        }
        // Close all threads
        threadPool.shutdown();
        try {
            if (!threadPool.awaitTermination(60, TimeUnit.SECONDS)) {
                threadPool.shutdownNow();
            }
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }
        } catch (IOException | InterruptedException e) {
            threadPool.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }

    /**
     * The main server loop that accepts client connections and handles them.
     */
    @Override
    public void run() {
        try (ServerSocket server = new ServerSocket(port)) {
            serverSocket = server;
            while (running) {
                try {
                    Socket clientSocket = server.accept();
                    this.threadPool.submit(() -> handleClient(clientSocket));
                } catch (IOException e) {
                    if (running) {
                        e.printStackTrace();
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Handles an individual client connection.
     * Parses the HTTP request, finds the appropriate servlet, and handles the request.
     *
     * @param clientSocket the {@link Socket} representing the client connection
     */
    private void handleClient(Socket clientSocket) {
        try (OutputStream out = clientSocket.getOutputStream();
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))) {
            
            RequestInfo requestInfo = RequestParser.parseRequest(in);
            Servlet servlet = findServlet(requestInfo.getHttpCommand(), requestInfo.getUri());
            
            if (servlet != null) {
                servlet.handle(requestInfo, out);
            } else {
                String response = "HTTP/1.1 404 Not Found\r\n\r\n";
                out.write(response.getBytes());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Finds the most appropriate servlet for the given HTTP command and URI.
     * The servlet is selected based on the longest matching URI.
     *
     * @param httpCommand the HTTP command (e.g., GET, POST, DELETE)
     * @param uri the URI of the request
     * @return the {@link Servlet} that matches the request, or {@code null} if no matching servlet is found
     */
    private Servlet findServlet(String httpCommand, String uri) {
        ConcurrentHashMap<String, Servlet> servletMap;
        switch (httpCommand.toUpperCase()) {
            case "GET":
                servletMap = getServlets;
                break;
            case "POST":
                servletMap = postServlets;
                break;
            case "DELETE":
                servletMap = deleteServlets;
                break;
            default:
                return null;
        }

        String longestMatch = "";
        Servlet matchedServlet = null;
        for (String key : servletMap.keySet()) {
            if (uri.startsWith(key) && key.length() > longestMatch.length()) {
                longestMatch = key;
                matchedServlet = servletMap.get(key);
            }
        }
        return matchedServlet;
    }
}