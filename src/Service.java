import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class Service implements Runnable {
    private final Socket clientSocket;

    public Service(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }

    @Override
    public void run() {
        try {
            handleRequest();
        } catch (Exception e) {
            System.err.println("Error handling request: " + e.getMessage());
            e.printStackTrace();
        } finally {
            closeClientSocket();
        }
    }

    private void handleRequest() throws IOException {
        try (InputStream input = clientSocket.getInputStream();
             OutputStream output = clientSocket.getOutputStream();
             BufferedReader reader = new BufferedReader(new InputStreamReader(input))) {

            System.out.println(Thread.currentThread().getName() + " processing request");

            RequestHandler requestHandler = new RequestHandler(reader);
            requestHandler.processRequest();

            String responseHeaders = requestHandler.getResponseHeaders();
            byte[] bodyBytes = requestHandler.getBodyBytes();
            System.out.println(responseHeaders);
            output.write(responseHeaders.getBytes(StandardCharsets.UTF_8));
            output.write(bodyBytes);
            output.flush();

            System.out.println(Thread.currentThread().getName() + " finished at: " + System.currentTimeMillis());
        }
    }

    private void closeClientSocket() {
        try {
            if (clientSocket != null && !clientSocket.isClosed()) {
                clientSocket.close();
            }
        } catch (IOException e) {
            System.err.println("Error closing client socket: " + e.getMessage());
        }
    }
}