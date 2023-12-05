package Server.ServerFiles;



import java.io.*;
import java.net.Socket;

public class She {

    private Socket socket;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;
    private String username;

    // public String getLastCmd() {
    //     return lastCmd;
    // }


    // public void setLastCmd(String lastCmd) {
    //     this.lastCmd = lastCmd;
    // }


    public She (Socket socket, String username) {


        try {
            this.socket = socket;
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())); // change later to input from user
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.username = username;
        } catch (IOException e){
            closeEverything(socket, bufferedReader, bufferedWriter);
        }
    }


    // ** METHODS

    public void sendMessage(){
        try {
            bufferedWriter.write(username);
            bufferedWriter.newLine();
            bufferedWriter.flush();

            while (socket.isConnected()){
                bufferedWriter.newLine();
                bufferedWriter.flush();
            }
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    public void listenForMessage(){
        new Thread(new Runnable(){
            @Override
            public void run(){
                String msgFromGroupChat;

                while(socket.isConnected()){
                    try{
                        msgFromGroupChat = bufferedReader.readLine();
                        System.out.println(msgFromGroupChat);
                    } catch (IOException e){
                        closeEverything(socket, bufferedReader, bufferedWriter);
                    }
                }
            }
        }).start();
    }


    public void closeEverything(Socket socket, BufferedReader bufferedReader, BufferedWriter bufferedWriter){

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
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException {

        // SwingUtilities.invokeLater(() -> {
        //     ChatGUI chatGUI = new ChatGUI();
        //     chatGUI.setVisible(true);
        // });
        
        System.out.println("Enter your username for the group chat");
    }
}

    
