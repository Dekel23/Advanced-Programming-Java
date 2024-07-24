import server.*;
import servlets.*;

/**
 * The entry point for the HTTP server application.
 * This class initializes and starts the HTTP server, configures it with
 * various servlets, and handles server shutdown gracefully.
 */
public class Main {
    
    /**
     * The main method that starts the HTTP server.
     * <p>
     * This method creates an instance of {@link MyHTTPServer}, configures it with
     * different servlets for handling HTTP requests, and starts the server.
     * The server will continue running until the user presses Enter.
     * </p>
     * 
     * @param args Command-line arguments (not used in this implementation).
     * @throws Exception If an error occurs during server initialization or operation.
     */
    public static void main(String[] args) throws Exception {
        // Create an instance of MyHTTPServer on port 8080 with a thread pool size of 5
        HTTPServer server = new MyHTTPServer(8080, 5);
        
        // Add servlets to handle different HTTP requests
        server.addServlet("GET", "/publish", new TopicDisplayer());
        server.addServlet("POST", "/upload", new ConfLoader("../html_files"));
        server.addServlet("GET", "/app/", new HtmlLoader("../html_files"));
        
        // Start the server
        server.start();
        
        // Wait for user input to stop the server
        System.in.read();
        
        // Stop the server
        server.close();
        
        // Print a message indicating that the server has stopped
        System.out.println("done");
    }
}
