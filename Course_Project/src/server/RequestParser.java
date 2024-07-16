package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

 
public class RequestParser {

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
        
         // Check if http is a handled http command
        if (!(httpMethod.equals("GET") || httpMethod.equals("POST") || httpMethod.equals("DELETE")))
            throw new IllegalArgumentException("Invalid HTTP command: " + httpMethod);

        String[] uriSegments;
        Map<String, String> parameters = new HashMap<>(); // Hash map for parameters values
        int questionMarkIndex = httpURI.indexOf('?'); // Find URI "?" index
        if (questionMarkIndex != -1){
            uriSegments = httpURI.substring(1, questionMarkIndex).split("/"); // Split segments by "/"
            String paramString = httpURI.substring(questionMarkIndex + 1);
            String[] paramPairs = paramString.split("&"); // Split parameters by "&"
            for (String pair: paramPairs){
                String[] keyValue = pair.split("="); // App parameter and value to map
                if (keyValue.length != 2)
                    throw new IllegalArgumentException("Invalid HTTP parameter" + pair);
                parameters.put(keyValue[0], keyValue[1]);
            }
        }
        else{
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
                throw new IllegalArgumentException("Invalid HTTP parameter" + line);
            parameters.put(keyValue[0], keyValue[1].substring(1, keyValue[1].length() - 1)); // Add parameter to map
        }

        // Continue until content part
        while (reader.ready() && (line = reader.readLine()) != null && !line.isEmpty()) {}

        // Read until finished or line with "------"
        StringBuilder bodyBuilder = new StringBuilder();
        while (reader.ready() && (line = reader.readLine()) != null && !line.isEmpty() && !line.contains("------")) {
            bodyBuilder.append(line + "\n");
        }

        // Convert content to byte array
        byte[] content = bodyBuilder.toString().getBytes();

        return new RequestInfo(httpMethod, httpURI, uriSegments, parameters, content);
    }

    // RequestInfo given internal class
    public static class RequestInfo {
        private final String httpCommand;
        private final String uri;
        private final String[] uriSegments;
        private final Map<String, String> parameters;
        private final byte[] content;

        public RequestInfo(String httpCommand, String uri, String[] uriSegments, Map<String, String> parameters, byte[] content) {
            this.httpCommand = httpCommand;
            this.uri = uri;
            this.uriSegments = uriSegments;
            this.parameters = parameters;
            this.content = content;
        }

        public String getHttpCommand() {
            return httpCommand;
        }

        public String getUri() {
            return uri;
        }

        public String[] getUriSegments() {
            return uriSegments;
        }

        public Map<String, String> getParameters() {
            return parameters;
        }

        public byte[] getContent() {
            return content;
        }

        public void print() {
            System.out.println("command: " + httpCommand);
            System.out.println("uri: " + uri);
            System.out.println("segments: " + uriSegments);
            System.out.println("param: " + parameters.toString());
            System.out.println("content: " + content.toString());
        }
    }
}
