package com.cs5520.assignments.numad22su_team24_puddle;


import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.viewpager.widget.ViewPager;


import com.cs5520.assignments.numad22su_team24_puddle.Model.Puddle;
import com.cs5520.assignments.numad22su_team24_puddle.Model.PuddleMarker;
import com.cs5520.assignments.numad22su_team24_puddle.Utils.FirebaseDB;
import com.cs5520.assignments.numad22su_team24_puddle.Utils.LocationPermissionActivity;
import com.cs5520.assignments.numad22su_team24_puddle.Utils.Util;
import com.cs5520.assignments.numad22su_team24_puddle.chatroom_fragments.MessageNotification;
import com.cs5520.assignments.numad22su_team24_puddle.services.FilterService;
import com.cs5520.assignments.numad22su_team24_puddle.services.MapService;
import com.cs5520.assignments.numad22su_team24_puddle.services.MarkerService;
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
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;


public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {
    private static final String TAG = "MapActivity";
    private boolean mapInitiated = true;
    private boolean mapFirstZoom = true;
    LatLng latLng;
    private GoogleMap mMap;
    private HashMap<String, Puddle> allPuddlesData;
    private Boolean justOpened;
    private Handler handler = new Handler();
    FusedLocationProviderClient fusedLocationProviderClient;
    LocationRequest locationRequest;
    LocationCallback locationCallback;
    ViewPager viewPager;
    MapViewPagerAdapter mapViewPagerAdapter;
    List<PuddleMarker> puddleList;
    List<PuddleMarker> filteredPuddleList;
    HashSet<String> categories;
    ChipGroup chipGroup;
    SupportMapFragment mapFragment;
    MessageNotification notification;
    private ValueEventListener valueEventListener;
    private DatabaseReference userRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);

        allPuddlesData = new HashMap<>();
        categories = new HashSet<String>();


        viewPager = findViewById(R.id.viewPager);
        viewPager.setPageMargin(15);

        HorizontalScrollView map_filter_chips = findViewById(R.id.map_filter_chips);
        chipGroup = map_filter_chips.findViewById(R.id.map_chip_group);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        locationRequest = LocationRequest.create();
        locationRequest.setInterval(500);
        locationRequest.setFastestInterval(500);
        locationRequest.setPriority(Priority.PRIORITY_HIGH_ACCURACY);

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                if (mapInitiated == true) {
                    Location location = locationResult.getLastLocation();
                    latLng = new LatLng(location.getLatitude(), location.getLongitude());
                    fetchAllPuddles();
                    mapInitiated = false;
                }
            }
        };
        notification = new MessageNotification(this);

    }

    @Override
    protected void onResume() {
        super.onResume();
        Util.isPuddleListForeground = true;
        initializeNotificationListener();
    }

    @Override
    protected void onPause() {
        super.onPause();
        userRef.removeEventListener(valueEventListener);
    }

    private void initializeNotificationListener() {
        userRef = FirebaseDB.getDataReference("Users").child(FirebaseDB.currentUser.getId()).child("my_puddles");
        valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getChildrenCount() != 0) {
                    for (DataSnapshot snap :
                            snapshot.getChildren()) {
                        String puddleID = snap.getValue(String.class);
                        FirebaseDB.getDataReference("Messages").child(puddleID).orderByKey().limitToLast(1).
                                addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        for (DataSnapshot snap :
                                                snapshot.getChildren()) {
                                            String senderUsername = snap.child("username").getValue(String.class);
                                            String body = snap.child("body").getValue(String.class);
                                            Boolean isImage = snap.child("isMessage").getValue(Boolean.class);
                                            Boolean isNew = snap.child("isNew").getValue(Boolean.class);
                                                if (isNew != null && isNew && !senderUsername.equals(FirebaseDB.currentUser.getUsername())
                                                        && Util.isPuddleListForeground) {
                                                    snap.getRef().child("isNew").setValue(false);
                                                    FirebaseDB.getDataReference("Puddles").child(puddleID).child("name").addValueEventListener(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                            Log.d("here","mapactivity");
                                                            String name = snapshot.getValue(String.class);
                                                            if (isImage != null && isImage) {
                                                                notification.createNotification(senderUsername, senderUsername +
                                                                        " sent a new image!", puddleID, name);
                                                            } else {
                                                                notification.createNotification(senderUsername, body, puddleID, name);
                                                            }
                                                        }
                                                        @Override
                                                        public void onCancelled(@NonNull DatabaseError error) {

                                                        }
                                                    });
                                                }
                                                snap.getRef().child("isNew").setValue(false);
                                        }

                                    }

                                    @Override
                                    public void onCancelled (@NonNull DatabaseError error){

                                    }
                                });

                    }
                }
            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        userRef.addValueEventListener(valueEventListener);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mMap = googleMap;
        if (mapFirstZoom){
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17));
            mapFirstZoom = false;
        }
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mMap.setMyLocationEnabled(true);

        updatePuddles();
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(@NonNull Marker marker) {
                viewPager.setCurrentItem(Integer.parseInt(marker.getSnippet()), true);
                return true;
            }
        });

        chipGroup.setOnCheckedStateChangeListener(new ChipGroup.OnCheckedStateChangeListener() {
            @Override
            public void onCheckedChanged(@NonNull ChipGroup group, @NonNull List<Integer> checkedIds) {
                categories.clear();

                for (Integer id: checkedIds){
                    Chip chip = chipGroup.findViewById(id);
                    String cat = chip.getText().toString();
                    Log.d(TAG, "chip cat: " + cat);
                    categories.add(cat);
                }

                if (mMap != null) {
                    mMap.clear();
                }
                filteredPuddleList = puddleList.stream().filter(x->categories.contains(x.getCategory())).collect(Collectors.toList());
                updatePuddles();
            }
        });
    }

    private void updatePuddles(){
        int position = 0;
        for (PuddleMarker puddle: filteredPuddleList){
            Log.d(TAG, "filteredPuddleList: " + puddle.getName());
            Log.d(TAG, "filteredPuddleList: position " + position);

            puddle.setPosition(position++);
            MarkerService.addMarker(puddle, mMap);
        }
        mapViewPagerAdapter = new MapViewPagerAdapter(getSupportFragmentManager(), filteredPuddleList);
        viewPager.setAdapter(mapViewPagerAdapter);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(filteredPuddleList.get(position).getLatLng(), 17));
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }

        });

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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LocationPermissionActivity.REQUEST_CODE_FINE_LOCATION) {

            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                LocationPermissionActivity.locationPermissionGranted = true;
                Toast.makeText(this, "Location access successfully granted", Toast.LENGTH_SHORT).show();
                startLocationUpdates();
            }
            else {
                Toast.makeText(this, "Location access is not granted", Toast.LENGTH_SHORT).show();
            }

        }
    }
    private void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        Toast.makeText(MapActivity.this, "Locating to your current place.", Toast.LENGTH_SHORT).show();
        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());

    }

    private void stopLocationUpdates() {
        fusedLocationProviderClient.removeLocationUpdates(locationCallback);
    }


    @Override
    public void onBackPressed() {
        finish();
    }



//   public void testSelectLocation(View v){
//        startActivity(new Intent(this,SelectLocation.class));
//   }

    public void fetchAllPuddles() {DatabaseReference pudRef = FirebaseDB.getDataReference("Puddles");

        pudRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot snap : snapshot.getChildren()) {
                    Puddle puddle = snap.getValue(Puddle.class);
                    if (puddle != null && puddle.getIsPrivate().equals("false")) {
                        Log.d("MapActivity", "fetchAllPuddles: " + snap.getKey());
                        allPuddlesData.put(snap.getKey(), puddle);
                    }
                }
                Log.d("MapActivity", "fetchAllPuddles: "+allPuddlesData.size());
                List<Puddle> filteredPuddles = new ArrayList<>(allPuddlesData.values());

                double filteredDistance = 50.0;
                List<String> filteredCategories = Category.getCategoryNames();
                boolean filteredGlobal = true;
                int filteredMembership = 1000;
                filteredPuddles = FilterService.filteredPuddles(filteredPuddles, filteredDistance * 1609.34, latLng.latitude, latLng.longitude, filteredCategories, filteredGlobal, filteredMembership);

                puddleList = MapService.getPuddleList(filteredPuddles);
                filteredPuddleList = puddleList;
                for (PuddleMarker e: filteredPuddleList){
                    Log.d("MapActivity", "fetchAllPuddles: filteredpuddlelist categories "+e.getCategory());
                }

                fetchAllPuddlesCallBack();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void fetchAllPuddlesCallBack(){
        mapFragment.getMapAsync(this);
    }
}



