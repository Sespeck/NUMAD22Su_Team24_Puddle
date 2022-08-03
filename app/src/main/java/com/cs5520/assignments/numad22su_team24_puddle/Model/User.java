package com.cs5520.assignments.numad22su_team24_puddle.Model;

import java.util.HashMap;

public class User {
    public String id;
    public String username;
    public HashMap<String, String> my_puddles;

    // Constructors
    public User(){}

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public HashMap<String, String> getMy_puddles() {
        return my_puddles;
    }

    public void setMy_puddles(HashMap<String, String> my_puddles) {
        this.my_puddles = my_puddles;
    }

    public User(String id, String username, HashMap<String, String> my_puddles) {
        this.id = id;
        this.username = username;
        this.my_puddles = my_puddles;
    }
}
