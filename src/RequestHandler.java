import java.io.BufferedReader;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class RequestHandler {
    private final BufferedReader reader;
    private byte[] bodyBytes;
    private String responseHeaders;

    public RequestHandler(BufferedReader reader) {
        this.reader = reader;
    }

    public void processRequest() throws IOException {
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

        String method = parts[0];
        String path = parts[1];

        serveFile(path);
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