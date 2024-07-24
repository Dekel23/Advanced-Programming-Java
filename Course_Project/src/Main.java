import server.*;
import servlets.*;

public class Main {
    public static void main(String[] args) throws Exception{
        HTTPServer server=new MyHTTPServer(8080,5); // 8080 defines the port
        server.addServlet("GET", "/publish", new TopicDisplayer());
        server.addServlet("POST", "/upload", new ConfLoader("../html_files"));
        server.addServlet("GET", "/app/", new HtmlLoader("../html_files"));
        server.start();
        System.in.read();
        server.close();
        System.out.println("done");
    }
}