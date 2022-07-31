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
    int attendanceCount;


    public Event(String name, String datetime, Location location, String description, Bitmap backgroundImg, int attendanceCount){
        this.name = name;
        this.datetime = datetime;
        this.location = location;
        this.description = description;
        this.backgroundImg = backgroundImg;
        this.attendanceCount = attendanceCount;
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

    public int getAttendanceCount(){
        return attendanceCount;
    }
}
