package com.ojjis.ohmychat.common.message;

import com.ojjis.ohmychat.common.User;
import com.ojjis.ohmychat.common.UserList;

import java.io.Serializable;

/**
 * This is the common chat message class that is used for communication throughout the
 * whole chat. It has some different actions and types to distinguish between the different messages.
 */

public class ChatMessage implements Serializable {

    protected static final long serialVersionUID = 1112122200L;

    public static final int LOGIN = 0, USERLIST = 1, MESSAGE = 2, LOGOUT = 3, USER=4;
    public static final int GET = 10, PUT = 11;

    private int action;
    private int type;
    private String message;
    private UserList userList;
    private User user;

    // Standard constructor
    public ChatMessage(int type, String message) {
        this.type = type;
        this.message = message;
    }

    // Standard constructor
    public ChatMessage(int type, String message, User u) {
        this.type = type;
        this.user = u;
        this.message = message;
    }

    // User modification action
    public ChatMessage(int type, int action, String message, User user) {
        this.type = type;
        this.message = message;
        this.action = action;
        this.user = user;
    }

    // UserList fetch / send constructor
    public ChatMessage(int type, int action, String message, UserList userList){
        this.type = type;
        this.action = action;
        this.message = message;
        this.userList = userList;
    }

    // Getters
    public int getType() {
        return type;
    }
    public String getMessage() {
        return message;
    }

    public UserList getUserList() {
        return this.userList;
    }

    public User getUser() {
        return user;
    }

    public int getAction() {
        return action;
    }
}

