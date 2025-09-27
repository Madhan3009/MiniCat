import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Service implements Runnable {
    private Socket clientSocket;

    Service(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }

    public void run() {
        try {
            InputStream input = clientSocket.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(input));
            String line = bufferedReader.readLine();
            OutputStream output = clientSocket.getOutputStream();

            System.out.println(Thread.currentThread().getName() + ", executing run() method!");

            String path = "/"; // Default path
            if (line != null) {
                String parts[] = line.split(" ");
                if (parts.length >= 2) {
                    path = parts[1];
                }
            }

            // Security: Ensure path doesn't escape the static directory
            Path base = Paths.get("static").toAbsolutePath().normalize();
            Path finalPath = base.resolve(path.substring(1)).normalize();

            // Prevent directory traversal attacks
            if (!finalPath.startsWith(base)) {
                sendError(output, 403, "Forbidden");
                return;
            }

            if (Files.exists(finalPath) && Files.isRegularFile(finalPath)) {
                // Serve the actual file content
                byte[] content = Files.readAllBytes(finalPath);
                String contentType = getContentType(finalPath);

                String responseHeaders =
                        "HTTP/1.1 200 OK\r\n" +
                                "Content-Type: " + contentType + "; charset=utf-8\r\n" +
                                "Content-Length: " + content.length + "\r\n" +
                                "Connection: close\r\n" +
                                "\r\n";

                output.write(responseHeaders.getBytes(StandardCharsets.UTF_8));
                output.write(content); // Send the actual file content
            } else {
                // File not found - send 404
                String responseBody = "<h1>404 - File Not Found</h1>";
                byte[] bodyBytes = responseBody.getBytes(StandardCharsets.UTF_8);

                String responseHeaders =
                        "HTTP/1.1 404 Not Found\r\n" +
                                "Content-Type: text/html; charset=utf-8\r\n" +
                                "Content-Length: " + bodyBytes.length + "\r\n" +
                                "Connection: close\r\n" +
                                "\r\n";

                output.write(responseHeaders.getBytes(StandardCharsets.UTF_8));
                output.write(bodyBytes);
            }

            output.flush();
            clientSocket.close();
            System.out.println(Thread.currentThread().getName() + " FINISHED at: " + System.currentTimeMillis());

        } catch (Exception e) {
            e.printStackTrace();
            try {
                // Try to send error response if possible
                OutputStream output = clientSocket.getOutputStream();
                sendError(output, 500, "Internal Server Error");
            } catch (IOException ex) {
                // Ignore if we can't send error
            }
        } finally {
            try {
                clientSocket.close();
            } catch (IOException e) {
                // Ignore close errors
            }
        }
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
        } else {
            return "text/plain";
        }
    }

    private void sendError(OutputStream output, int statusCode, String message) throws IOException {
        String responseBody = "<h1>" + statusCode + " - " + message + "</h1>";
        byte[] bodyBytes = responseBody.getBytes(StandardCharsets.UTF_8);

        String responseHeaders =
                "HTTP/1.1 " + statusCode + " " + message + "\r\n" +
                        "Content-Type: text/html; charset=utf-8\r\n" +
                        "Content-Length: " + bodyBytes.length + "\r\n" +
                        "Connection: close\r\n" +
                        "\r\n";

        output.write(responseHeaders.getBytes(StandardCharsets.UTF_8));
        output.write(bodyBytes);
        output.flush();
    }
}