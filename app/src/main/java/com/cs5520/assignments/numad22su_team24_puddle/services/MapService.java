package com.cs5520.assignments.numad22su_team24_puddle.services;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.provider.Settings;

import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;

import com.cs5520.assignments.numad22su_team24_puddle.model.PuddleMarker;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

public class MapService {

    public List<PuddleMarker> getPuddleList(){
        List<PuddleMarker> puddleList = new ArrayList<>();

        // Dummy data
        puddleList.add(new PuddleMarker("Volleyball Sat Out", "vso111", "SPORTS", new LatLng(43.66861, -70.29778), 10));
        puddleList.add(new PuddleMarker("Money 2022", "m111", "FINANCE", new LatLng(43.667761, -70.298552),10 ));
        puddleList.add(new PuddleMarker("Reading Inspiration", "ri111", "BOOK", new LatLng(43.670074, -70.294990),10 ));
        puddleList.add(new PuddleMarker("Global Traveller", "gt111", "TRAVEL", new LatLng(43.667777, -70.296471), 10));
        puddleList.add(new PuddleMarker("Pop Concert Fans", "pcf111", "MUSIC", new LatLng(43.668149, -70.297201), 20));

        return puddleList;
    }
}
