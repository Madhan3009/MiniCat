import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.Provider;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {
    private static final int PORT = 5000;
    private static final int THREADPOOLSIZE = 10;
    public static void main(String[] args){
        ExecutorService executorService = Executors.newFixedThreadPool(THREADPOOLSIZE);
        try{
            ServerSocket socket = new ServerSocket(PORT);
            System.out.println("Visit http://localhost:" + PORT + " in your browser.");
            while(true){
                Socket clientSocket = socket.accept();
                Service service = new Service(clientSocket);
                executorService.submit(service);
            }
        } catch (IOException e) {
            System.err.println("Server error: " + e.getMessage());
            e.printStackTrace();
        }
        finally {
            executorService.shutdown();
        }
    }
}
