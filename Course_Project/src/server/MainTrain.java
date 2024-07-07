package server;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import servlets.Servlet;
import server.RequestParser.RequestInfo;

public class MainTrain {
    

    private static void testParseRequest() {
        // Test data
        String request = "GET /api/resource?id=123&name=test HTTP/1.1\n" +
                            "Host: example.com\n" +
                            "Content-Length: 5\n"+
                            "\n" +
                            "filename=\"hello_world.txt\"\n"+
                            "\n" +
                            "hello world!\n"+
                            "\n" ;

        BufferedReader input=new BufferedReader(new InputStreamReader(new ByteArrayInputStream(request.getBytes())));
        try {
            RequestInfo requestInfo = RequestParser.parseRequest(input);

            // Test HTTP command
            if (!requestInfo.getHttpCommand().equals("GET")) {
                System.out.println("HTTP command test failed (-5)");
            }

            // Test URI
            if (!requestInfo.getUri().equals("/api/resource?id=123&name=test")) {
                System.out.println("URI test failed (-5)");
            }

            // Test URI segments
            String[] expectedUriSegments = {"api", "resource"};
            if (!Arrays.equals(requestInfo.getUriSegments(), expectedUriSegments)) {
                System.out.println("URI segments test failed (-5)");
                for(String s : requestInfo.getUriSegments()){
                    System.out.println(s);
                }
            } 
            // Test parameters
            Map<String, String> expectedParams = new HashMap<>();
            expectedParams.put("id", "123");
            expectedParams.put("name", "test");
            expectedParams.put("filename","\"hello_world.txt\"");
            if (!requestInfo.getParameters().equals(expectedParams)) {
                System.out.println("Parameters test failed (-5)");
            }

            // Test content
            byte[] expectedContent = "hello world!\n".getBytes();
            if (!Arrays.equals(requestInfo.getContent(), expectedContent)) {
                System.out.println("Content test failed (-5)");
            } 
            input.close();
        } catch (IOException e) {
            System.out.println("Exception occurred during parsing: " + e.getMessage() + " (-5)");
        }        
    }

    public static void testServer() throws Exception {
        // Create and start the server
        int port = 8880;
        int maxThreads = 10;
        MyHTTPServer server = new MyHTTPServer(port, maxThreads);

        // Add test servlets using anonymous classes
        server.addServlet("GET", "/hello", new Servlet() {
            @Override
            public void handle(RequestInfo ri, OutputStream toClient) throws IOException {
                String response = "HTTP/1.1 200 OK\nContent-Type: text/plain\n\nHello, World!";
                toClient.write(response.getBytes());
            }

            @Override
            public void close() throws IOException {}
        });
        
        server.addServlet("POST", "/echo", new Servlet() {
            @Override
            public void handle(RequestInfo ri, OutputStream toClient) throws IOException {
                String content = new String(ri.getContent());
                String response = "HTTP/1.1 200 OK\nContent-Type: text/plain\n\n" + content;
                toClient.write(response.getBytes());
            }

            @Override
            public void close() throws IOException {}
        });
        
        server.start();
        
        Thread.sleep(1000);
        
        try {
            // Test GET request
            String getResponse = sendRequest("GET", "/hello", "");
            assert getResponse.contains("Hello, World!") : "GET request failed";
            System.out.println("GET test passed");
            
            // Test POST request
            String postData = "Hello, Server!";
            String postResponse = sendRequest("POST", "/echo", postData);
            assert postResponse.contains(postData) : "POST request failed";
            System.out.println("POST test passed");
            
            // Test non-existent endpoint
            String notFoundResponse = sendRequest("GET", "/nonexistent", "");
            assert notFoundResponse.contains("404 Not Found") : "404 Not Found test failed";
            System.out.println("404 Not Found test passed");
            
        } finally {
            // Stop the server
            server.close();
        }
        
        System.out.println("All tests passed successfully!");
    }

    private static String sendRequest(String method, String path, String body) throws Exception {
        String request = method + " " + path + " HTTP/1.1\n" +
                            "Host: localhost:8080\n" +
                            "Content-Length: " + body.length() + "\n" +
                            "\n" +
                            body +
                            "\n";
        
        try (Socket socket = new Socket("localhost", 8880);
            OutputStream out = socket.getOutputStream();
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
            
            out.write(request.getBytes());
            out.flush();
            
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = in.readLine()) != null) {
                response.append(line).append("\n");
            }
            
            return response.toString();
        }
    }
    
    public static void main(String[] args) {
        testParseRequest(); // 40 points
        try{
            testServer(); // 60
        }catch(Exception e){
            System.out.println("your server throwed an exception (-60)");
        }
        System.out.println("done");
    }

}
