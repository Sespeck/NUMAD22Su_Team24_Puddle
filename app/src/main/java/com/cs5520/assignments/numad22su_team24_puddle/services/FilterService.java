package com.cs5520.assignments.numad22su_team24_puddle.services;

import android.location.Location;

import com.cs5520.assignments.numad22su_team24_puddle.Model.Puddles;

import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
        Location.distanceBetween(userLat, userLong, endLatitude, endLongitude, distanceArray);
        double distance = distanceArray[0];
        return distance <= range;
    }

    public static Stream<Puddles> withinRangePuddles(Stream<Puddles> puddleList, double range, double currentLat, double currentLong) {
        return puddleList.filter(x -> withinRange(x, currentLat, currentLong, range));
    }

    public static Stream<Puddles> withinDatePuddles(Stream<Puddles> puddleList, String startDate, String endDate) {
        // need event date information in Puddles objects
        return puddleList;
    }

    public static Stream<Puddles> selectedCategoryPuddles(Stream<Puddles> puddleList, List<String> categories) {
        return puddleList.filter(x -> categories.contains(x.getCategory()));
    }

    public static List<Puddles> filteredPuddles(List<Puddles> puddleList, double range, double currentLat, double currentLong, List<String> categories, String startDate, String endDate) {
        return withinDatePuddles(
                withinRangePuddles(
                        selectedCategoryPuddles(
                                puddleList.stream(), categories),
                        range, currentLat, currentLong),
                startDate, endDate
        ).collect(Collectors.toList());
    }
}
