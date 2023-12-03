package Server;



import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;




public class ClientHandler implements Runnable {

    public static ArrayList<ClientHandler> clientHandlers = new ArrayList<>();
    private Socket socket;
    private BufferedReader  bufferedReader;
    private BufferedWriter bufferedWriter;
    private DataOutputStream dataOutputStream;
    private String clientUsername;
    private Boolean registered; 

    public ClientHandler(Socket socket){



        try {
            System.out.println("listening.2.");
            this.socket =  socket;
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.dataOutputStream = new DataOutputStream(socket.getOutputStream());
            this.registered = false;
            sendMsg("You are now connected!");
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
        
       System.out.println("Waiting for message...");
       String messageFromClient;
       
       
       while (socket.isConnected()) {
           try {
               messageFromClient = bufferedReader.readLine();
               System.out.println("Received message from client: " + messageFromClient + "/n");

               if (messageFromClient != null) {
                   if (messageFromClient.startsWith("/download")) {
                        System.out.println("I received req to download!");
                       handleFileDownload(messageFromClient);
                   } else if (messageFromClient.startsWith("/register")) {
                        System.out.println("I received req to register");
                        registerUser(messageFromClient);
                   } else if (messageFromClient.startsWith("/getName")) {
                        System.out.println("I received req for name");
                        getUserName(); 
                   } else {
                    //    broadcastMessage(messageFromClient);
                   }
               }
           } catch (IOException e) {
               e.printStackTrace();
           }
       }
    }

    public void registerUser(String name) throws IOException{
        System.out.println("I entered registration Server");
        name  = extractName(name).substring(0,1).toUpperCase() + extractName(name).substring(1);
        System.out.println("Reg name: " + name);

        try {
            if (this.registered == true){
                System.out.println("register true");
                dataOutputStream.writeInt(0);
                sendMsg("You are already registered!");
            } else {
                System.out.println("register false");
                this.clientUsername = name;
                this.registered = true;
                dataOutputStream.writeInt(1);
                sendMsg("Registration Successful. Your username now is " + name + "!");
            }
        } catch (IOException e){
            e.printStackTrace();
            sendMsg("Error in registratoin.");
        }
    }

    public void getUserName () throws IOException {
        System.out.println("I AM IN GETUSERNAME!");
        System.out.println(this.clientUsername);
        sendMsg(this.clientUsername);
    }


    public void handleFileDownload(String message) {
        String filename = extractFilename(message);

        // Check if the file exists before attempting to send it
        String path = System.getProperty("user.dir");
        File file = new File(path + "/Server/" + filename);
    
        try {
            if (file.exists()) {
                FileInputStream fis = new FileInputStream(file);
                byte[] buffer = new byte[1024];
                int bytesRead;
    
                // Send the file size to the client
                long fileSize = file.length();
                dataOutputStream.writeLong(fileSize);
    
                // Send the file content in chunks
                while ((bytesRead = fis.read(buffer)) != -1) {
                    dataOutputStream.write(buffer, 0, bytesRead);
                }
    
                // System.out.println("Server: File \"" + file + "\" sent to client (" + fileSize + " bytes)");
                sendMsg("Successful Download!");
            } else {
                // File not found, notify the client
                dataOutputStream.writeLong(-1); // Signal that the file is not found
                sendMsg("File not found in the server!");
            }
    
        } catch (IOException e) {
            e.printStackTrace();
        } 

    }

  

    public void sendMsg(String msg) throws IOException{
        System.out.println("The sent msg is: " + msg);

        bufferedWriter.write("SERVER: " + msg);
        bufferedWriter.newLine();
        bufferedWriter.flush();
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

    // * Extract Name from user input
    private static String extractName(String input) {
        // Define a regex pattern to match "/register" followed by a space and capture the word
       // The word is captured in a capturing group (indicated by parentheses)
       String regex = "/register\\s+(\\w+)";
       
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
}
