package com.cs5520.assignments.numad22su_team24_puddle;

import android.graphics.Bitmap;

public class Puddle {
    private final String name;
    private final String description;
    private final Bitmap displayImage;
    private double longitude, latitude;

    Puddle(String name, String description, Bitmap displayImage) {
        this.name = name;
        this.description = description;
        this.displayImage = displayImage;
    }

    Puddle(String name, String description, Bitmap displayImage, double longitude, double latitude) {
        this.name = name;
        this.description = description;
        this.displayImage = displayImage;
        this.longitude = longitude;
        this.latitude = latitude;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Bitmap getDisplayImage() {
        return displayImage;
    }

    public double getLongitude() {
        return longitude;
    }

    public double getLatitude() {
        return latitude;
    }
}
