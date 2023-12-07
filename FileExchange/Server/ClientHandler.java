package Server;



import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;




public class ClientHandler implements Runnable {

    public static ArrayList<ClientHandler> clientHandlers = new ArrayList<>();
    private Socket socket;
    private BufferedReader  bufferedReader;
    private BufferedWriter bufferedWriter;
    private DataOutputStream dataOutputStream;
    private DataInputStream dataInputStream;
    private String clientUsername;
    private Boolean joined;
    private Boolean registered; 

    

    public String getClientUsername() {
        return clientUsername;
    }

    public ClientHandler(Socket socket){


        try {
            System.out.println("listening.2.");
            this.socket =  socket;
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.dataOutputStream = new DataOutputStream(socket.getOutputStream());
            this.dataInputStream = new DataInputStream(socket.getInputStream());
            this.registered = false;
            sendMsg("Connection to the File Exchange Server is successful!");
            // this.clientUsername =  bufferedReader.readLine();
            clientHandlers.add(this);
            System.out.println("listening.3.");
            // broadcastMessage("SERVER: " + clientUsername + " has entered the chat!");

        } catch (IOException e){
            // closeEverything(socket, bufferedReader, bufferedWriter);
        }
    }

    // what is run on a separate thread for each client . Listens to client messages
    @Override
    public void run(){
       // Existing code...
        
       String messageFromClient;
       
       
       while (!socket.isClosed()) { // socket.isConnected()
           try {
               messageFromClient = bufferedReader.readLine().trim();
               System.out.println("Received message from client: " + messageFromClient);

               if (messageFromClient != null) {
                   if (messageFromClient.startsWith("/get")) {
                        System.out.println("I received req to download!");
                        handleFileDownload(messageFromClient);
                    } else if (messageFromClient.startsWith("/register")) {
                        System.out.println("I received req to register");
                        registerUser(messageFromClient);
                    } else if (messageFromClient.startsWith("/store")) {
                        System.out.println("I received req to store");
                        receiveFile(messageFromClient); 
                    } else if (messageFromClient.startsWith("/pullName")) {
                        System.out.println("I received req for name");
                        getUserName(); 
                    } else if (messageFromClient.startsWith("/success")){
                        System.out.println("I received successful msg");
                        ackSuccess(messageFromClient);
                    } else if (messageFromClient.startsWith("/leave")){
                        System.out.println("I received disconnection req");
                        disconnectClient();
                    } else if (messageFromClient.startsWith("/dir")){
                        System.out.println("I received directory req.");
                        sendDirectory();
                    } else {
                    //    broadcastMessage(messageFromClient);
                    }
               }
           } catch (SocketException e) {
                // Handle SocketException (connection closed)
                System.out.println("Client disconnected: " + socket.getInetAddress());
                disconnectClient();
           } catch (IOException e) {
               e.printStackTrace();
           }
       }
    }

    public void sendMsg(String msg) throws IOException{
        System.out.println("The sent msg from server is: " + msg);

        bufferedWriter.write("SERVER: " + msg);
        bufferedWriter.newLine();
        bufferedWriter.flush();
    }

    public void registerUser(String name) throws IOException{
        name  = extractName("/register", name);
        
        if (name.length() != 0){
            name = name.substring(0,1).toUpperCase() + name.substring(1);
            System.out.println("To register name: " + name);

            Boolean unique = true;

            System.out.println("Client Handler Size: " + clientHandlers.size());

            for (int i = 0; i < clientHandlers.size() - 1; i++) {

                if (clientHandlers.get(i).registered){
                    System.out.println("Clients joined: " + clientHandlers.get(i).getClientUsername());
                
                    if (clientHandlers.get(i).getClientUsername().equals(name)) {
                        unique = false;
                        System.out.println(unique);
                    }
                } else {
                    unique = true;
                }
            }

            try {
                if (this.registered == true){
                    dataOutputStream.writeInt(0);
                    sendMsg("Error: You are already registered!");
                } else {

                    if (unique == true){
                        this.clientUsername = name;
                        this.registered = true;
                        dataOutputStream.writeInt(1);
                        sendMsg("Welcome " + name + "!");
                    } else {
                        dataOutputStream.writeInt(0);
                        sendMsg("Error: Registration failed. Handle or alias already exists");
                    }
                }
            } catch (IOException e){
                e.printStackTrace();
                sendMsg("Error in registration.");
            }

        } else {
            dataOutputStream.writeInt(0);
            sendMsg("Invalid registration. Please check your alias.");
            return;
        }
        
    }

    public void getUserName () throws IOException {
        System.out.println(this.clientUsername);
        sendMsg(this.clientUsername);
    }


    public void handleFileDownload(String message) {
        String filename = extractSentence("/get", message);

        // Check if the file exists before attempting to send it
        String path = System.getProperty("user.dir");
        File dir = new File(path + "/Server/ServerFiles/");
        String [] files =  dir.list();
        Boolean flag = false;
    
        try {
            for(String file : files) {
                if(file.equals(filename)) {
                    flag = true;
                }
            }

            if (flag){

                File Ffile = new File(path + "/Server/ServerFiles/" + filename);
                FileInputStream fis = new FileInputStream(Ffile);
                byte[] buffer = new byte[1024];
                int bytesRead;
    
                // Send the file size to the client
                long fileSize = Ffile.length();
                dataOutputStream.writeLong(fileSize);
    
                // Send the file content in chunks
                while ((bytesRead = fis.read(buffer)) != -1) {
                    dataOutputStream.write(buffer, 0, bytesRead);
                }
            } else {
                // File not found, notify the client
                dataOutputStream.writeLong(-1); // Signal that the file is not found
                sendMsg("Error: File not found in the server!");
            }
    
        } catch (IOException e) {
            e.printStackTrace();
        } 
    }

    public void sendDirectory() throws IOException {
        String path = System.getProperty("user.dir");
        File dir = new File(path + "/Server/ServerFiles/");
        String[] files = dir.list();
    
        System.out.println("Sending Directory List...");
    
        bufferedWriter.write("Server Directory:");
        bufferedWriter.newLine();
    
        for (String file : files) {
            System.out.println(file);
            bufferedWriter.write(file);
            bufferedWriter.newLine();
        }
    
        // Indicator for the end of the list
        bufferedWriter.write("END_OF_LIST");
        bufferedWriter.newLine();
    
        bufferedWriter.flush();
    }
    


    public void receiveFile(String message){
            String fpath =  extractSentence("/store", message);
            String dirPath = System.getProperty("user.dir");
            String finalPath =  dirPath + "/Server/ServerFiles/" + fpath;

            LocalDateTime currentDateTime = LocalDateTime.now();

            // Format LocalDateTime to a human-readable string
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            String formattedDateTime = currentDateTime.format(formatter);

            try {
                System.out.println("ENTER HERE!: " + finalPath);
                // Receive the file size from the server
                long fileSize = dataInputStream.readLong();
                System.out.println(fileSize);

                if (fileSize != -1) {
                    // Receive and save the file
                    try (FileOutputStream fileOutputStream = new FileOutputStream(finalPath);
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
                        System.out.println("/serverRes File received from server: " + fpath);
                        sendMsg("/serverRes " + this.clientUsername + " (" + formattedDateTime + ") "  + "stored a file to server: " + fpath);
                    }
                } else {
                    sendMsg("/serverRes File not found locally");
                }    

            } catch (IOException e) {
                e.printStackTrace();
            }
    }

    public void ackSuccess(String msg) throws IOException{
        System.out.println("I entered successful function");
        String successMessage  = extractSentence("/success", msg);
        sendMsg(successMessage);
    }


    public void broadcastMessage(String messageToSend){
        for (ClientHandler clientHandler: clientHandlers ){
            try {
                // if not you, send message to that client
                if (!clientHandler.clientUsername.equals(clientUsername)){
                    clientHandler.bufferedWriter.write(messageToSend);
                    clientHandler.bufferedWriter.newLine();
                    clientHandler.bufferedWriter.flush();
            }
        } catch (IOException e) {
                closeEverything(socket, bufferedReader, bufferedWriter);
            }
        }
    }

    public void disconnectClient(){
        try {
            System.out.println("Someone is trying to leave.");
            sendMsg("Connection closed. Thank you!");
            clientHandlers.remove(this);
            socket.close();
            
        } catch (SocketException e) {
            System.out.println("Someone left.");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    // user disconnected
    public void removeClientHandler() {
        clientHandlers.remove(this);
        broadcastMessage("SERVER: " + clientUsername + "has left the chat!");
    }

    public void closeEverything(Socket socket, BufferedReader bufferedReader, BufferedWriter bufferedWriter){
        removeClientHandler();

        try{
            if (bufferedReader  != null){
                bufferedReader.close();
            }
            if (bufferedWriter != null){
                bufferedWriter.close();
            }
            if (socket !=  null){
                socket.close();
            }
        } catch (IOException e){}
    }


    public String extractFilename(String msg){
         // Define a regex pattern to match "/register" followed by a space and capture the word
        // The word is captured in a capturing group (indicated by parentheses)
        String regex = "/get\\s+([\\S]+)";

        
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

    // * Extract Name from user input
    private static String extractName(String key, String input) {
        // Define a regex pattern to match "/register" followed by a space and capture the word
       // The word is captured in a capturing group (indicated by parentheses)
       String regex = key + "\\s+(\\w+)";
       
       // Create a Pattern object from the regex
       Pattern pattern = Pattern.compile(regex);
       
       // Create a Matcher object for the input sentence
       Matcher matcher = pattern.matcher(input);
       
       // Check if the pattern is found in the input sentence
       if (matcher.find()) {
           // Group 1 of the matcher contains the captured word
           return matcher.group(1);
       } else {
           // Return an empty string or handle the case when "/register" is not found
           return "";
       }
   }

   private static String extractSentence(String key, String input) {
    // Define a regex pattern to match "/success" followed by a space and capture the sentence
    // The sentence is captured in a capturing group (indicated by parentheses)
    String regex = key + "\\s+(.*)";

    // Create a Pattern object from the regex
    Pattern pattern = Pattern.compile(regex);

    // Create a Matcher object for the input sentence
    Matcher matcher = pattern.matcher(input);

    // Check if the pattern is found in the input sentence
    if (matcher.find()) {
        // Group 1 of the matcher contains the captured sentence
        return matcher.group(1);
    } else {
        // Return an empty string or handle the case when "/success" is not found
        return "";
    }
}
}
