import java.io.BufferedReader;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Locale;
import java.util.Map;

public class RequestHandler {
    private final BufferedReader reader;
    private byte[] bodyBytes;
    private String responseHeaders;
    private String method;
    private String path;
    private Map<String,String> requestHeaders;
    public RequestHandler(BufferedReader reader) {
        this.reader = reader;
    }

    public void processRequest() throws IOException {
       serveRequestLine();
       serveRequestHeaders();
       handleRequest();
    }

    private void handleRequest() throws IOException {
        switch (method.toUpperCase()){
            case "GET":
                serveGetRequest(path);
                break;
            case "HEAD":
                serveHeadRequest(path);
                break;
            default:
                sendError(405,"Method Not Allowed");
        }
    }

    private void serveHeadRequest(String path) throws IOException {
        serveFile(path);
        bodyBytes = new byte[0];
    }

    private void serveGetRequest(String path) throws IOException {
        serveFile(path);
    }

    public void serveRequestLine() throws IOException {
        String requestLine = reader.readLine();
        if (requestLine == null) {
            sendError(400, "Bad Request");
            return;
        }

        String[] parts = requestLine.split(" ");
        if (parts.length < 2) {
            sendError(400, "Bad Request");
            return;
        }

        method = parts[0];
        path = parts[1];

        serveFile(path);
    }
    public void serveRequestHeaders() throws IOException{
        String headerline;
        while((headerline = reader.readLine())!=null){
            if(headerline.isEmpty()){
                break;
            }
        }
        int index = headerline.indexOf(':');
        if(index>0){
            String name = headerline.substring(0,index).trim();
            String value = headerline.substring(index+1).trim();
            requestHeaders.put(name.toLowerCase(),value);
        }
        System.out.println(STR."Method: \{method}");
        System.out.println(STR."Host: \{requestHeaders.get("host")}");
        System.out.println(STR."User-agent: \{requestHeaders.get("user-agent")}");
    }

    private void serveFile(String path) throws IOException {
        Path base = Paths.get("static").toAbsolutePath().normalize();
        Path finalPath = base.resolve(path.substring(1)).normalize();

        // Prevent directory traversal attacks
        if (!finalPath.startsWith(base)) {
            sendError(403, "Forbidden");
            return;
        }

        if (Files.exists(finalPath) && Files.isRegularFile(finalPath)) {
            serveFileContent(finalPath);
        } else {
            sendError(404, "Not Found");
        }
    }

    private void serveFileContent(Path filePath) throws IOException {
        bodyBytes = Files.readAllBytes(filePath);
        String contentType = getContentType(filePath);

        responseHeaders = String.format(
                "HTTP/1.1 200 OK\r\n" +
                        "Content-Type: %s; charset=utf-8\r\n" +
                        "Content-Length: %d\r\n" +
                        "Connection: close\r\n" +
                        "\r\n",
                contentType, bodyBytes.length
        );
    }

    private void sendError(int statusCode, String message) {
        String responseBody = String.format("<h1>%d - %s</h1>", statusCode, message);
        bodyBytes = responseBody.getBytes(StandardCharsets.UTF_8);

        responseHeaders = String.format(
                "HTTP/1.1 %d %s\r\n" +
                        "Content-Type: text/html; charset=utf-8\r\n" +
                        "Content-Length: %d\r\n" +
                        "Connection: close\r\n" +
                        "\r\n",
                statusCode, message, bodyBytes.length
        );
    }

    private String getContentType(Path path) {
        String fileName = path.getFileName().toString().toLowerCase();

        if (fileName.endsWith(".html") || fileName.endsWith(".htm")) {
            return "text/html";
        } else if (fileName.endsWith(".css")) {
            return "text/css";
        } else if (fileName.endsWith(".js")) {
            return "application/javascript";
        } else if (fileName.endsWith(".png")) {
            return "image/png";
        } else if (fileName.endsWith(".jpg") || fileName.endsWith(".jpeg")) {
            return "image/jpeg";
        } else if (fileName.endsWith(".gif")) {
            return "image/gif";
        } else {
            return "text/plain";
        }
    }

    public String getResponseHeaders() {
        return responseHeaders;
    }

    public byte[] getBodyBytes() {
        return bodyBytes != null ? bodyBytes : new byte[0];
    }
}