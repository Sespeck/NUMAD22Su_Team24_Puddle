package com.cs5520.assignments.numad22su_team24_puddle.model;

import com.google.android.gms.maps.model.LatLng;

public class PuddleMarker {

    private String name, puddleId, category;
    private LatLng latLng;
    private int memberCount;

    public PuddleMarker(String name, String puddleId, String category, LatLng latLng, int memberCount) {
        this.name = name;
        this.puddleId = puddleId;
        this.category = category;
        this.latLng = latLng;
        this.memberCount = memberCount;
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
}
