package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * A utility class for parsing HTTP requests from a {@link BufferedReader}.
 * This class is used to extract the HTTP method, URI, parameters, and content from incoming requests.
 */
public class RequestParser {

    /**
     * Constructs a {@code RequestParser} instance.
     * <p>
     * This default constructor is provided to allow instantiation of the class.
     * </p>
     */
    public RequestParser() {
        // Default constructor
    }

    /**
     * Parses an HTTP request from the provided {@link BufferedReader}.
     * The request line is expected to be in the format: &lt;httpMethod&gt; &lt;httpURI&gt; &lt;httpVersion&gt;.
     * Parameters and content are extracted based on the request type and format.
     *
     * @param reader the {@link BufferedReader} to read the HTTP request from
     * @return a {@link RequestInfo} object containing the parsed request information
     * @throws IOException if an I/O error occurs while reading from the reader
     * @throws IllegalArgumentException if the request is malformed or invalid
     */
    public static RequestInfo parseRequest(BufferedReader reader) throws IOException {
        if (reader == null)
            throw new IllegalArgumentException("BufferedReader cannot be null");
        
        String line = reader.readLine();
        if (line == null || line.isEmpty())
            throw new IllegalArgumentException("Request is empty");
        
        String[] parts = line.split(" "); // Split first line to httpCommand, URI and version
        if (parts.length != 3)
            throw new IllegalArgumentException("Invalid HTTP request line: " + line);

        String httpMethod = parts[0];
        String httpURI = parts[1];
        
        // Check if httpMethod is a handled HTTP command
        if (!(httpMethod.equals("GET") || httpMethod.equals("POST") || httpMethod.equals("DELETE")))
            throw new IllegalArgumentException("Invalid HTTP command: " + httpMethod);

        String[] uriSegments;
        Map<String, String> parameters = new HashMap<>(); // Hash map for parameter values
        int questionMarkIndex = httpURI.indexOf('?'); // Find URI "?" index
        if (questionMarkIndex != -1) {
            uriSegments = httpURI.substring(1, questionMarkIndex).split("/"); // Split segments by "/"
            String paramString = httpURI.substring(questionMarkIndex + 1);
            String[] paramPairs = paramString.split("&"); // Split parameters by "&"
            for (String pair : paramPairs) {
                String[] keyValue = pair.split("="); // App parameter and value to map
                if (keyValue.length != 2)
                    throw new IllegalArgumentException("Invalid HTTP parameter: " + pair);
                String key = URLDecoder.decode(keyValue[0], StandardCharsets.UTF_8);
                String value = URLDecoder.decode(keyValue[1], StandardCharsets.UTF_8);
                parameters.put(key, value);
            }
        } else {
            uriSegments = httpURI.substring(1).split("/"); // If no "?" then split segments by "/"
        }
        
        // Continue until header ends
        while (reader.ready() && (line = reader.readLine()) != null && !line.isEmpty()) {}
        // Continue until lines with "-----" ends
        while (reader.ready() && (line = reader.readLine()) != null && !line.contains("------")) {}

        // While there are more lines and new line has "="
        while (reader.ready() && (line = reader.readLine()) != null && !line.isEmpty() && line.contains("=")) {
            if (line.contains(" filename=")) { // If there is parameter filename
                String[] params = line.split(" ");
                line = line.split(" ")[params.length - 1]; // Set line to the last part
            }
                
            String[] keyValue = line.split("="); // Split the line by "="
            if (keyValue.length != 2)
                throw new IllegalArgumentException("Invalid HTTP parameter: " + line);
            parameters.put(keyValue[0], keyValue[1].substring(1, keyValue[1].length() - 1)); // Add parameter to map
        }

        // Continue until content part
        while (reader.ready() && (line = reader.readLine()) != null && !line.isEmpty()) {}

        // Read until finished or line with "------"
        StringBuilder bodyBuilder = new StringBuilder();
        while (reader.ready() && (line = reader.readLine()) != null && !line.isEmpty() && !line.contains("------")) {
            bodyBuilder.append(line).append("\n");
        }

        // Convert content to byte array
        byte[] content = bodyBuilder.toString().getBytes();

        return new RequestInfo(httpMethod, httpURI, uriSegments, parameters, content);
    }

    /**
     * Represents the information extracted from an HTTP request.
     * Contains details such as the HTTP command, URI, URI segments, parameters, and request content.
     */
    public static class RequestInfo {
        private final String httpCommand;
        private final String uri;
        private final String[] uriSegments;
        private final Map<String, String> parameters;
        private final byte[] content;

        /**
         * Constructs a new {@code RequestInfo} instance with the provided details.
         *
         * @param httpCommand the HTTP command (e.g., GET, POST, DELETE)
         * @param uri the URI of the request
         * @param uriSegments the segments of the URI
         * @param parameters the parameters extracted from the request
         * @param content the content of the request
         */
        public RequestInfo(String httpCommand, String uri, String[] uriSegments, Map<String, String> parameters, byte[] content) {
            this.httpCommand = httpCommand;
            this.uri = uri;
            this.uriSegments = uriSegments;
            this.parameters = parameters;
            this.content = content;
        }

        /**
         * Gets the HTTP command (e.g., GET, POST, DELETE) of the request.
         *
         * @return the HTTP command
         */
        public String getHttpCommand() {
            return httpCommand;
        }

        /**
         * Gets the URI of the request.
         *
         * @return the URI
         */
        public String getUri() {
            return uri;
        }

        /**
         * Gets the segments of the URI.
         *
         * @return an array of URI segments
         */
        public String[] getUriSegments() {
            return uriSegments;
        }

        /**
         * Gets the parameters extracted from the request.
         *
         * @return a map of parameter names to values
         */
        public Map<String, String> getParameters() {
            return parameters;
        }

        /**
         * Gets the content of the request as a byte array.
         *
         * @return the request content
         */
        public byte[] getContent() {
            return content;
        }

        /**
         * Prints the details of the request to the standard output.
         * Useful for debugging purposes.
         */
        public void print() {
            System.out.println("command: " + httpCommand);
            System.out.println("uri: " + uri);
            System.out.println("segments: " + String.join(", ", uriSegments));
            System.out.println("parameters: " + parameters.toString());
            System.out.println("content: " + new String(content));
        }
    }
}