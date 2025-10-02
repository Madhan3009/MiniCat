import Request.Request;
import Request.RequestImpl;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;

public class Service implements Runnable{
    private final Socket clientSocket;
    private final InputStream inputStream;
    public Service(Socket clientSocket) throws IOException {
        this.clientSocket = clientSocket;
        this.inputStream = clientSocket.getInputStream();
    }

    public void run(){
        try{
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            try {
                Request request = new RequestImpl(bufferedReader);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        } catch (RuntimeException e) {
            throw new RuntimeException(e);
        }
    }
}
