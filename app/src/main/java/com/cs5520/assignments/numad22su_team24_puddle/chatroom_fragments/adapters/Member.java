package com.cs5520.assignments.numad22su_team24_puddle.chatroom_fragments.adapters;

public class Member {
    String profileUrl;
    String username;

    public Member(String username, String profileUrl){
        this.username = username;
        this.profileUrl = profileUrl;
    }

    public String getUsername() {
        return username;
    }
    public String getProfileUrl() {
        return profileUrl;
    }
}
