package com.cs5520.assignments.numad22su_team24_puddle.chatroom_fragments.adapters;

public class Message {
    String body;
    String timestamp;
    String username;
    String profilePicture;

    public Message(String username, String text, String datetime, String profilePicture){
        this.body = text;
        this.timestamp = datetime;
        this.username = username;
        this.profilePicture = profilePicture;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public String getProfilePicture() {
        return profilePicture;
    }

    public String getBody() {
        return body;
    }

    public String getUsername() {
        return username;
    }
}
