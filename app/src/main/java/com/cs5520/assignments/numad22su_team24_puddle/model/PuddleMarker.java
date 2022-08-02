package com.cs5520.assignments.numad22su_team24_puddle.model;

import com.google.android.gms.maps.model.LatLng;

public class PuddleMarker {

    private String name, puddleId, category;
    private LatLng latLng;
    private int memberCount, position, background;

    public PuddleMarker(String name, String puddleId, String category, LatLng latLng, int memberCount, int background) {
        this.name = name;
        this.puddleId = puddleId;
        this.category = category;
        this.latLng = latLng;
        this.memberCount = memberCount;
        this.background = background;
    }

    public String getName() {
        return name;
    }

    public String getPuddleId() {
        return puddleId;
    }

    public String getCategory() {
        return category;
    }

    public LatLng getLatLng() {
        return latLng;
    }

    public int getMemberCount() {
        return memberCount;
    }

    public int getPosition() {
        return position;
    }

    public int getBackground() {
        return background;
    }

    public void setPosition(int position) {
        this.position = position;
    }
}
