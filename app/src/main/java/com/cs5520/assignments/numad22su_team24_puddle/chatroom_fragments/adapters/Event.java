package com.cs5520.assignments.numad22su_team24_puddle.chatroom_fragments.adapters;

import android.graphics.Bitmap;
import android.location.Location;

public class Event {
    Boolean isOnline;
    String name;
    String datetime;
    Location location;
    String description;
    Bitmap backgroundImg;

    public Event(String name, String datetime, Location location, String description, Bitmap backgroundImg){
        this.name = name;
        this.datetime = datetime;
        this.location = location;
        this.description = description;
        this.backgroundImg = backgroundImg;
    }

    public String getName() {
        return name;
    }

    public String getDatetime() {
        return datetime;
    }

    public Location getLocation(){
        return location;
    }

    public String getDescription() {
        return description;
    }

    public Bitmap getBackgroundImg() {
        return backgroundImg;
    }
}
