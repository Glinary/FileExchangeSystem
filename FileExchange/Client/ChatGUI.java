package Client;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentListener;


import java.awt.*;
import java.awt.event.ActionListener;

public class ChatGUI extends JFrame {

    private JButton enterButton;
    JTextField userIn;
    JTextArea terminalOut;

    JScrollPane scrollPane;



    public ChatGUI(){
        super("Chat Page");
        setLayout(new BorderLayout());
    
     
        // set size of window
        setSize(1300, 600);

        init();

        
        // explicitly set visibility to true and resizable false
        setVisible(true);
        setResizable(false);
        setLocationRelativeTo(null);

        // Default: Hide the frame, do not close program. Change to exit on close
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    }

    private void init() {

        // ** MAIN NORTH PANEL
        JPanel panelNorth = new JPanel();
        panelNorth.setLayout(new FlowLayout());
        panelNorth.setBackground(Color.decode("#f2f2f2"));

        JLabel label =  new JLabel("File Exchange / Chat Page");
        label.setForeground(Color.WHITE);
        panelNorth.add(label);

        this.add(panelNorth, BorderLayout.NORTH);




        // ** MAIN LEFT
        JPanel leftPanel = new JPanel();
        leftPanel.setBackground(Color.BLACK);
        // centerPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        leftPanel.setLayout(new BorderLayout());

        JLabel userOut = new JLabel("  ~ Welcome to FileExchange. Type /help for commands");
        userOut.setForeground(Color.WHITE);


        terminalOut = new JTextArea();
        terminalOut.setEditable(false);
        terminalOut.setBackground(Color.BLACK);
        // terminalOut.setPreferredSize(new Dimension(850, 800)); // Set your preferred size


        scrollPane = new JScrollPane(terminalOut);
        scrollPane.setBackground(Color.BLACK);
        JScrollBar verticalScrollBar = scrollPane.getVerticalScrollBar();

        verticalScrollBar.setPreferredSize(new Dimension(10, verticalScrollBar.getPreferredSize().height));
        // verticalScrollBar.setPreferredSize(new Dimension(20, Integer.MAX_VALUE));
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        leftPanel.add(scrollPane, BorderLayout.CENTER);
        leftPanel.add(userOut, BorderLayout.NORTH);


            // ** MAIN LEFT:  SOUTH PANEL
            JPanel panelSouth = new JPanel();
            panelSouth.setLayout(new FlowLayout());
            panelSouth.setBackground(Color.RED);

            userIn = new JTextField(30);
            panelSouth.add(userIn);

            enterButton = new JButton("Enter");  // declared outside for listener
            panelSouth.add(enterButton);

            leftPanel.add(panelSouth, BorderLayout.SOUTH);

        this.add(leftPanel, BorderLayout.WEST);



        // ** MAIN RIGHT
        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new BorderLayout());
        rightPanel.setBackground(Color.BLUE);

        JLabel text = new JLabel("HEY THERE!");
        rightPanel.add(text, BorderLayout.CENTER);


        this.add(rightPanel, BorderLayout.CENTER);

    }


    // Listeners for button and texts
    public void setActionListener(ActionListener listener){
        enterButton.addActionListener(listener);
    }       
    

    public void setDocumentListener(DocumentListener listener){
        userIn.getDocument().addDocumentListener(listener);
        terminalOut.getDocument().addDocumentListener(listener);
    }


    // ** METHODS

    public void setTerminalOut(String msg, String clientName, Boolean reg){
        terminalOut.setForeground(Color.WHITE);  // Set color for the entire JTextArea
        if (reg == true){
            terminalOut.append(clientName + ": " + msg + "\n\n");
        } else {
            terminalOut.append(clientName + " " + msg + "\n\n");
        }

        // Ensure the scroll pane scrolls to the bottom
        SwingUtilities.invokeLater(() -> {
        JScrollBar vBar = scrollPane.getVerticalScrollBar();
        vBar.setValue(vBar.getMaximum());
    });
    }

    public void serverTerminalOut(String msg){
        terminalOut.setForeground(Color.WHITE);  // Set color for the entire JTextArea
        terminalOut.append("SERVER: " + msg + "\n\n");
    }

    public void clientTerminalOut(String msg){
        terminalOut.setForeground(Color.WHITE);  // Set color for the entire JTextArea
        terminalOut.append("~ " + msg + "\n\n");
    }


    // ** Getter and Setter (typed into the input box)

    public String getUserInput(){
        return userIn.getText();
    }

    public void setUserInput(String text){
        userIn.setText(text);
    }

    public String getTerminalOut(){
        return terminalOut.getText();
    }


} 
