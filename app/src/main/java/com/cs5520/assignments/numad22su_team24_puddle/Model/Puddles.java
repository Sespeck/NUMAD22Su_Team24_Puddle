package com.cs5520.assignments.numad22su_team24_puddle.Model;

import java.util.HashMap;

public class Puddles {
    private String bannerUrl;
    private String bio;
    private String category;
    private String isPrivate;
    private String name;
    private String range;

    public Puddles(){}
    public Puddles(String bannerUrl, String bio, String category, String isPrivate, String name, String range) {
        this.bannerUrl = bannerUrl;
        this.bio = bio;
        this.category = category;
        this.isPrivate = isPrivate;
        this.name = name;
        this.range = range;
    }

    public String getBannerUrl() {
        return bannerUrl;
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
