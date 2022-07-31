package com.cs5520.assignments.numad22su_team24_puddle.Model;

public class User {
    public String id;
    public String username;

    // Constructors
    public User(){}
    public User(String id, String username) {
        this.id = id;
        this.username = username;
    }

    // getters and setters
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
}
