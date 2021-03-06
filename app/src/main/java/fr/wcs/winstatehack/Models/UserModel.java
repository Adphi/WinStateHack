package fr.wcs.winstatehack.Models;

import java.util.HashMap;

public class UserModel {
    private String name = "";
    private HashMap<String, Boolean> fires = new HashMap<>();
    private String uid = "";


    public UserModel() {
    }

    public UserModel(String name, String uid) {
        this.name = name;
        this.fires= new HashMap<>();
        this.uid = uid;
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
