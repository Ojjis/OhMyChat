package com.ojjis.ohmychat.client.gui;

import com.ojjis.ohmychat.client.ChatClient;
import com.ojjis.ohmychat.client.functions.FunctionFilter;
import com.ojjis.ohmychat.common.message.ChatMessage;
import com.ojjis.ohmychat.common.User;
import com.ojjis.ohmychat.common.UserList;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.net.URISyntaxException;


/**
 * The Client with its GUI
 */
public class ClientGUI extends JFrame implements ActionListener {
    private Image backgroundImage;
    private static final long serialVersionUID = 1L;
    public static final String BG_JPG = "/bg.jpg";
    public static final String LOGIN_PANEL = "Login Panel";
    public static final String CHAT_PANEL = "Main Panel";

    private JPanel mainContainerPanel, loginPanel, chatPanel;
    private JScrollPane chatBox, userBox;
    private JList userList;

    // to hold the Username and later on the messages
    private JTextField textFieldUsername;
    private JTextField textFieldChat;
    private JTextField textFieldServer;
    private JTextField textFieldPortNr;
    private JLabel textFieldError;
    // to Logout and get the list of the users
    private JButton loginButton, logoutButton;

    // for the chat room
    private JTextPane chatArea;
    // the Client object

    private ChatClient client;
    // the default port number

    // if it is for connection
    private boolean connected;
    private int defaultPort;
    private String defaultHost;
    private User clientUser;

    // Constructor connection receiving a socket number

    /**
     * Constructor that creates the GUI and gets a socket from the
     * server if everything goes as planned :)
     *
     * @param host the host ip address
     * @param port port number
     * @throws IOException
     * @throws URISyntaxException
     */
    ClientGUI(String host, int port) throws IOException, URISyntaxException {

        super("Oh My Chat Client");

        defaultPort = port;
        defaultHost = host;
        backgroundImage = ImageIO.read(getClass().getResource(BG_JPG));

        // Create the loginButton panel with background
        loginPanel = new JPanel(new GridLayout(3, 1)) {
            public void paint(Graphics g) {
                g.drawImage(backgroundImage, 0, 0, null);
                super.paint(g);
            }

        };
        loginPanel.setOpaque(false);
        // the server name and the port number

        // the two JTextField with default value for server address and port number
        JLabel serverLabel = new JLabel("Server IP:  ");
        textFieldServer = new JTextField(host, 20);
        textFieldServer.setHorizontalAlignment(SwingConstants.RIGHT);


        JLabel portNumberLabel = new JLabel("Port Number:  ");
        textFieldPortNr = new JTextField("" + port, 20);
        textFieldPortNr.setHorizontalAlignment(SwingConstants.RIGHT);

        //Login button
        loginButton = new JButton("Login");
        loginButton.addActionListener(this);


        // the Label and the TextField
        JLabel userNameLabel = new JLabel("Chat Name:");
        textFieldUsername = new JTextField("Anonymous", 20);
        textFieldUsername.setBackground(Color.WHITE);

        //Error label
        textFieldError = new JLabel();
        textFieldError.setFont(new Font("sans serif", Font.BOLD, 16));
        textFieldError.setForeground(Color.ORANGE);
        textFieldError.setHorizontalAlignment(SwingConstants.CENTER);
        textFieldError.setMaximumSize(new Dimension(200, 20));



        JPanel serverAndPort = new JPanel(new GridLayout(4, 4, 1, 3));
        serverAndPort.add(new JLabel());
        serverAndPort.add(serverLabel);
        serverAndPort.add(textFieldServer);
        serverAndPort.add(new JLabel());

        serverAndPort.add(new JLabel());
        serverAndPort.add(portNumberLabel);
        serverAndPort.add(textFieldPortNr);
        serverAndPort.add(new JLabel());

        serverAndPort.add(new JLabel());
        serverAndPort.add(userNameLabel);
        serverAndPort.add(textFieldUsername);
        serverAndPort.add(new JLabel());

        serverAndPort.add(new JLabel());
        serverAndPort.add(new JLabel());
        serverAndPort.add(loginButton);
        serverAndPort.add(new JLabel());

        serverAndPort.setOpaque(false);
        loginPanel.add(new JLabel());
        loginPanel.add(serverAndPort);
        loginPanel.add(textFieldError);


        // The CenterPanel which is the chat room and the user list
        chatPanel = new JPanel(new BorderLayout());

        //Set up the main chat area
        chatArea = new JTextPane();
        chatArea.setEditable(false);
        chatArea.setContentType("text/html");
        //chatArea.setLineWrap(true);
        chatArea.setBorder(BorderFactory.createEmptyBorder());
        chatArea.setBackground(Color.black);
        chatArea.setForeground(Color.green);

        chatBox = new JScrollPane(chatArea);
        chatBox.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, Color.DARK_GRAY));


        // Buttons for logout and updating users -> Will be removed...
        logoutButton = new JButton("Logout");
        logoutButton.addActionListener(this);
        logoutButton.setEnabled(false);        // you have to login before being able to logout

        //Create and initially hide the chat field
        JPanel chatInputBox = new JPanel(new GridLayout(1, 1));
        textFieldChat = new JTextField("");
        textFieldChat.setBackground(Color.WHITE);
        chatInputBox.add(textFieldChat);

        //Set up the user list
        DefaultListModel userListModel = new DefaultListModel();
        userList = new JList(userListModel);
        userList.setBackground(Color.black);
        userList.setCellRenderer(new CustomListRenderer(userList));
        userBox = new JScrollPane(userList);
        userBox.setBackground(Color.black);


        //Add the chat area and the user list
        mainContainerPanel = new JPanel(new CardLayout());
        chatPanel.add(chatBox, BorderLayout.CENTER);
        chatPanel.add(userBox, BorderLayout.EAST);
        chatPanel.add(chatInputBox, BorderLayout.SOUTH);


        //Add to card layout mainContainerPanel
        mainContainerPanel.add(loginPanel, LOGIN_PANEL);
        mainContainerPanel.add(chatPanel, CHAT_PANEL);

        //Add the card panel to the main window
        add(mainContainerPanel);


        //Add the menu bar to the top of the main container
        setJMenuBar(menuBarCreator());


        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(800, 600);
        setResizable(false);
        setVisible(true);
        textFieldUsername.requestFocus();

    }

    /**
     * append
     * Method that will append messages to the main chat area in the GUI
     *
     * @param text string to be appended
     *
     */
    public void append(String text) {
        try {
            Style greenStyle = chatArea.addStyle("Green Style", null);
            StyleConstants.setForeground(greenStyle, Color.GREEN);
            Document doc = chatArea.getDocument();
            doc.insertString(doc.getLength(), text, greenStyle);
        } catch(BadLocationException exc) {
            exc.printStackTrace();
        }
    }

     /**
     * append
     * Method that will append messages to the main chat area in the GUI
     * but with different info-color
     *
     * @param text string to be appended
     */
    public void appendInfo(String text) {
        try {
            Style grayStyle = chatArea.addStyle("Dark Gray Style", null);
            StyleConstants.setForeground(grayStyle, Color.DARK_GRAY);
            Document doc = chatArea.getDocument();
            doc.insertString(doc.getLength(), text, grayStyle);
        } catch(BadLocationException exc) {
            exc.printStackTrace();
        }
    }

    /**
     * Append error
     * Will append an error to the textfield
     *
     * @param msg message to be appended to the error textfield
     */
    public void displayError(String msg) {
        //To change body of created methods use File | Settings | File Templates.
        textFieldError.setText(msg);
    }

    /**
     * updateUserList
     * Method for updating the user list in the client
     * Keeps the client at the top.
     *
     * @param ul the new user list to be used
     */
    public void updateUserList(UserList ul) {
        DefaultListModel dm;

        if (ul != null && ul.getUsers().size() > 0) {
            dm = new DefaultListModel();
            for (User user : ul.getUsers()) {
                if (user.equals(clientUser)) {
                    user.setIsClient(true);
                    dm.insertElementAt(user, 0);
                } else {
                    dm.addElement(user);
                }
            }
            userList.setModel(dm);
        } else {
            dm = new DefaultListModel();
        }
        userList.setModel(dm);
    }

    /**
     * TODO: Fix this one
     * Method for handling connection fails
     */
    public void connectionFailed() {
        loginButton.setEnabled(true);
        logoutButton.setEnabled(false);

        textFieldUsername.setText(clientUser.getName());
        // reset port number and host name as a construction time
        textFieldPortNr.setText("" + defaultPort);
        textFieldServer.setText(defaultHost);

        // let the user change them
        textFieldServer.setEditable(true);
        textFieldPortNr.setEditable(true);
        // don't react to a <CR> after the username
        textFieldUsername.removeActionListener(this);
        connected = false;
    }

    /**
     * Actions from the client GUI
     *
     * @param e event
     */
    public void actionPerformed(ActionEvent e) {
        Object eSource = e.getSource();

        // This is a logout action
        if (eSource == logoutButton) {
            doLogout("Exit from the button");
            return;
        }

        /*
        //This is an update users action
        if (eSource == updateUsersButton) {
            client.sendMessage(new ChatMessage(ChatMessage.USERLIST, "Update My Users Please"));
            return;
        } */

        // This is the normal message action
        if (connected) {
            String chatText = textFieldChat.getText();
            boolean sendAfterFilter = filterChatText(chatText);
            if(sendAfterFilter){
                client.sendMessage(new ChatMessage(ChatMessage.MESSAGE, chatText));
            }
            textFieldChat.setText("");
            return;
        }

        // This is the login action
        if (eSource == loginButton) {

            String username = textFieldUsername.getText().trim();
            String server = textFieldServer.getText().trim();
            String portNumber = textFieldPortNr.getText().trim();
            int port;

            //Empty username (display error message)
            if (username.length() == 0) {
                return;
            }

            //Empty server name (display error message)
            if (server.length() == 0) {
                return;
            }

            //Empty port number or not numbers (display error message)
            if (portNumber.length() == 0) {
                return;
            } else {
                try {
                    port = Integer.parseInt(portNumber);
                } catch (Exception en) {
                    return;
                }
            }

            //Create this user as new user
            clientUser = new User(username);

            //Create and start the chat GUI
            client = new ChatClient(server, port, clientUser, this);
            if (!client.start()) {
                System.out.println("Could not start..");
                return;
            }

            //Set connected true and change view to the chat panel
            connected = true;
            switchMainView(CHAT_PANEL);

            // Disable login button and enable logout and update user buttons
            loginButton.setEnabled(false);
            logoutButton.setEnabled(true);

            // Disable the Server and Port JTextField
            textFieldUsername.setEditable(false);
            textFieldServer.setEditable(false);
            textFieldPortNr.setEditable(false);
            textFieldError.setText("");

            // Add listener for chat messages and set focus to this field
            textFieldChat.addActionListener(this);
            textFieldChat.requestFocus();
        }

    }

    /**
     * filterChatText will see if there is a command message
     * or not and if so filter it through the function filter
     * @param chatText text from user
     * @return true if text is not filtered
     */
    private boolean filterChatText(String chatText) {
        if(chatText.startsWith("/")){
            appendInfo(FunctionFilter.process(chatText));
            return false;
        }else{
            return true;
        }
    }

    /**
     * menuBarCreator
     * Method for creating the menu for the client
     *
     * @return a new menu
     */
    public JMenuBar menuBarCreator() {

        // Create the menu elements
        JMenuBar menuBar = new JMenuBar();
        JMenu menuFile = new JMenu("File");
        JMenu menuHelp = new JMenu("Help");
        JMenuItem menuFileLogout = new JMenuItem("Logout");
        JMenuItem menuFileGetUsers = new JMenuItem("Update user list");
        JMenuItem menuFileExit = new JMenuItem("Exit");
        JMenuItem menuHelpRules = new JMenuItem("Rules");
        JMenuItem menuHelpAbout = new JMenuItem("About");
        JMenuItem menuHelpHow = new JMenuItem("How To Chat");

        // Set shortcuts for menu
        menuFile.setMnemonic(KeyEvent.VK_F);
        menuHelp.setMnemonic(KeyEvent.VK_H);

        // Put together the menu parts with each other
        menuBar.add(menuFile);
        menuBar.add(menuHelp);
        menuFile.add(menuFileGetUsers);
        menuFile.add(menuFileLogout);
        menuFile.add(menuFileExit);
        menuHelp.add(menuHelpRules);
        menuHelp.add(menuHelpAbout);
        menuHelp.add(menuHelpHow);

        // Set listener to the logout menu option
        menuFileLogout.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                doLogout("Logout from the menu");
            }
        });

        // Set listener to the exit menu option
        menuFileExit.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String message = "Are you sure you want to quit?";
                int answer = JOptionPane.showConfirmDialog(getContentPane(), message, "", JOptionPane.YES_NO_OPTION);
                if (answer == JOptionPane.YES_OPTION) {
                    if (client != null) {
                        doLogout("Logout and exit from the menu");
                    } else {
                        System.out.println("Logout and exit from the menu");
                    }
                    System.exit(0);
                }
            }
        });

        // Set listener to the update user list menu option
        menuFileGetUsers.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                client.sendMessage(new ChatMessage(ChatMessage.USERLIST, "Update my user list please"));
            }
        });

        return menuBar;
    }


    /**
     * doLogout
     * Method responsible for handling the logout command
     *
     * @param message logout message
     */
    private void doLogout(String message) {
        //Send logout message
        client.sendMessage(new ChatMessage(ChatMessage.LOGOUT, message));

        //Remove the listener to the chat panel
        textFieldChat.removeActionListener(this);

        //Enable the login button
        loginButton.setEnabled(true);

        //Switch view to the login view
        switchMainView(LOGIN_PANEL);
    }


    /**
     * switchMainView
     * Method responsible for switching views between chat and login
     *
     * @param panelId the panel/card id you want to switch to
     */
    private void switchMainView(String panelId) {
        CardLayout cardLayout = (CardLayout) (mainContainerPanel.getLayout());
        cardLayout.show(mainContainerPanel, panelId);
    }

    /**
     * main
     * Main method for starting the client GUI
     * Will be run as the single point of entry for the client
     */
    public static void main(String[] args) {
        try {
            new ClientGUI("localhost", 1500);
        } catch (Exception e) {
            System.out.println("Could not start: " + e.getMessage());
            e.printStackTrace();
        }
    }


}

