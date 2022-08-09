package com.cs5520.assignments.numad22su_team24_puddle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.cs5520.assignments.numad22su_team24_puddle.Adapter.MyPuddlesAdapter;
import com.cs5520.assignments.numad22su_team24_puddle.Adapter.PuddleListAdapter;
import com.cs5520.assignments.numad22su_team24_puddle.Model.Puddle;
import com.cs5520.assignments.numad22su_team24_puddle.Model.User;
import com.cs5520.assignments.numad22su_team24_puddle.Utils.FirebaseDB;
import com.cs5520.assignments.numad22su_team24_puddle.Utils.LocationPermissionActivity;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PuddleListActivity extends AppCompatActivity implements View.OnClickListener {

    // Widgets
    RecyclerView puddleListRecyclerView;
    PuddleListAdapter puddleListAdapter;
    ShapeableImageView profileIcon;
    ImageView createIcon;
    ImageView navigationIcon;
    ImageView filterIcon;
    Button nearMeBtn, myPuddlesBtn;

    // Firebase
    FirebaseUser current_user;
    DatabaseReference userRef;
    DatabaseReference imgRef;
    StorageReference storeRef;

    public static final int REQUEST_CODE_LOCATION_FOR_CREATE = 100;
    public static final int REQUEST_CODE_LOCATION_FOR_NEAR_ME = 101;

    private HashMap<String, String> userDetails;
    private Uri imageUri;
    private HashMap<String, Puddle> allPuddlesData;
    private HashMap<String, Puddle> myPuddlesData;
    private HashMap<String, List<Puddle>> categoryPuddlesData;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_puddle_list);
        userDetails = new HashMap<>();
        allPuddlesData = new HashMap<>();
        myPuddlesData = new HashMap<>();

        profileIcon = findViewById(R.id.puddle_list_header_profile_icon);
        profileIcon.setOnClickListener(this);
        nearMeBtn = findViewById(R.id.header_near_me_btn);
        nearMeBtn.setOnClickListener(this);
        myPuddlesBtn = findViewById(R.id.header_my_puddles_btn);
        myPuddlesBtn.setOnClickListener(this);
        createIcon = findViewById(R.id.header_create_puddle_icon);
        createIcon.setOnClickListener(this);
        filterIcon = findViewById(R.id.header_filter_icon);
        filterIcon.setOnClickListener(this);
        navigationIcon = findViewById(R.id.header_navigation_icon);
        navigationIcon.setOnClickListener(this);
        // Register for the filter results
        handleFilterResults();
        // Api Calls
        FirebaseDB.fetchCurrentUserData();
        uploadImageToFb();
//        uploadImageToFb();
        fetchAllPuddles();

        // Initializing Widgets
        puddleListRecyclerView = findViewById(R.id.puddle_list_rv);


        updateRecyclerView(myPuddlesBtn);
        LocationPermissionActivity.checkLocationPermission(this);

        handleAppLink(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        handleAppLink(intent);
    }

    private void handleAppLink(Intent intent) {
        String appLinkAction = intent.getAction();
        Uri appLinkData = intent.getData();
        if (Intent.ACTION_VIEW.equals(appLinkAction) && appLinkData != null) {
//            updateRecyclerView(nearMeBtn);
            String puddleId = appLinkData.getLastPathSegment();
            Puddle puddle = new Puddle("Puddle 1", "https://firebasestorage.googleapis.com/v0/b/android-chat-85561.appspot.com/o/1659765502514.jpg?alt=media&token=a1b9ff75-682e-499f-99a6-df4148b05358", "Bio 1", Category.EDUCATION.toString(), "false", "10",5, new HashMap<>());
            showJoinPuddleDialogue(this, puddle);
        }
    }

    private List<List<Puddle>> getPuddleList() {
        List<List<Puddle>> puddlesList = new ArrayList<>();
        Bitmap bitmap = BitmapFactory.decodeResource(this.getResources(), R.drawable.puddle);
        for (Category category : Category.values()) {
            List<Puddle> puddleArray = new ArrayList<>();
            puddleArray.add(new Puddle("Puddle 1", "https://firebasestorage.googleapis.com/v0/b/android-chat-85561.appspot.com/o/1659765502514.jpg?alt=media&token=a1b9ff75-682e-499f-99a6-df4148b05358", "Bio 1", Category.EDUCATION.toString(), "false", "10",5, new HashMap<>()));
            puddleArray.add(new Puddle("Puddle 1", "https://firebasestorage.googleapis.com/v0/b/android-chat-85561.appspot.com/o/1659765502514.jpg?alt=media&token=a1b9ff75-682e-499f-99a6-df4148b05358", "Bio 1", Category.EDUCATION.toString(), "false", "10",5, new HashMap<>()));
            puddleArray.add(new Puddle("Puddle 1", "https://firebasestorage.googleapis.com/v0/b/android-chat-85561.appspot.com/o/1659765502514.jpg?alt=media&token=a1b9ff75-682e-499f-99a6-df4148b05358", "Bio 1", Category.EDUCATION.toString(), "false", "10",5, new HashMap<>()));
            puddleArray.add(new Puddle("Puddle 1", "https://firebasestorage.googleapis.com/v0/b/android-chat-85561.appspot.com/o/1659765502514.jpg?alt=media&token=a1b9ff75-682e-499f-99a6-df4148b05358", "Bio 1", Category.EDUCATION.toString(), "false", "10",5, new HashMap<>()));
            puddleArray.add(new Puddle("Puddle 1", "https://firebasestorage.googleapis.com/v0/b/android-chat-85561.appspot.com/o/1659765502514.jpg?alt=media&token=a1b9ff75-682e-499f-99a6-df4148b05358", "Bio 1", Category.EDUCATION.toString(), "false", "10",5, new HashMap<>()));
            puddleArray.add(new Puddle("Puddle 1", "https://firebasestorage.googleapis.com/v0/b/android-chat-85561.appspot.com/o/1659765502514.jpg?alt=media&token=a1b9ff75-682e-499f-99a6-df4148b05358", "Bio 1", Category.EDUCATION.toString(), "false", "10",5, new HashMap<>()));
            puddleArray.add(new Puddle("Puddle 1", "https://firebasestorage.googleapis.com/v0/b/android-chat-85561.appspot.com/o/1659765502514.jpg?alt=media&token=a1b9ff75-682e-499f-99a6-df4148b05358", "Bio 1", Category.EDUCATION.toString(), "false", "10",5, new HashMap<>()));
            puddleArray.add(new Puddle("Puddle 1", "https://firebasestorage.googleapis.com/v0/b/android-chat-85561.appspot.com/o/1659765502514.jpg?alt=media&token=a1b9ff75-682e-499f-99a6-df4148b05358", "Bio 1", Category.EDUCATION.toString(), "false", "10",5, new HashMap<>()));
            puddleArray.add(new Puddle("Puddle 1", "https://firebasestorage.googleapis.com/v0/b/android-chat-85561.appspot.com/o/1659765502514.jpg?alt=media&token=a1b9ff75-682e-499f-99a6-df4148b05358", "Bio 1", Category.EDUCATION.toString(), "false", "10",5, new HashMap<>()));
            puddleArray.add(new Puddle("Puddle 1", "https://firebasestorage.googleapis.com/v0/b/android-chat-85561.appspot.com/o/1659765502514.jpg?alt=media&token=a1b9ff75-682e-499f-99a6-df4148b05358", "Bio 1", Category.EDUCATION.toString(), "false", "10",5, new HashMap<>()));
            puddlesList.add(puddleArray);
        }
        return puddlesList;
    }

    public void fetchCurrentUserData() {
        current_user = FirebaseDB.getCurrentUser();

        userRef = FirebaseDB.getDataReference(getString(R.string.users)).child(current_user.getUid());
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                for (DataSnapshot snap : snapshot) {
//                    userDetails.put(snap.getKey(), snap.getValue(String.class));
//                }
                User currentUser = snapshot.getValue(User.class);
                Log.d("currentUser", currentUser.toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    private void handleFilterResults(){
        getSupportFragmentManager().setFragmentResultListener("filter_result",this,
                ((requestKey, result) -> {
                    // result extras can be null if user didn't select them
                    if (result.getString("start_date") != null && result.getString("end_date") != null){
                        String startDate = result.getString("start_date");
                        String endDate = result.getString("end_date");
                    }
                    Category category = null;
                    if (result.getString("category") != null) {
                        switch (result.getString("category")) {
                            case "Music":
                                category = Category.MUSIC;
                                break;
                            case "Finance":
                                category = Category.FINANCE;
                                break;
                            case "Travel":
                                category = Category.TRAVEL;
                            case "Education":
                                category = Category.EDUCATION;
                        }
                    }
                    double distance = result.getDouble("distance");
        }));
    }

    @Override
    public void onClick(View view) {
        if (view.equals(profileIcon)) {
            startActivity(new Intent(this, ProfileActivity.class));
        } else if (view.equals(nearMeBtn) || view.equals(myPuddlesBtn)) {
            updateRecyclerView(view);
        } else if (view.equals(createIcon)) {
            if (LocationPermissionActivity.checkLocationPermission(this)) {
                startActivity(new Intent(PuddleListActivity.this, CreatePuddle.class));
            } else {
                LocationPermissionActivity.requestPermission(this, REQUEST_CODE_LOCATION_FOR_CREATE);
            }

        } else if (view.equals(navigationIcon)) {
            if (LocationPermissionActivity.checkMapServices(this)) {
                if (LocationPermissionActivity.locationPermissionGranted) {
                    startActivity(new Intent(this, MapActivity.class));
                } else {
                    LocationPermissionActivity.requestPermission(this, LocationPermissionActivity.REQUEST_CODE_FINE_LOCATION);
                }
            }
        } else if (view.equals(filterIcon)) {
            BottomFilterModal modal = new BottomFilterModal();
            modal.show(getSupportFragmentManager(),"filter");
        }
    }

    private void updateRecyclerView(View view) {
        // Initializing RecyclerView
        if (view.equals(nearMeBtn)) {
            if (LocationPermissionActivity.checkLocationPermission(this)) {
                puddleListRecyclerView.setLayoutManager(new LinearLayoutManager(this));
                puddleListRecyclerView.setAdapter(new PuddleListAdapter(this, getPuddleList()));
                setSelectedButton(nearMeBtn);
                setUnselectedButton(myPuddlesBtn);
            } else {
                LocationPermissionActivity.requestPermission(this, REQUEST_CODE_LOCATION_FOR_NEAR_ME);
            }
        } else {
            if (myPuddlesData.isEmpty()) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        fetchMyPuddles();
                    }
                });
                setSelectedButton(myPuddlesBtn);
                setUnselectedButton(nearMeBtn);
            } else {
                puddleListRecyclerView.setLayoutManager(new GridLayoutManager(PuddleListActivity.this, 2));
                puddleListRecyclerView.setAdapter(new MyPuddlesAdapter(PuddleListActivity.this, myPuddlesData));
                setSelectedButton(myPuddlesBtn);
                setUnselectedButton(nearMeBtn);
            }

        }

    }

    private void setSelectedButton(Button btn) {
        btn.setBackgroundColor(ContextCompat.getColor(this, R.color.purple_700));
        btn.setTextColor(ContextCompat.getColor(this, R.color.white));
    }

    private void setUnselectedButton(Button btn) {
        btn.setBackgroundColor(ContextCompat.getColor(this, com.google.android.libraries.places.R.color.quantum_grey200));
        btn.setTextColor(ContextCompat.getColor(this, R.color.black));
    }

    // Testing Firestore + Firebase DB
    public void uploadImageToFb() {
        imgRef = FirebaseDB.getDataReference("images");
        imgRef.setValue("url");

        storeRef = FirebaseDB.storageRef;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case LocationPermissionActivity.PERMISSIONS_REQUEST_ENABLE_GPS: {
                if (LocationPermissionActivity.locationPermissionGranted) {
                    startActivity(new Intent(this, MapActivity.class));
                } else {
                    LocationPermissionActivity.requestPermission(this, LocationPermissionActivity.REQUEST_CODE_FINE_LOCATION);
                }
            }
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case LocationPermissionActivity.REQUEST_CODE_FINE_LOCATION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    LocationPermissionActivity.locationPermissionGranted = true;
                    Toast.makeText(this, "Location access successfully granted", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(this, MapActivity.class));
                } else {
                    Toast.makeText(this, "Location access is not granted", Toast.LENGTH_SHORT).show();
                }
                break;
            case REQUEST_CODE_LOCATION_FOR_CREATE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    LocationPermissionActivity.locationPermissionGranted = true;
                    Toast.makeText(this, "Location access successfully granted", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(PuddleListActivity.this, CreatePuddle.class));
                } else {
                    Toast.makeText(this, "Location access is not granted", Toast.LENGTH_SHORT).show();
                }
                break;
            case REQUEST_CODE_LOCATION_FOR_NEAR_ME:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    LocationPermissionActivity.locationPermissionGranted = true;
                    Toast.makeText(this, "Location access successfully granted", Toast.LENGTH_SHORT).show();
                    updateRecyclerView(nearMeBtn);
                } else {
                    Toast.makeText(this, "Location access is not granted", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    public static void showJoinPuddleDialogue(Context context, Puddle puddle) {
        View layoutView = View.inflate(context, R.layout.puddle_modal, null);
        AlertDialog dialog = new MaterialAlertDialogBuilder(context).setTitle(puddle.getName()).setView(layoutView).create();
        TextView tv = layoutView.findViewById(R.id.puddle_modal_name_tv);
        tv.setText(puddle.getBio());
        ShapeableImageView image = layoutView.findViewById(R.id.puddle_modal_item_image);
        Glide.with(context).load(puddle.getBannerUrl()).into(image);
        MaterialButton button = layoutView.findViewById(R.id.puddle_modal_join_btn);
        button.setOnClickListener(v -> {
            dialog.dismiss();
            Intent intent = new Intent(context, PuddleChatroomActivity.class);
            context.startActivity(intent);
        });
        dialog.show();
    }

    // To capture all the puddles
    public void fetchAllPuddles(){
        DatabaseReference pudRef = FirebaseDB.getDataReference("Puddles");
        pudRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot snap: snapshot.getChildren()){
                    Puddle puddle = snap.getValue(Puddle.class);
                    if(puddle != null){
                        allPuddlesData.put(snap.getKey(), puddle);
                    }
                }
                fetchMyPuddles();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    // To store the current user puddles
    public void fetchMyPuddles(){
        puddleListRecyclerView.setLayoutManager(new GridLayoutManager(PuddleListActivity.this, 2));
        puddleListRecyclerView.setAdapter(new MyPuddlesAdapter(PuddleListActivity.this, myPuddlesData));
        setSelectedButton(myPuddlesBtn);
        setUnselectedButton(nearMeBtn);

        DatabaseReference myPuds = FirebaseDB.getDataReference("Users").child(FirebaseDB.getCurrentUser().getUid()).child("my_puddles");
        myPuds.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Log.d("snap data", snapshot.toString());
                for(DataSnapshot snap: snapshot.getChildren()){
                    String key = snap.getValue(String.class);
                    if(key != null){
                        myPuddlesData.put(key, allPuddlesData.get(key));
                    }
                    puddleListRecyclerView.setLayoutManager(new GridLayoutManager(PuddleListActivity.this, 2));
                    puddleListRecyclerView.setAdapter(new MyPuddlesAdapter(PuddleListActivity.this, myPuddlesData));
                    setSelectedButton(myPuddlesBtn);
                    setUnselectedButton(nearMeBtn);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void initializePuddles(){
        categoryPuddlesData.put(Category.MUSIC.toString(), new ArrayList<>());
        categoryPuddlesData.put(Category.EDUCATION.toString(), new ArrayList<>());
        categoryPuddlesData.put(Category.TRAVEL.toString(), new ArrayList<>());
        categoryPuddlesData.put(Category.FINANCE.toString(), new ArrayList<>());
    }

    // To categorize puddles for near me screen
    public void categorizePuddles(){
        initializePuddles();

        for(Map.Entry<String, Puddle> puddle: allPuddlesData.entrySet()){
            categoryPuddlesData.get(puddle.getKey()).add(puddle.getValue());
        }

    }
}