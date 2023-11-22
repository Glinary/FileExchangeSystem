package FileExchangeSystem.src;
import java.net.ServerSocket;
import java.net.Socket;

public class FileServer {
    private int port;

    public FileServer() {
        this.port = 50;
    }

    private void connect() {
        try {
            System.out.println("Waiting for client...");
            ServerSocket ss = new ServerSocket(this.port);
            Socket soc = ss.accept();
            System.out.println("Connection established");

            //create new thread to handle each connection somewhere here
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
