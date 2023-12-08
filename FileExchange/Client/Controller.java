package Client;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class Controller implements ActionListener, DocumentListener, MessageCallback{

    ChatGUI gui;
    private Client client;

    public Controller(ChatGUI gui, Client client) {
        this.gui = gui;
        this.client = client;
        
        this.client.setMessageCallback(this);
        gui.setActionListener(this);
        gui.setDocumentListener(this);
        updateView();
        
        gui.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                handleWindowClosing(); // Terminate Connection to Server if GUI got closed
            }
        });
    }

    public void updateView() {
        SwingUtilities.invokeLater(() -> gui.getTerminalOut());        
    }

    @Override
    public void insertUpdate(DocumentEvent e) {
        // TODO Auto-generated method stub

       
    }

    @Override
    public void removeUpdate(DocumentEvent e) {
        // TODO Auto-generated method stub
    }

    @Override
    public void changedUpdate(DocumentEvent e) {
        // TODO Auto-generated method stub
    }

    private static boolean isValidHost(String ipAddress) {
        System.out.println("isvalidhost reads " + ipAddress);
        return ipAddress.equals("localhost") || ipAddress.equals("127.0.0.1");
    }

    private static boolean isValidPort(String port) {
        System.out.println("isvalidhost port " + port);
        try {
            int portNumber = Integer.parseInt(port);
            return portNumber >= 0 && portNumber <= 65535;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        // TODO Auto-generated method stub

         if (e.getActionCommand().equals("Enter")){
            // System.out.println(e.getActionCommand());
            // System.out.println(gui.getUserInput());

            client.setLastCmd(gui.getUserInput());
            Boolean validJoin = client.getJoined();
            Boolean validRegister = client.getRegistered();


            // * JOIN Command
            if (gui.getUserInput().trim().startsWith("/join")){

                //tokenize the userinput into an array
                String userInput = gui.getUserInput().trim();
                String[] parts = userInput.split("\\s+");

                lastCmdDisplay();
                //check if array has 3 elements exactly
                if (parts.length == 3 && parts[0].equals("/join")) {
                    String hostCharacters = parts[1];
                    String portCharacters = parts[2];

                    //check if command is valid
                    //check if first index is valid host
                    //check if second index is valid port
                    if (isValidHost(hostCharacters) && isValidPort(portCharacters)) {
             

                        if (!validJoin){
                            // get port
                            //String portCharacters = extractPort(gui.getUserInput());
                            System.out.println("Last port characters: " + portCharacters);
                            int port = Integer.parseInt(portCharacters);

                            // get host
                            //String hostCharacters =  extractHost(gui.getUserInput());
                            System.out.println("Last host characters: " + hostCharacters);

                            // client creds
                            client.setPort(port);
                            client.setHost(hostCharacters);
                            
                            try {
                                client.setSocket(client.getHost(), client.getPort());

                                // Listen to Server:
                                client.listenForMessage();
                            
            
                                gui.setUserInput(""); // clear input box
                            } catch (IOException e1) {
                                gui.clientTerminalOut("Error: Connection to the Server has failed! Please check IP Address and Port Number.");
                                gui.setUserInput("");
                            }
                        } else {
                            gui.clientTerminalOut("Error: You are already joined to the server.");
                            
                        }
                    } else {
                        gui.clientTerminalOut("Error: Connection to the Server has failed! Please check IP Address and Port Number.");
                        gui.setUserInput("");
                    }
                } else {
                    gui.clientTerminalOut("Error: Command parameters do not match or is not allowed.");
                    gui.setUserInput("");
                    
                }

                updateView();
            // * Download Command
            } else if (gui.getUserInput().trim().startsWith("/get")){

                lastCmdDisplay();

                //tokenize the userinput into an array
                String userInput = gui.getUserInput().trim();
                String[] parts = userInput.split("\\s+");

                if (parts.length == 2 && parts[0].equals("/get")) {

                    if (validJoin){

                        client.sendMessage(gui.getUserInput());
                        client.receiveFile(gui.getUserInput());

                        client.listenForMessage();
                        // Ensure that the listenForMessage thread completes before moving on
                            try {
                                client.getListenThread().join();
                            } catch (InterruptedException e2) {
                                e2.printStackTrace();
                            }
                    } else {
                        gui.clientTerminalOut("Error: Invalid command. Make sure you are joined or registered.");
                    }

                    gui.setUserInput("");

                } else {
                    gui.clientTerminalOut("Error: Command parameters do not match or is not allowed.");
                    gui.setUserInput("");
                }

                

            // * STORE Command
            } else if (gui.getUserInput().trim().startsWith("/store")){

                lastCmdDisplay();
                
                //tokenize the userinput into an array
                String userInput = gui.getUserInput().trim();
                String[] parts = userInput.split("\\s+");

                if (parts.length == 2 && parts[0].equals("/store")) {
                    if (validJoin && validRegister){
                        client.sendMessage(gui.getUserInput());
                        client.sendFile(gui.getUserInput());
                        
                    } else {
                        gui.clientTerminalOut("Error: Invalid command. Make sure you are joined or registered.");
                    }

                    client.listenForMessage();
                    gui.setUserInput("");
                } else {
                    gui.clientTerminalOut("Error: Command parameters do not match or is not allowed.");
                    gui.setUserInput("");
                }

            // * Register Command
            } else if (gui.getUserInput().trim().startsWith("/register")){

                //lastCmdDisplay();
                
                //tokenize the userinput into an array
                String userInput = gui.getUserInput().trim();
                String[] parts = userInput.split("\\s+");

                if (parts.length == 2 && parts[0].equals("/register")) {
                    
                    if (validJoin) {
                        int registration = 0; 
                        String name = null;

                        // send message /register to server using client
                        client.sendMessage("/checkReg");
                        registration = client.receiveRegistrationStatus();

                        if (registration != 1){

                            client.sendMessage(gui.getUserInput());
                            client.sendMessage("/checkReg");
                            registration = client.receiveRegistrationStatus();
                        

                            // display last command (differs on other commands because name must not show yet)
                            if (registration == 1){
                                gui.clientTerminalOut(client.getLastCmd()); // display last command without registered name
                                gui.setUserInput("");

                                // client.listenForMessage();

                                // // Ensure that the listenForMessage thread completes before moving on
                                // try {
                                //     client.getListenThread().join();
                                // } catch (InterruptedException e2) {
                                //     e2.printStackTrace();
                                // }

                                // get name from server
                                client.sendMessage("/pullName");
                                name = extractName("SERVER:", client.receiveUserName()); // remove "SERVER" from received name
                                System.out.println("Name: " + name);
                        
                                setRegistration(registration, name);
                            } else {
                                gui.clientTerminalOut("Alias is already taken.");
                            }

                        // already registered/ taken alias
                        } else {
                            lastCmdDisplay();
                            gui.setUserInput("");
                        }
                    } else {
                        lastCmdDisplay();
                        gui.setUserInput("");
                        gui.clientTerminalOut("Error: Invalid command. Make sure you are joined.");
                    }
                } else {
                    lastCmdDisplay();
                    gui.clientTerminalOut("Error: Command parameters do not match or is not allowed.");
                    gui.setUserInput("");
                }


                
                
            // * /? or Help command
            } else if (gui.getUserInput().trim().startsWith("/?")) {

                lastCmdDisplay();
                
                //tokenize the userinput into an array
                String userInput = gui.getUserInput().trim();
                String[] parts = userInput.split("\\s+");

                if (parts.length == 1 && parts[0].equals("/?")) {
                    gui.clientTerminalOut("Commands: /?, /join, /register, /leave, /get, /store, /dir");
                    gui.setUserInput("");
                } else {
                    gui.clientTerminalOut("Error: Command parameters do not match or is not allowed.");
                    gui.setUserInput("");
                }
                
            
            // * /leave or Leave command
            } else if (gui.getUserInput().trim().startsWith("/leave")){

                //tokenize the userinput into an array
                String userInput = gui.getUserInput().trim();
                String[] parts = userInput.split("\\s+");

                lastCmdDisplay();

                //check if parts has only 1 element
                if (parts.length == 1 && parts[0].equals("/leave")) {
                
                    if(validJoin) {
                        client.sendMessage(gui.getUserInput());
                        client.leave();
                        client.setJoined(false);
                        client.setRegistered(false);
                        gui.clientTerminalOut("Connection is closed. Thank you!");


                    } else {
                        gui.clientTerminalOut("Error: Disconnection failed. Please connect to the server first.");
                    }

                    gui.setUserInput("");
                } else {
                    gui.clientTerminalOut("Error: Command parameters do not match or is not allowed.");
                    gui.setUserInput("");
                }
                
            } else if (gui.getUserInput().trim().startsWith("/dir")){

                lastCmdDisplay();

                //tokenize the userinput into an array
                String userInput = gui.getUserInput().trim();
                String[] parts = userInput.split("\\s+");

                if (parts.length == 1 && parts[0].equals("/dir")) {

                    if (validJoin && validRegister){
                        client.sendMessage(gui.getUserInput());

                        client.listenForDirectory();
                        // // Ensure that the listenForMessage thread completes before moving on
                        //     try {
                        //         client.getListenThread().join();
                        //     } catch (InterruptedException e2) {
                        //         e2.printStackTrace();
                        //     }

                    } else {
                        gui.clientTerminalOut("Error: Invalid command. Make sure you have joined and registered.");
                    }

                    gui.setUserInput("");
                } else {
                    gui.clientTerminalOut("Error: Command parameters do not match or is not allowed.");
                    gui.setUserInput("");
                }

                

            } else {
                lastCmdDisplay();
                gui.clientTerminalOut("Error: Command not found.");
                gui.setUserInput("");
            }
         } 
    }   

    //*  Displays last command of user in Terminal
    public void lastCmdDisplay(){
        if (client.getRegistered() == false){
            gui.setTerminalOut( client.getLastCmd(), "", client.getRegistered());
        } else {
            gui.setTerminalOut( client.getLastCmd(), client.getName(), client.getRegistered());
        }
    }   

    // * Register client registration in client
    public void setRegistration(int status, String name){
        System.out.println("Set Registration (Controller)");
        if (status == 1){
            client.setRegistered(true);
            client.setName(name);
        } else if (status == 0){
            client.setRegistered(false);
        }
    }

    //* Displays server response on gui.
    @Override
    public void onMessageReceived(String message) {
        // Update GUI with the received message
        SwingUtilities.invokeLater(() -> {
            gui.clientTerminalOut(message);
        });
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
    // * Auto Leaves when Client closed the GUI
    private void handleWindowClosing() {
        if (client.getJoined()) {
            // Send "/leave" command if the client is joined
            client.sendMessage("/leave");
            client.leave();
            client.setJoined(false);
            client.setRegistered(false);
        }
    }
    
}
