package com.ojjis.ohmychat.client;

import com.ojjis.ohmychat.client.gui.ClientGUI;
import com.ojjis.ohmychat.common.message.ChatMessage;
import com.ojjis.ohmychat.common.User;
import com.ojjis.ohmychat.common.UserList;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * The main chat client class that will handle the communication
 * between the client and the server..
 */
public class ChatClient  {

    private User user;
    private ObjectInputStream sInput;
    private ObjectOutputStream sOutput;
    private Socket socket;

    private ClientGUI cg;
    private String server;
    private int port;


    /**
     * Constructor call when used from a GUI
     * in console mode the ClienGUI parameter is null
     */
    public ChatClient(String server, int port, User user, ClientGUI cg) {
        this.server = server;
        this.port = port;
        this.user = user;
        this.cg = cg;
    }

    /**
     * start
     * Will try to connect to server via a new socket using the server name and port
     * provided in the login screen. Will send error messages to the login screen.
     * @return true if the
     */
    public boolean start() {
        try {
            socket = new Socket(server, port);
        } catch (UnknownHostException e) {
            displayError("Error when trying to connect to server '" + server + "' - Unknown host!");
            return false;
        } catch (IOException e) {
            displayError("Error when trying to connect to server '" + server + "' - IOError!");
            return false;
        }

        String msg = "Connection accepted " + socket.getInetAddress() + ":" + socket.getPort();
        display(msg);

        // Create the data streams
        try
        {
            sInput  = new ObjectInputStream(socket.getInputStream());
            sOutput = new ObjectOutputStream(socket.getOutputStream());
        }
        catch (IOException eIO) {
            displayError("Exception creating new Input/output Streams: " + eIO);
            return false;
        }

        // Creates a new listen thread to listen to messages from the server
        new ServerMessageListener().start();

        // Start of with sending a login message to the server to login to server..
        try {

            sOutput.writeObject(new ChatMessage(ChatMessage.LOGIN,"Hi, I want to log in",user));

        } catch (IOException ioe) {
            displayError("Exception doing login: " + ioe);
            disconnect();
            return false;
        }
        // success we inform the caller that it worked
        return true;
    }

    /**
     * display
     * Will display a message to the GUI
     * @param msg message to display in GUI
     */
    private void display(String msg) {
        if(cg == null)
            System.out.println("GUI is null ?! Message: " + msg);      // println in console mode
        else
            cg.append(msg + "\n");		// append to the ClientGUI JTextArea (or whatever)
    }

    private void displayError(String msg){
        cg.displayError(msg);
    }

    /**
     * This method till send a ChatMessage to the server
     * @param msg message to the server
     */
    public void sendMessage(ChatMessage msg) {
        try {
            sOutput.writeObject(msg);
        }
        catch(IOException e) {
            display("Exception writing to server: " + e);
        }catch (NullPointerException ne){
            System.out.println("Could not write out");
        }
    }



    /**
     * When something goes wrong
     * Close the Input/Output streams and disconnect not much to do in the catch clause
     */
    void disconnect() {
        try {
            if(sInput != null){
                sInput.close();
            }
            if(sOutput != null){
                sOutput.close();
            }

            if(socket != null){
                socket.close();
            }
        } catch (IOException e) {
            displayError("Error disconnecting: " + e);
        }
        // inform the GUI
        if(cg != null){
            cg.connectionFailed();
        }

    }



    /**
     * a class that waits for the message from the server and append them to the JTextArea
     * if we have a GUI or simply System.out.println() it in console mode
     */
    class ServerMessageListener extends Thread {

        public void run() {
            while(true) {
                try {

                    ChatMessage chatMessage = (ChatMessage) sInput.readObject();

                    if(chatMessage.getType() == ChatMessage.MESSAGE){
                        if(chatMessage.getUser().equals(user)){
                            System.out.println("This is a message From Myself");
                        }
                        sendText("["+chatMessage.getUser().getName()+"] "+ chatMessage.getMessage());
                    }else if(chatMessage.getType() == ChatMessage.USERLIST){

                        UserList ul = chatMessage.getUserList();
                        cg.updateUserList(ul);
                    }else if(chatMessage.getType() == ChatMessage.USER && chatMessage.getAction() == ChatMessage.PUT){
                        //This is an update message to update the user
                        User newUser = chatMessage.getUser();
                        user.setId(newUser.getId());
                        user.setName(newUser.getName());
                        System.out.println("User Updated widht id: " + newUser.getId());
                    }
                }
                catch(IOException e) {
                    display("Server has closed the connection: " + e);
                    if(cg != null)
                        cg.connectionFailed();
                    break;
                }
                catch(ClassNotFoundException cnfe) {
                    System.out.println("Class Not Found Exception when listening from server: " + cnfe.getMessage());
                }
            }
        }

        private void sendText(String msg) {
            if(cg == null) {
                System.out.println(msg);
                System.out.print("> ");
            }
            else {
                cg.append(msg +"\n");
            }
        }
    }
}