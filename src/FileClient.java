package FileExchangeSystem.src;
import java.net.Socket;

public class FileClient {
    private int port;
    private String host;

    public FileClient() {
        this.port = 50;
        this.host = "localhost";
    }

    public void connect() {
        try {
            System.out.println("Client started");
            Socket soc = new Socket(this.host, this.port);
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
