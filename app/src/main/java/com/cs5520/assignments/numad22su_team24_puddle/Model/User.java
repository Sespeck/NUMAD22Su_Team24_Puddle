package com.cs5520.assignments.numad22su_team24_puddle.Model;

import java.util.HashMap;

public class User {
    private String id;
    private String username;
    private String bio;
    private String display_name;
    private String email;
    private String password;
    private String phone_number;
    private String profile_icon;
    private HashMap<String, String> my_puddles;

    // Constructors
    public User() {
    }

    public User(String id, String username, String password, String email, String display_name,
                String profile_icon, String bio, String phone_number, HashMap<String, String> my_puddles) {
        this.id = id;
        this.username = username;

        this.bio = bio;
        this.display_name = display_name;
        this.phone_number = phone_number;
        this.email = email;
        this.password = password;
        this.profile_icon = profile_icon;

        this.my_puddles = my_puddles;
    }

    public User(String id, String username, String password, String email) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.display_name = username;
        this.email = email;
        this.bio = "";
        this.profile_icon = "https://firebasestorage.googleapis.com/v0/b/android-chat-85561.appspot.com/o/blank_image.png?alt=media&token=9a6190a2-0e1b-4dbf-8479-4bcbffc30e00";
        this.phone_number = "";
        this.my_puddles = new HashMap<String, String>();
    }

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



    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getDisplay_name() {
        return display_name;
    }

    public void setDisplay_name(String display_name) {
        this.display_name = display_name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhone_number() {
        return phone_number;
    }

    public void setPhone_number(String phone_number) {
        this.phone_number = phone_number;
    }

    public String getProfile_icon() {
        return profile_icon;
    }

    public void setProfile_icon(String profile_icon) {
        this.profile_icon = profile_icon;
    }

    @Override
    public String toString() {
        return "User{" +
                "id='" + id + '\'' +
                ", username='" + username + '\'' +
                ", bio='" + bio + '\'' +
                ", display_name='" + display_name + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", phone_number='" + phone_number + '\'' +
                ", profile_icon='" + profile_icon + '\'' +
                ", my_puddles=" + my_puddles +
                '}';
    }

    public HashMap<String, Object> getUserMap() {
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("id", this.id);
        hashMap.put("username", this.username);
        hashMap.put("display_name", this.username);
        hashMap.put("password", this.password);
        hashMap.put("email", this.email);
        hashMap.put("bio", this.bio);
        hashMap.put("profile_icon", this.profile_icon);
        hashMap.put("phone_number", this.phone_number);
        hashMap.put("my_puddles", this.my_puddles);
        return hashMap;
    }
}
