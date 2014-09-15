package com.ojjis.ohmychat.server;

import com.ojjis.ohmychat.common.message.ChatMessage;
import com.ojjis.ohmychat.common.User;
import com.ojjis.ohmychat.common.UserList;
import com.ojjis.ohmychat.server.gui.ServerGUI;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/*
 * The server that can be run both as a console application or a GUI
 */
public class MainServer {
    // a unique ID for each connection
    private static int uniqueId;
    // an ArrayList to keep the list of the Client
    private ArrayList<ClientThread> al;
    // if I am in a GUI
    private ServerGUI sg;
    // to display time
    private SimpleDateFormat sdf;
    // the port number to listen for connection
    private int port;
    // the boolean that will be turned of to stop the server
    private boolean keepGoing;


    /*
     *  server constructor that receive the port to listen to for connection as parameter
     *  in console
     */
    public MainServer(int port) {
        this(port, null);
    }

    public MainServer(int port, ServerGUI sg) {
        // GUI or not
        this.sg = sg;
        // the port
        this.port = port;
        // to display hh:mm:ss
        sdf = new SimpleDateFormat("HH:mm:ss");
        // ArrayList for the Client list
        al = new ArrayList<ClientThread>();
    }

    public void start() {
        keepGoing = true;
		/* create socket server and wait for connection requests */
        try
        {
            // the socket used by the server
            ServerSocket serverSocket = new ServerSocket(port);

            // infinite loop to wait for connections
            while(keepGoing)
            {
                // format message saying we are waiting
                display("Server waiting for Clients on port " + port + ".");

                Socket socket = serverSocket.accept();  	// accept connection
                // if I was asked to stop
                if(!keepGoing)
                    break;
                ClientThread t = new ClientThread(socket);  // make a thread of it
                al.add(t);
                						// save it in the ArrayList
                t.start();
                broadcast(new ChatMessage(ChatMessage.USERLIST, ChatMessage.PUT, "New User Connected", getCurrentUsers()));

            }
            // I was asked to stop
            try {
                serverSocket.close();
                for(int i = 0; i < al.size(); ++i) {
                    ClientThread tc = al.get(i);
                    try {
                        tc.sInput.close();
                        tc.sOutput.close();
                        tc.socket.close();
                    }
                    catch(IOException ioE) {
                        // not much I can do
                    }
                }
            }
            catch(Exception e) {
                display("Exception closing the server and clients: " + e);
            }
        }
        // something went bad
        catch (IOException e) {
            String msg = sdf.format(new Date()) + " Exception on new ServerSocket: " + e + "\n";
            display(msg);
        }
    }
    /*
     * For the GUI to stop the server
     */
    public void stop() {
        keepGoing = false;
        // connect to myself as Client to exit statement
        // Socket socket = serverSocket.accept();
        try {
            new Socket("localhost", port);
        }
        catch(Exception e) {
            // nothing I can really do
        }
    }
    /*
     * Display an event (not a message) to the console or the GUI
     */
    private void display(String msg) {
        String time = sdf.format(new Date()) + " " + msg;
        if(sg == null)
            System.out.println(time);
        else
            sg.appendEvent(time + "\n");
    }
    /*
     *  to broadcast a message to all Clients
     */
    private synchronized void broadcast(ChatMessage message) {
        // add HH:mm:ss and \n to the message
        String time = sdf.format(new Date());
        String messageLf = time + " " + message.getMessage();
        // display message on console or GUI
        if(sg == null)
            System.out.print(messageLf);
        else
            sg.appendRoom(messageLf);     // append in the room window

        // we loop in reverse order in case we would have to remove a Client
        // because it has disconnected
        for(int i = al.size(); --i >= 0;) {
            ClientThread ct = al.get(i);
            // try to write to the Client if it fails remove it from the list
            if(!ct.writeMsg(message)) {
                al.remove(i);
                display("Disconnected Client " + ct.user.getName() + " removed from the user list.");
            }
        }
    }

    // for a client who logoff using the LOGOUT message
    synchronized void remove(int id) {
        System.out.println("Trying to remove user");
        // scan the array list until we found the Id
        for(int i = 0; i < al.size(); ++i) {
            ClientThread ct = al.get(i);
            // found it
            if(ct.id == id) {
                System.out.println("Removing: " + ct.user.getName());
                al.remove(i);
                broadcast(new ChatMessage(ChatMessage.MESSAGE, "User <" + ct.user.getName() + "> left the room...\n", new User("Server", "localhost", -1)));
                broadcast(new ChatMessage(ChatMessage.USERLIST, ChatMessage.PUT, "New User Connected", getCurrentUsers()));
                return;
            }
        }

    }

    /*
     *  To run as a console application just open a console window and:
     * > java Server
     * > java Server portNumber
     * If the port number is not specified 1500 is used
     */
    public static void main(String[] args) {
        // start server on port 1500 unless a PortNumber is specified
        int portNumber = 1500;
        switch(args.length) {
            case 1:
                try {
                    portNumber = Integer.parseInt(args[0]);
                }
                catch(Exception e) {
                    System.out.println("Invalid port number.");
                    System.out.println("Usage is: > java Server [portNumber]");
                    return;
                }
            case 0:
                break;
            default:
                System.out.println("Usage is: > java Server [portNumber]");
                return;

        }
        // create a server object and start it
        MainServer server = new MainServer(portNumber);
        server.start();
    }

    private synchronized UserList getCurrentUsers() {
        UserList ul = new UserList();
        Date now = new Date();
        //writeMsg("List of the users connected at " + sdf.format(now));
        // scan al the users connected
        for(int i = 0; i < al.size(); ++i) {
            ClientThread ct = al.get(i);
            ul.addUser(ct.user);
            //writeMsg((i + 1) + ") " + ct.user.getName() + " for " + ct.user.getConnectionDuration(now));
        }
        return ul;
    }

    /** One instance of this thread will run for each client */
    class ClientThread extends Thread {
        // the socket where to listen/talk
        Socket socket;
        ObjectInputStream sInput;
        ObjectOutputStream sOutput;
        // my unique id (easier for disconnect)
        int id;
        // the only type of message a will receive
        ChatMessage cm;
        //User that is connected
        User user;

        // Constructor
        ClientThread(Socket socket) {
            // a unique id
            id = ++uniqueId;
            System.out.println("Creating new id: " + id);
            this.socket = socket;
			/* Creating both Data Stream */
            System.out.println("Thread trying to create Object Input/Output Streams");
            try
            {
                // create output first
                sOutput = new ObjectOutputStream(socket.getOutputStream());
                sInput  = new ObjectInputStream(socket.getInputStream());

                //Create a new user from the connection
                ChatMessage loginMessage = (ChatMessage) sInput.readObject();
                if(loginMessage.getType() == ChatMessage.LOGIN){
                    user = loginMessage.getUser();
                }else{
                    display("Error, no user was sent with the login request");
                    return;
                }
                display(user.getName() + " just connected.");
            }
            catch (IOException e) {
                display("Exception creating new Input/output Streams: " + e);
                return;
            }
            // have to catch ClassNotFoundException
            // but I read a String, I am sure it will work
            catch (ClassNotFoundException e) {
                System.out.println("Class not found exception while creating client thread: " + e.getMessage());
            }
            Date d = new Date();
            user.setConnected(d);
            user.setId(id);
            writeMsg(new ChatMessage(ChatMessage.USER,ChatMessage.PUT, "Heres a new user", user));
        }

        // what will run forever
        public void run() {
            // to loop until LOGOUT
            boolean keepGoing = true;
            while(keepGoing) {
                // read a String (which is an object)
                try {
                    cm = (ChatMessage) sInput.readObject();
                }
                catch (IOException e) {
                    display(user.getName() + " Exception reading Streams: " + e);
                    break;
                }
                catch(ClassNotFoundException e2) {
                    break;
                }
                // the message part of the ChatMessage
                String message = cm.getMessage();

                // Switch on the type of message receive
                switch(cm.getType()) {

                    case ChatMessage.MESSAGE:
                        System.out.println("User: " +user.getName() +" sent a message: " + cm.getMessage());
                        broadcast(new ChatMessage(ChatMessage.MESSAGE, message, user));
                        break;
                    case ChatMessage.LOGOUT:
                        display(user.getName() + " disconnected with a LOGOUT message: " + cm.getMessage());
                        keepGoing = false;
                        break;
                    case ChatMessage.USERLIST:
                        UserList ul = getCurrentUsers();
                        writeMsg(new ChatMessage(ChatMessage.USERLIST, ChatMessage.GET,"Requested Userlist",ul));
                        break;
                }
            }
            // remove myself from the arrayList containing the list of the
            // connected Clients
            remove(id);
            close();
        }


        // try to close everything
        private void close() {
            // try to close the connection
            try {
                if(sOutput != null){
                    sOutput.close();
                }
            } catch (IOException e) {
                System.out.println("IOException while close of output: " + e.getMessage());
            }
            try {
                if(sInput != null){
                    sInput.close();
                }
            } catch (IOException e) {
                System.out.println("IOException while close of input: " + e.getMessage());
            }
            try {
                if(socket != null){
                    socket.close();
                }
            }catch (IOException e) {
                System.out.println("IOException while close of socket: " + e.getMessage());
            }
        }

        /*
         * Write a String to the Client output stream
         */
        private boolean writeMsg(ChatMessage msg) {
            // if Client is still connected send the message to it
            if(!socket.isConnected()) {
                close();
                return false;
            }
            // write the message to the stream
            try {
                sOutput.writeObject(msg);
            }
            // if an error occurs, do not abort just inform the user
            catch(IOException e) {
                display("Error sending message to " + user.getName());
                display(e.toString());
            }
            return true;
        }
    }
}


