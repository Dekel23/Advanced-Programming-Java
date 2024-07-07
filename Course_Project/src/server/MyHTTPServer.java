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

public class MyHTTPServer extends Thread implements HTTPServer {
    private final int port;
    private final int maxThreads;
    private final ExecutorService threadPool;
    private final ConcurrentHashMap<String, Servlet> getServlets = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, Servlet> postServlets = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, Servlet> deleteServlets = new ConcurrentHashMap<>();
    private volatile boolean running = true;
    private ServerSocket serverSocket;

    public MyHTTPServer(int port, int maxThreads) {
        this.port = port;
        this.maxThreads = maxThreads;
        this.threadPool = Executors.newFixedThreadPool(this.maxThreads);
    }

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

    @Override
    public void start() {
        super.start();
    }

    @Override
    public void close() {
        running = false;
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
