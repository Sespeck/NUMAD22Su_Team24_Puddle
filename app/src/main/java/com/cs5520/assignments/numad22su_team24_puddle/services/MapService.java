package com.cs5520.assignments.numad22su_team24_puddle.services;

import android.location.Location;

import com.cs5520.assignments.numad22su_team24_puddle.Model.PuddleMarker;
import com.cs5520.assignments.numad22su_team24_puddle.Puddle;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class MapService {

    public List<PuddleMarker> getPuddleList() {
        List<PuddleMarker> puddleList = new ArrayList<>();

        // Dummy data
        puddleList.add(new PuddleMarker("Volleyball Sat Out", "vso111", "SPORTS", new LatLng(43.66861, -70.29778), 10, 3232));
        puddleList.add(new PuddleMarker("Money 2022", "m111", "FINANCE", new LatLng(43.667761, -70.298552), 10, 3232));
        puddleList.add(new PuddleMarker("Reading Inspiration", "ri111", "BOOK", new LatLng(43.670074, -70.294990), 10, 3232));
        puddleList.add(new PuddleMarker("Global Traveller", "gt111", "TRAVEL", new LatLng(43.667777, -70.296471), 10, 3232));
        puddleList.add(new PuddleMarker("Pop Concert Fans", "pcf111", "MUSIC", new LatLng(43.668149, -70.297201), 20, 3232));

        return puddleList;
    }

    public static boolean withinRange(double startLatitude,
                                      double startLongitude,
                                      double endLatitude,
                                      double endLongitude,
                                      double range
    ) {
        float[] distanceArray = new float[1];
        Location.distanceBetween(startLatitude, startLongitude,endLatitude, endLongitude, distanceArray);
        double distance = distanceArray[0];
        return distance <= range;
    }

    public static List<Puddle> withinRangePuddles(List<Puddle> puddleList, double range, double currentLat, double currentLong){
        return puddleList.stream().filter(x->withinRange(x.getLatitude(),x.getLongitude(),currentLat,currentLong,range)).collect(Collectors.toList());
    }
}
