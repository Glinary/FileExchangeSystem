package Client;
public class Driver {

    public static void main(String[] args) {
        ChatGUI gui = new ChatGUI();
        Client client = new Client();
        // Try trym = new Try();
        Controller controller = new Controller(gui, client);
    }
        
    
    
}
