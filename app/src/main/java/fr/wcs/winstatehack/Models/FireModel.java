package fr.wcs.winstatehack.Models;

import java.util.HashMap;

/**
 * Created by adphi on 21/12/17.
 */

public class FireModel {
    private HashMap<String, Integer> users = new HashMap<>();
    private String uid = "";
    private int life = 0;

    public FireModel() {
    }

    public HashMap<String, Integer> getUsers() {
        return users;
    }

    public void setUsers(HashMap<String, Integer> users) {
        this.users = users;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public int getLife() {
        return life;
    }

    public void setLife(int life) {
        this.life = life;
    }
}
