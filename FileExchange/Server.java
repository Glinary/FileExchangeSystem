package FileExchange;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {

    private ServerSocket serverSocket; // responsible for listening for incoming clients and create a socket object to communicate with them

    public Server(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
    }

    public void startServer(){

        try {

            while(!serverSocket.isClosed()){

                Socket socket = serverSocket.accept();  // halted here until client connects and returns socket object to communicate with client
                System.out.println("A new client has connected!");

                ClientHandler cliHandler =  new ClientHandler(socket);
                Thread thread = new Thread(cliHandler);
                thread.start();
            }
        } catch (IOException e){

        }
    }


    public void closeServerSocket(){
        try {
            if (serverSocket != null){
                serverSocket.close();
            }
        } catch (IOException e){
            e.printStackTrace();
        }
    }


    public static void main(String[] args) throws IOException {
        int port = 9000;

        // listen to port
        ServerSocket serverSocket = new ServerSocket(port);
        Server server = new Server(serverSocket);
        server.startServer();

    }
}

