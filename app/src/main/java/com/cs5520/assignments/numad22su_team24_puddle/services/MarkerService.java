package com.cs5520.assignments.numad22su_team24_puddle.services;

import com.cs5520.assignments.numad22su_team24_puddle.R;
import com.cs5520.assignments.numad22su_team24_puddle.model.PuddleMarker;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MarkerService {

    public void addMarker(PuddleMarker puddleMarker, GoogleMap mMap){
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(puddleMarker.getLatLng());
        String category = puddleMarker.getCategory();
        switch (category){
            case "SPORTS": markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.sports_marker));
                break;
            case "MUSIC": markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.music_marker));
                break;
            case "FINANCE": markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.finance_marker));
                break;
            case "BOOK": markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.book_marker));
                break;
            case "TRAVEL": markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.travel_marker));
                break;
        }
        markerOptions.title(puddleMarker.getPuddleId());
        markerOptions.anchor((float) 0.5, (float) 0.5);
        mMap.addMarker(markerOptions);

    }




}
