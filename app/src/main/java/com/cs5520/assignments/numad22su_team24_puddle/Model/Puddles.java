package com.cs5520.assignments.numad22su_team24_puddle.Model;

import java.util.HashMap;

public class Puddles {
    private String bannerUrl;
    private String bio;
    private String category;
    private String isPrivate;
    private String name;
    private String range;
    private int count;
    private HashMap<String, String> Location = new HashMap<>();

    // Constructors
    public Puddles(){}

    public Puddles(String bannerUrl, String bio, String category, String isPrivate, String name, String range, int count, HashMap<String, String> location) {
        this.bannerUrl = bannerUrl;
        this.bio = bio;
        this.category = category;
        this.isPrivate = isPrivate;
        this.name = name;
        this.range = range;
        this.count = count;
        Location = location;
    }

    // Getters and Setters
    public String getBannerUrl() {
        return bannerUrl;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public HashMap<String, String> getLocation() {
        return Location;
    }

    public void setLocation(HashMap<String, String> location) {
        Location = location;
    }

    public void setBannerUrl(String bannerUrl) {
        this.bannerUrl = bannerUrl;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getIsPrivate() {
        return isPrivate;
    }

    public void setIsPrivate(String isPrivate) {
        this.isPrivate = isPrivate;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRange() {
        return range;
    }

    public void setRange(String range) {
        this.range = range;
    }
}
