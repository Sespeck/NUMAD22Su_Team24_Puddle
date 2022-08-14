package com.cs5520.assignments.numad22su_team24_puddle.chatroom_fragments.adapters;

import java.util.HashSet;

public class Event {
    String id;
    String name;
    String startingDatetime;
    String endingDatetime;
    String location;
    String description;
    String backgroundImgUri;
    int attendanceCount;
    String createdBy;
    HashSet<String> hasRsvped;


    public Event(String name, String startingDatetime, String endingDatetime, String location,
                 String description, String backgroundImgUri, int attendanceCount, String id, String createdBy, HashSet<String> hasRsvped){
        this.name = name;
        this.startingDatetime = startingDatetime;
        this.endingDatetime = endingDatetime;
        this.location = location;
        this.description = description;
        this.backgroundImgUri = backgroundImgUri;
        this.attendanceCount = attendanceCount;
        this.id = id;
        this.createdBy = createdBy;
        this.hasRsvped = hasRsvped;
    }

    public String getName() {
        return name;
    }

    public String getBackgroundImgUri() {
        return backgroundImgUri;
    }

    public String getEndingDatetime() {
        return endingDatetime;
    }

    public String getStartingDatetime() {
        return startingDatetime;
    }

    public String getLocation(){
        return location;
    }

    public String getDescription() {
        return description;
    }

    public int getAttendanceCount(){
        return attendanceCount;
    }

    public HashSet<String> getHasRsvped() {
        return hasRsvped;
    }
}
