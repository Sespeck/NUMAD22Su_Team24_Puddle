package com.cs5520.assignments.numad22su_team24_puddle.services;

import android.location.Location;

import com.cs5520.assignments.numad22su_team24_puddle.Model.Puddle;

import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FilterService {


    public static boolean withinRange(Puddle puddle,
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

    public static Stream<Puddle> withinRangePuddles(Stream<Puddle> puddleList, double range, double currentLat, double currentLong) {
        return puddleList.filter(x -> withinRange(x, currentLat, currentLong, range));
    }


    public static Stream<Puddle> selectedMemberCount(Stream<Puddle> puddleList, int memberCount) {
        return puddleList.filter(x -> x.getCount() <= memberCount);
    }

    public static Stream<Puddle> selectedCategoryPuddles(Stream<Puddle> puddleList, List<String> categories) {
        return puddleList.filter(x -> categories.contains(x.getCategory()));
    }

    public static Stream<Puddle> isGlobalPuddles(Stream<Puddle> puddleList, boolean globalSwitch) {
        return puddleList.filter(x -> !Boolean.parseBoolean(x.getIsGlobal()) || globalSwitch);
    }

    public static Stream<Puddle> privateFilter(Stream<Puddle> puddleList) {
        return puddleList.filter(x -> !Boolean.parseBoolean(x.getIsPrivate()));
    }

    public static Stream<Puddle> showPrivateInNearMe(Stream<Puddle> puddleList, boolean privateSwitch) {
        return puddleList.filter(x -> !Boolean.parseBoolean(x.getIsPrivate()) || privateSwitch);
    }

    public static List<Puddle> filteredPuddles(List<Puddle> puddleList, double range, double currentLat, double currentLong, List<String> categories, boolean globalSwitch, int memberCount) {
        return
                selectedMemberCount(
                        selectedCategoryPuddles(
                                withinRangePuddles(
                                        isGlobalPuddles(privateFilter(puddleList.stream())
                                                , globalSwitch)
                                        , range, currentLat, currentLong)
                                , categories)
                        , memberCount)
                        .collect(Collectors.toList());
    }

    public static List<Puddle> filteredPuddlesForMyPuddles(List<Puddle> puddleList, List<String> categories, boolean globalSwitch, int memberCount, boolean privateSwitch) {
        return
                selectedMemberCount(
                        selectedCategoryPuddles(
                                isGlobalPuddles(
                                        showPrivateInNearMe(puddleList.stream(), privateSwitch)
                                        , globalSwitch)
                                , categories)
                        , memberCount)
                        .collect(Collectors.toList());
    }
}
