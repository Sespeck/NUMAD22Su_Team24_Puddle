package com.cs5520.assignments.numad22su_team24_puddle.chatroom_fragments.adapters;

public class Message {
    String body;
    String timestamp;
    String username;
    String profilePicture;
    String dbKey;
    Boolean isImage;

    public Message(String username, String text, String datetime, String profilePicture, String dbKey, Boolean isImage){
        this.body = text;
        this.timestamp = datetime;
        this.username = username;
        this.profilePicture = profilePicture;
        this.dbKey = dbKey;
        this.isImage = isImage;
    }

    public String getDbKey() {
        return dbKey;
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
