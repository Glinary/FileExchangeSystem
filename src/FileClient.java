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

    public void displayFailedConnection() {
        System.out.println("""
                Error: Connection to the Server has failed!
                Please check IP Address and Port Number.
                """);
    }

    public void displayFailedDisconnection() {
        System.out.println("""
                Error: Disconnection failed. Please connect
                to the server first.
                """);
    }

    public void displayFailedRegistration() {
        System.out.println("""
                Error: Registration failed. Handle or alias
                already exists
                """);
    }

    public void displayFailedSendDNEClient() {
        System.out.println("""
                Error: File not found.
                """);
    }

    public void displayFailedSendDNEServer() {
        System.out.println("""
                Error: File not found in the server.
                """);
    }

    public void displayFailedSyntax() {
        System.out.println("""
                Error: Command not found.
                """);
    }

    public void displayInvalid() {
        System.out.println("""
                Error: Command parameters do not match
                or is not allowed.
                """);
    }
}
