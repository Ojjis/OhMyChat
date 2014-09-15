package com.ojjis.ohmychat.common;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * User: johan
 * Date: 11/27/13
 * Time: 8:23 AM
 */
public class UserList implements Serializable{

    private ArrayList<User> users = new ArrayList<User>();

    public UserList(){

    }
    public UserList(ArrayList<User> us){
        if(us == null){
            this.users = new ArrayList<User>();
        }else{
            this.users = us;
        }
    }

    public ArrayList<User> getUsers(){
        return this.users;
    }

    public void addUsers(ArrayList<User> us){
        if(us == null){
           //Do nothing
        }else if(this.users == null){
            this.users = us;
        }else{
            for (User u : us) {
                users.add(u);
            }
        }
    }

    public void addUser(User u){
        if(this.users == null){
            this.users = new ArrayList<User>();
        }
        this.users.add(u);
    }
}
