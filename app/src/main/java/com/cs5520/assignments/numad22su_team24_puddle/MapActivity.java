package com.cs5520.assignments.numad22su_team24_puddle;


import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.cs5520.assignments.numad22su_team24_puddle.services.LocationService;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;


public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {
    private static final String TAG = "MapActivity";
    private static final int REQUEST_CODE_FINE_LOCATION = 2;
    private GoogleMap mMap;
    private Geocoder geocoder;
    private int ACCESS_LOCATION_REQUEST_CODE = 1001;
    FusedLocationProviderClient fusedLocationProviderClient;
    LocationRequest locationRequest;
    LocationCallback locationCallback;
    Marker userLocationMarker;
    Circle userLocationAccuracyCircle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        geocoder = new Geocoder(this);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        locationRequest = LocationRequest.create();
        locationRequest.setInterval(500);
        locationRequest.setFastestInterval(500);
        locationRequest.setPriority(Priority.PRIORITY_HIGH_ACCURACY);

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                Log.d(TAG, "onLocationResult: " + locationResult.getLastLocation());
                if (mMap != null) {
                    setUserLocationMarker(locationResult.getLastLocation());
                }
            }
        };


    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

//        LatLng sydney = new LatLng(-33.852, 151.211);
//        googleMap.addMarker(new MarkerOptions()
//                .position(sydney)
//                .title("Marker in Sydney"));
//        googleMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            LocationService locationService = new LocationService();
            locationService.requestPermission(this, REQUEST_CODE_FINE_LOCATION);
        }
        mMap.setMyLocationEnabled(true);

    }


    @Override
    protected void onStart() {
        super.onStart();
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            startLocationUpdates();
        } else {
            LocationService locationService = new LocationService();
            locationService.requestPermission(this, REQUEST_CODE_FINE_LOCATION);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        stopLocationUpdates();
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_FINE_LOCATION) {

            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Location access successfully granted", Toast.LENGTH_SHORT).show();
            } else {
                //Permission NOT granted
                if (!ActivityCompat.shouldShowRequestPermissionRationale(MapActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                    new AlertDialog.Builder(MapActivity.this)
                            .setMessage("Please enable location access in the settings")
                            .setPositiveButton("Go to settings", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    LocationService locationService = null;
                                    locationService.locationSetting(MapActivity.this);
                                }
                            })
                            .setNegativeButton("Cancel", null)
                            .setCancelable(false)
                            .show();

                } else {
                    Toast.makeText(this, "Location access is not granted", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
    private void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
    }

    private void stopLocationUpdates() {
        fusedLocationProviderClient.removeLocationUpdates(locationCallback);
    }

    private void setUserLocationMarker(Location location) {

        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());

        if (userLocationMarker == null) {
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(latLng);
            markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.user));
//            markerOptions.rotation(location.getBearing());
            markerOptions.anchor((float) 0.5, (float) 0.5);
            userLocationMarker = mMap.addMarker(markerOptions);
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17));
        } else  {
            userLocationMarker.setPosition(latLng);
//            userLocationMarker.setRotation(location.getBearing());
//            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17));
        }
//        if (userLocationAccuracyCircle == null) {
//            CircleOptions circleOptions = new CircleOptions();
//            circleOptions.center(latLng);
//            circleOptions.strokeWidth(4);
//            circleOptions.strokeColor(Color.argb(255, 255, 0, 0));
//            circleOptions.fillColor(Color.argb(32, 255, 0, 0));
//            circleOptions.radius(location.getAccuracy());
//            userLocationAccuracyCircle = mMap.addCircle(circleOptions);
//        } else {
//            userLocationAccuracyCircle.setCenter(latLng);
//            userLocationAccuracyCircle.setRadius(location.getAccuracy());
//        }
    }


}



