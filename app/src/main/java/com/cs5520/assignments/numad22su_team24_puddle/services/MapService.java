package com.cs5520.assignments.numad22su_team24_puddle.services;


import android.util.Log;

import com.cs5520.assignments.numad22su_team24_puddle.Model.Puddle;
import com.cs5520.assignments.numad22su_team24_puddle.Model.PuddleMarker;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class MapService {


    public static List<PuddleMarker> getPuddleList(HashMap<String, Puddle> allPuddlesData) {
//        List<PuddleMarker> puddleList = new ArrayList<>();
//
//            puddleList.add(new PuddleMarker("Volleyball Sat Out", "vso111", "SPORTS", new LatLng(43.66861, -70.29778), 10, "dsfds"));
//            puddleList.add(new PuddleMarker("Money 2022", "m111", "FINANCE", new LatLng(43.667761, -70.298552), 10, "dsfds"));
//            puddleList.add(new PuddleMarker("Reading Inspiration", "ri111", "BOOK", new LatLng(43.670074, -70.294990), 10, "dsfds"));
//            puddleList.add(new PuddleMarker("Global Traveller", "gt111", "TRAVEL",  new LatLng(43.667777, -70.296471), 10, "dsfds"));
//            puddleList.add(new PuddleMarker("Pop Concert Fans", "pcf111", "MUSIC", new LatLng(43.668149, -70.297201), 20, "dsfds"));
//
//            return puddleList;

        List<PuddleMarker> puddleList = new ArrayList<>();

        for (Map.Entry<String, Puddle> set :
                allPuddlesData.entrySet()) {
            HashMap<String, String> location = set.getValue().getLocation();
            double lat = Double.parseDouble(Objects.requireNonNull(location.getOrDefault("latitude", "43.66861")));
            double lng = Double.parseDouble(Objects.requireNonNull(location.getOrDefault("longitude", "-70.29778")));
            puddleList.add(new PuddleMarker(set.getValue().getName(),
                    set.getKey(),
                    set.getValue().getCategory(),
                    new LatLng(lat, lng),
                    set.getValue().getCount(),
                    set.getValue().getBannerUrl(),
                    set.getValue().getBio()));
        }
        return puddleList;
    }

}
