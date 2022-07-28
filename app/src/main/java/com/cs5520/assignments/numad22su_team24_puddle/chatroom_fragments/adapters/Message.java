package com.cs5520.assignments.numad22su_team24_puddle.chatroom_fragments.adapters;

import android.graphics.Bitmap;

public class Message {
    String text;
    String datetime;
    String username;
    Bitmap profilePicture;

    public Message(String username, String text, String datetime, Bitmap profilePicture){
        this.text = text;
        this.datetime = datetime;
        this.username = username;
        this.profilePicture = profilePicture;
    }

    public String getDatetime() {
        return datetime;
    }

    public Bitmap getProfilePicture() {
        return profilePicture;
    }

    public String getText() {
        return text;
    }

    public String getUsername() {
        return username;
    }
}
