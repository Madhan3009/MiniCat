import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    public static void main(String[] args) {
        int port = 5000;

        // Create static directory if it doesn't exist
        File staticDir = new File("static");
        if (!staticDir.exists()) {
            staticDir.mkdir();
        }

        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Minicat started on port " + port);
            System.out.println("Visit http://localhost:" + port + " in your browser.");

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Client connected!");
                Service service = new Service(clientSocket);
                Thread thread = new Thread(service);
                thread.start();
            }
        } catch (IOException e) {
            System.err.println("Server error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}