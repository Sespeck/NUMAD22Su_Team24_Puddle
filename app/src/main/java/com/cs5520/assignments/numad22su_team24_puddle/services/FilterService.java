package com.cs5520.assignments.numad22su_team24_puddle.services;

import android.location.Location;

import com.cs5520.assignments.numad22su_team24_puddle.Model.Puddles;

import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class FilterService {

    public static boolean withinRange(Puddles puddle,
                                      double endLatitude,
                                      double endLongitude,
                                      double range
    ) {
        float[] distanceArray = new float[1];
        HashMap<String, String> location = puddle.getLocation();
        double userLat = Double.parseDouble(Objects.requireNonNull(location.getOrDefault("latitude", "0.0")));
        double userLong = Double.parseDouble(Objects.requireNonNull(location.getOrDefault("longitude", "0.0")));
        Location.distanceBetween(userLat, userLong,endLatitude, endLongitude, distanceArray);
        double distance = distanceArray[0];
        return distance <= range;
    }

    public static List<Puddles> withinRangePuddles(List<Puddles> puddleList, double range, double currentLat, double currentLong){
        return puddleList.stream().filter(x->withinRange(x,currentLat,currentLong,range)).collect(Collectors.toList());
    }

    public static List<Puddles> withinDatePuddles(List<Puddles> puddleList){
        return null;
    }

    public static List<Puddles> withinCategoriesPuddles(List<Puddles> puddleList){
        return null;
    }
}
