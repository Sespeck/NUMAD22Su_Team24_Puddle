package com.cs5520.assignments.numad22su_team24_puddle;

import android.graphics.Bitmap;

public class Puddle {
    private final String name;
    private final String description;
    private final Bitmap displayImage;

    Puddle(String name, String description, Bitmap displayImage) {
        this.name = name;
        this.description = description;
        this.displayImage = displayImage;
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
}
