package Client;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.SwingUtilities;
import javax.swing.plaf.basic.BasicInternalFrameTitlePane.SystemMenuBar;

public class Client {


    private Socket socket;
    private String lastCmd;
    private String host;
    private String name;
    private int port;
    // private Boolean joined = false;
    private Boolean registered = false;

    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;

    private DataInputStream dataInputStream;

    private Thread listenThread;


    public Boolean getRegistered() {
        return registered;
    }
    public void setRegistered(Boolean registered) {
        this.registered = registered;
    }

    public Thread getListenThread() {
        return listenThread;
    }
    
    // public Boolean getJoin() {
    //     return joined;
    // }
    // public void setJoin(Boolean join) {
    //     this.joined = join;
    // }

    public String getHost() {
        return host;
    }
    public void setHost(String host) {
        this.host = host;
    }
    public int getPort() {
        return port;
    }
    public void setPort(int port) {
        this.port = port;
    }
    public Socket getSocket() {
        return socket;
    }

    public String getName() {
        return name;
    }

    public void setName(String username) {
        this.name = username;
    }

    public String getLastCmd() {
        return lastCmd;
    }

    public void setLastCmd(String lastCmd) {
        this.lastCmd = lastCmd;
    }

    private MessageCallback messageCallback;

    public void setMessageCallback(MessageCallback callback) {
        this.messageCallback = callback;
    }


    public void setSocket(String ip, int port) throws UnknownHostException, IOException{
        Socket socket = new Socket(ip, port);
        this.socket = socket;
        this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())); // change later to input from user
        this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.dataInputStream = new DataInputStream(socket.getInputStream());
    }


    // ** Send Message to the server
    public void sendMessage(String message){
        try {
            
            bufferedWriter.write(message);
            bufferedWriter.newLine();
            bufferedWriter.flush();
            System.out.println("Message sent to server!");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void listenForMessage(){
        listenThread = new Thread(new Runnable(){
            @Override
            public void run(){
                
                try {

                    String message = bufferedReader.readLine();

                    if (messageCallback != null) {
                        messageCallback.onMessageReceived(message);
                    }        
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }); 
        listenThread.start();
    }


        public int receiveRegistrationStatus(){
        try {
            System.out.println("I entered rec reg client");
            int regStatus = dataInputStream.readInt();
            System.out.println("reg status client: " + regStatus);

            if (regStatus == 1){
                return 1;
            }
            else if (regStatus == 0){
                return 0;
            }
        } catch (Exception e){
            e.printStackTrace();
        }
        return 0;
    }


    public void receiveFile(String filePath) {
        String fpath =  extractFilename(filePath);


        try {
            // Receive the file size from the server
            long fileSize = dataInputStream.readLong();
            System.out.println("File size: " + fileSize);

            if (fileSize != -1) {
                // Receive and save the file
                try (FileOutputStream fileOutputStream = new FileOutputStream("received_" + fpath);
                     BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(fileOutputStream)) {

                    byte[] buffer = new byte[1024];
                    int bytesRead;
                    long receivedData = 0;

                    // Receive the file content in chunks
                    while (receivedData < fileSize) {
                        bytesRead = dataInputStream.read(buffer);
                        receivedData += bytesRead;
                        bufferedOutputStream.write(buffer, 0, bytesRead);
                    }

                    // sendMessage(fpath + "successfully downloaded as received_" + fpath);
                    System.out.println("File received: " + fpath);
                }
            } else {
                System.out.println("File not found on the server (receiver message)");
            }    

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    


    public String receiveUserName(){
        try {
   
            System.out.println("Client Name (Client - receiver)");
            String username =  bufferedReader.readLine();
            System.out.println("Name Returned from Server: " + username);
            return username;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    // public void newSocket(String ip, int port) throws UnknownHostException, IOException{
    //     Socket socket = new Socket(ip, port);
    //     this.socket = socket;
    // }


    // public Client (Socket socket, String username) {


    //     try {
    //         this.socket = socket;
    //         // this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())); // change later to input from user
    //         // this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    //         this.username = username;
    //     } catch (IOException e){
    //         closeEverything(socket, bufferedReader, bufferedWriter);
    //     }
    // }


    // ** METHODS

    // public void sendMessage(){
    //     try {
    //         bufferedWriter.write(username);
    //         bufferedWriter.newLine();
    //         bufferedWriter.flush();

    //         Scanner scanner = new Scanner(System.in);
    //         while (socket.isConnected()){
    //             String messageToSend =  scanner.nextLine();
    //             bufferedWriter.write(username + ": " + messageToSend);
    //             bufferedWriter.newLine();
    //             bufferedWriter.flush();
    //         }
    //     } catch (IOException e){
    //         e.printStackTrace();
    //     }
    // }



    // public void closeEverything(Socket socket, BufferedReader bufferedReader, BufferedWriter bufferedWriter){

    //     try{
    //         if (bufferedReader  != null){
    //             bufferedReader.close();
    //         }
    //         if (bufferedWriter != null){
    //             bufferedWriter.close();
    //         }
    //         if (socket !=  null){
    //             socket.close();
    //         }
    //     } catch (IOException e){
    //         e.printStackTrace();
    //     }
    // }

    // public static void main(String[] args) throws IOException {

    //     // SwingUtilities.invokeLater(() -> {
    //     //     ChatGUI chatGUI = new ChatGUI();
    //     //     chatGUI.setVisible(true);
    //     // });
        
    // Scanner scanner = new Scanner(System.in);
    // System.out.println("Enter your username for the group chat");
    // String username = scanner.nextLine();
    // Socket socket = new Socket("localhost", 9000);
    // Client client = new Client(socket, username);
    // client.listenForMessage();
    // client.sendMessage();


    // }


     public String extractFilename(String msg){
         // Define a regex pattern to match "/register" followed by a space and capture the word
        // The word is captured in a capturing group (indicated by parentheses)
        String regex = "/download\\s+([\\S]+)";

        
        // Create a Pattern object from the regex
        Pattern pattern = Pattern.compile(regex);
        
        // Create a Matcher object for the input sentence
        Matcher matcher = pattern.matcher(msg);
        
        // Check if the pattern is found in the input sentence
        if (matcher.find()) {
            // Group 1 of the matcher contains the captured word
            return matcher.group(1);
        } else {
            // Return an empty string or handle the case when "/register" is not found
            return "";
        }
    }
}
