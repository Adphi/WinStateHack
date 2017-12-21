package fr.wcs.winstatehack.Models;

import java.util.HashMap;

/**
 * Created by adphi on 21/12/17.
 */

public class UserModel {
    private String name = "";
    private HashMap<String, Boolean> fires = new HashMap<>();
    private String uid = "";


    public UserModel() {
    }

    public UserModel(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public HashMap<String, Boolean> getFires() {
        return fires;
    }

    public void setFires(HashMap<String, Boolean> fires) {
        this.fires = fires;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}
