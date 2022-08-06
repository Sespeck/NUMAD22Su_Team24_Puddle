package com.cs5520.assignments.numad22su_team24_puddle.Model;

public class PuddleMembers {
    private String profile_url;
    private String username;

    public PuddleMembers(String profile_url, String username) {
        this.profile_url = profile_url;
        this.username = username;
    }

    public String getProfile_url() {
        return profile_url;
    }

    public void setProfile_url(String profile_url) {
        this.profile_url = profile_url;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
