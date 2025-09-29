import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {
    private static final int PORT = 5000;
    private static final int THREAD_POOL_SIZE = 10;

    public static void main(String[] args) {
        // Create static directory if it doesn't exist
        createStaticDirectory();

        ExecutorService executorService = Executors.newFixedThreadPool(THREAD_POOL_SIZE);

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Minicat started on port " + PORT);
            System.out.println("Visit http://localhost:" + PORT + " in your browser.");

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Client connected!");
                Service service = new Service(clientSocket);
                executorService.submit(service);
            }
        } catch (IOException e) {
            System.err.println("Server error: " + e.getMessage());
            e.printStackTrace();
        } finally {
            executorService.shutdown();
        }
    }

    private static void createStaticDirectory() {
        File staticDir = new File("static");
        if (!staticDir.exists() && !staticDir.mkdir()) {
            System.err.println("Warning: Could not create static directory");
        }
    }
}