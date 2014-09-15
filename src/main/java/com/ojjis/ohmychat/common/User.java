package com.ojjis.ohmychat.common;

import java.io.Serializable;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: johan
 * Date: 11/23/13
 * Time: 11:42 AM
 */
public class User implements Serializable{
    private int id;
    private Date connected;
    private String name;
    private boolean isClient;

    public User(String name){
        this.name = name;
        this.isClient = false;
    }
    public User(String name, String ipAddress, int id){
        this.name = name;
        this.id = id;
        this.isClient = false;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setConnected(Date connected) {
        this.connected = connected;
    }

    public Date getConnected() {
        return connected;
    }

    public String getConnectionDuration(Date d2) {
        long diff = d2.getTime() - connected.getTime();
        long diffMinutes = diff / (60 * 1000) % 60;
        long diffHours = diff / (60 * 60 * 1000) % 24;

        return diffHours + "h and " + diffMinutes +"m";
    }

    public boolean isClient() {
        return this.isClient;
    }

    public int getId() {
        return this.id;
    }

    public void setId(int id){
        this.id  = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        if (id != user.id) return false;
        if (!name.equals(user.name)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + connected.hashCode();
        result = 31 * result + name.hashCode();
        result = 31 * result + (isClient ? 1 : 0);
        return result;
    }

    public void setIsClient(boolean client) {
        isClient = client;
    }
}
