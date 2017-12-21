package fr.wcs.winstatehack.Models;

import java.util.HashMap;

/**
 * Created by adphi on 21/12/17.
 */

public class FireModel {
    private HashMap<String, Integer> users = new HashMap<>();
    private String uid = "";

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
}
