package com.cs5520.assignments.numad22su_team24_puddle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.cs5520.assignments.numad22su_team24_puddle.Utils.LocationPermissionActivity;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;

import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

public class SelectLocation extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnCameraMoveListener, GoogleMap.OnCameraMoveStartedListener, GoogleMap.OnCameraIdleListener {
    private GoogleMap mMap;
    private boolean mapInitiated = true;
    SupportMapFragment mapFragment;

    String selectedLocation;
    public static DecimalFormat df = new DecimalFormat("###.####");
    double pin_lat, pin_lng;

    FusedLocationProviderClient fusedLocationProviderClient;
    LocationRequest locationRequest;
    LocationCallback locationCallback;

    TextView selectLocationText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_location);

        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.select_location);
        mapFragment.getMapAsync(this);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        locationRequest = LocationRequest.create();
        locationRequest.setInterval(500);
        locationRequest.setFastestInterval(500);
        locationRequest.setPriority(Priority.PRIORITY_HIGH_ACCURACY);

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                if (mMap != null) {
                    if (mapInitiated == true) {
                        Location location = locationResult.getLastLocation();
                        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17));
                        mapInitiated = false;
                    }

                }
            }
        };

        selectLocationText = findViewById(R.id.select_location_text);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(LocationPermissionActivity.checkMapServices(this)){
            if(LocationPermissionActivity.locationPermissionGranted){
                startLocationUpdates();
            }
            else{
                LocationPermissionActivity.requestPermission(this, LocationPermissionActivity.REQUEST_CODE_FINE_LOCATION);
            }
        }
    }
    @Override
    protected void onStop() {
        super.onStop();
        stopLocationUpdates();
    }
    private void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
        Toast.makeText(this, "Locating to your current position...", Toast.LENGTH_LONG).show();
    }

    private void stopLocationUpdates() {
        fusedLocationProviderClient.removeLocationUpdates(locationCallback);
    }



    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mMap.setMyLocationEnabled(true);
        mMap.setOnCameraIdleListener(this);
        mMap.setOnCameraMoveListener(this);
        mMap.setOnCameraMoveStartedListener(this);
    }

    @Override
    public void onCameraIdle() {

        pin_lat = mMap.getCameraPosition().target.latitude;
        pin_lng = mMap.getCameraPosition().target.longitude;

        Log.d("select location", "onCameraIdle: pin_lat " + pin_lat);
        Log.d("select location", "onCameraIdle: pin_lng " + pin_lng);

        String googleMapsURL = "https://maps.googleapis.com/maps/api/geocode/xml?latlng=" +
                df.format(pin_lat) + "," + df.format(pin_lng) +
                "&key="+ getString(R.string.google_maps_api_key);
        XPathFactory factory = XPathFactory.newInstance();
        XPath xpath = factory.newXPath();
        final Handler handler = new Handler();
        Runnable runnable = () -> {
            URL url = null;
            try {
                url = new URL(googleMapsURL);
                InputStream stream = url.openStream();
                List<String> addresses = new ArrayList<>();
                if (stream != null) {
                    InputSource inputXml = new InputSource(stream);
                    NodeList nodes = (NodeList) xpath.evaluate(
                            "//formatted_address",
                            inputXml,
                            XPathConstants.NODESET);

                    for (int i = 0, n = nodes.getLength(); i < n; i++) {
                        String formattedAddress = nodes.item(i).getTextContent();
                        addresses.add(formattedAddress);
                        Log.d("select location", formattedAddress);
                    }
                    selectedLocation = addresses.get(0);
                    Log.d("select location", "onCameraIdle: " + addresses.get(0));

                    handler.post(() -> {
                        selectLocationText.setText(addresses.get(0));
                    });

                } else {
                    Toast.makeText(this, "Google Maps GeoLocation not working ...", Toast.LENGTH_SHORT).show();
                }

            } catch (IOException | XPathExpressionException | IndexOutOfBoundsException e) {
                e.printStackTrace();
            }

        };
        Thread thread = new Thread(runnable);
        thread.start();

    }

    @Override
    public void onCameraMove() {
    }

    @Override
    public void onCameraMoveStarted(int i) {
    }

    public void sendSelectedLocation(View v){
        Bundle extras = new Bundle();
        extras.putDouble("latitude",pin_lat);
        extras.putDouble("longitude",pin_lng);
        extras.putString("selectedLocation", selectedLocation);

        Intent intent = new Intent();
        intent.putExtras(extras);

        Log.d("select location", "sendSelectedLocation: " + selectedLocation + ' ' + pin_lat+ ' '+ pin_lng);
        setResult(RESULT_OK, intent);

        SelectLocation.super.onBackPressed();
        finish();
    }
}