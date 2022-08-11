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
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.cs5520.assignments.numad22su_team24_puddle.Adapter.MyPuddlesAdapter;
import com.cs5520.assignments.numad22su_team24_puddle.Adapter.PuddleListAdapter;
import com.cs5520.assignments.numad22su_team24_puddle.Model.Puddle;
import com.cs5520.assignments.numad22su_team24_puddle.Model.User;
import com.cs5520.assignments.numad22su_team24_puddle.Utils.FirebaseDB;
import com.cs5520.assignments.numad22su_team24_puddle.Utils.LocationPermissionActivity;
import com.cs5520.assignments.numad22su_team24_puddle.Utils.Util;
import com.cs5520.assignments.numad22su_team24_puddle.chatroom_fragments.EventsFragment;
import com.facebook.shimmer.ShimmerFrameLayout;
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
    SearchView puddleSearch;
    TextView noResultFound;

    // Firebase
    FirebaseUser current_user;
    DatabaseReference userRef;
    DatabaseReference imgRef;
    StorageReference storeRef;

    public static final int REQUEST_CODE_LOCATION_FOR_CREATE = 100;
    public static final int REQUEST_CODE_LOCATION_FOR_NEAR_ME = 101;

    private Handler handler = new Handler();
    private HashMap<String, String> userDetails;
    private Uri imageUri;
    private HashMap<String, Puddle> allPuddlesData;
    private HashMap<String, Puddle> myPuddlesData;
    private HashMap<String, Puddle> myPuddlesDataStored;

    private ArrayList<Puddle> allPuddleList;

    private HashMap<Category, List<Puddle>> categoryPuddlesData;
    private ShimmerFrameLayout shimmerFrameLayout;
    private EventsFragment.endShimmerEffectCallback callback = new EventsFragment.endShimmerEffectCallback() {
        @Override
        public void onLayoutInflated() {
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    shimmerFrameLayout.stopShimmer();
                    shimmerFrameLayout.setVisibility(View.GONE);
                    puddleListRecyclerView.setVisibility(View.VISIBLE);
                }
            }, 1200);
        }
    };

    public interface endShimmerEffectCallback {
        void onLayoutInflated();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_puddle_list);
        userDetails = new HashMap<>();
        allPuddlesData = new HashMap<>();
        categoryPuddlesData = new HashMap<>();
        myPuddlesData = new HashMap<>();
        myPuddlesDataStored = new HashMap<>();
        allPuddleList = new ArrayList<>();

        noResultFound = findViewById(R.id.no_result_found);
        noResultFound.setVisibility(View.GONE);

        shimmerFrameLayout = findViewById(R.id.events_shimmer_layout);
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
        puddleSearch = findViewById(R.id.search);
        puddleSearch.clearFocus();
        puddleSearch.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                if(s.equals("")) {
                    myPuddlesData = myPuddlesDataStored;
                    puddleListRecyclerView.setLayoutManager(new GridLayoutManager(PuddleListActivity.this, 2));
                    puddleListRecyclerView.setAdapter(new MyPuddlesAdapter(PuddleListActivity.this, myPuddlesData));

                    if(myPuddlesData.size() == 0){
                        noResultFound.setVisibility(TextView.VISIBLE);
                    } else {
                        noResultFound.setVisibility(TextView.GONE);
                    }
                } else {
                    filterPuddles(s);
                }
                return false;
            }
        });

        // Register for the filter results
        handleFilterResults();

        // Api Calls
        fetchCurrentUserData();
        uploadImageToFb();
        fetchAllPuddles();
        FirebaseDB.fetchAllUsers();

        // Initializing Widgets
        puddleListRecyclerView = findViewById(R.id.puddle_list_rv);
        if (!Util.puddleListPopulated) {
            shimmerFrameLayout.startShimmer();
            shimmerFrameLayout.setVisibility(View.VISIBLE);
            puddleListRecyclerView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    if (callback != null) {
                        callback.onLayoutInflated();
                    }
                    puddleListRecyclerView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                }
            });
            Util.puddleListPopulated = true;
        } else {
            shimmerFrameLayout.stopShimmer();
            shimmerFrameLayout.setVisibility(View.GONE);
            puddleListRecyclerView.setVisibility(View.VISIBLE);
        }


        updateRecyclerView(myPuddlesBtn);
        LocationPermissionActivity.checkLocationPermission(this);

        handleAppLink(getIntent());
    }

    public void filterPuddles(String txt){
        HashMap<String, Puddle> modifiedData = new HashMap<>();

        for(Map.Entry<String, Puddle> map: myPuddlesData.entrySet()){
            Puddle pud = map.getValue();
            if(pud.getName().contains(txt)){
                modifiedData.put(map.getKey(), map.getValue());
            }
        }

        puddleListRecyclerView.setLayoutManager(new GridLayoutManager(PuddleListActivity.this, 2));
        puddleListRecyclerView.setAdapter(new MyPuddlesAdapter(PuddleListActivity.this, modifiedData));

        if(modifiedData.size() == 0){
            noResultFound.setVisibility(TextView.VISIBLE);
        } else {
            noResultFound.setVisibility(TextView.GONE);
        }

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
//
            String puddleId = appLinkData.getLastPathSegment();
            DatabaseReference puddleRef = FirebaseDB.getDataReference("Puddles").child(puddleId);
            puddleRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    Puddle puddle = snapshot.getValue(Puddle.class);
                    showJoinPuddleDialogue(PuddleListActivity.this, puddle);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        }
    }


    public void fetchCurrentUserData() {
        FirebaseUser current_user = FirebaseDB.getCurrentUser();

        DatabaseReference userRef = FirebaseDB.getDataReference("Users").child(current_user.getUid());
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                for (DataSnapshot snap : snapshot) {
//                    userDetails.put(snap.getKey(), snap.getValue(String.class));
//                }
                FirebaseDB.currentUser = snapshot.getValue(User.class);
                Log.d("currentUser", FirebaseDB.currentUser.toString());
                if (!FirebaseDB.currentUser.getProfile_icon().equals("")) {
                    Glide.with(PuddleListActivity.this).load(FirebaseDB.currentUser.getProfile_icon()).into(profileIcon);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    private void handleFilterResults() {
        getSupportFragmentManager().setFragmentResultListener("filter_result", this,
                ((requestKey, result) -> {
                    // result extras can be null if user didn't select them
                    if (result.getString("start_date") != null && result.getString("end_date") != null) {
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
            modal.show(getSupportFragmentManager(), "filter");
        }
    }

    private void updateRecyclerView(View view) {
        // Initializing RecyclerView
        if (view.equals(nearMeBtn)) {
            puddleSearch.setVisibility(SearchView.GONE);
            if (LocationPermissionActivity.checkLocationPermission(this)) {
                categorizePuddles();
                puddleListRecyclerView.setLayoutManager(new LinearLayoutManager(this));
                puddleListRecyclerView.setAdapter(new PuddleListAdapter(this, categoryPuddlesData));
                setSelectedButton(nearMeBtn);
                setUnselectedButton(myPuddlesBtn);
            } else {
                LocationPermissionActivity.requestPermission(this, REQUEST_CODE_LOCATION_FOR_NEAR_ME);
            }
        } else {
            puddleSearch.setVisibility(SearchView.VISIBLE);
            puddleListRecyclerView.setLayoutManager(new GridLayoutManager(PuddleListActivity.this, 2));
            puddleListRecyclerView.setAdapter(new MyPuddlesAdapter(PuddleListActivity.this, myPuddlesData));
            setSelectedButton(myPuddlesBtn);
            setUnselectedButton(nearMeBtn);

            new Thread(new Runnable() {
                @Override
                public void run() {
                    fetchMyPuddles();
                }
            });
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

    public void showJoinPuddleDialogue(Context context, Puddle puddle) {

        if(FirebaseDB.currentUser == null){
            FirebaseUser current_user = FirebaseDB.getCurrentUser();
            DatabaseReference userRef = FirebaseDB.getDataReference("Users").child(current_user.getUid());
            userRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    FirebaseDB.currentUser = snapshot.getValue(User.class);
                    if(FirebaseDB.currentUser != null){
                        showJoinPuddle(context, puddle);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        } else {
            showJoinPuddle(context, puddle);
        }

    }

    public void showJoinPuddle(Context context, Puddle puddle){


        if (FirebaseDB.currentUser.getMy_puddles() != null  && FirebaseDB.currentUser.getMy_puddles().containsValue(puddle.getId())) {
            Intent intent = new Intent(PuddleListActivity.this, PuddleChatroomActivity.class);
            intent.putExtra("puddleID", puddle.getId());
            context.startActivity(intent);
        } else {
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
                intent.putExtra("puddleID", puddle.getId());
                addPuddlesToList(puddle.getId(), puddle);
                context.startActivity(intent);
            });
            dialog.show();
        }
    }

    public void addPuddlesToList(String pud_id, Puddle pud) {
        boolean alreadyJoined = this.myPuddlesData.containsKey(pud_id);

        new Thread(new Runnable() {
            @Override
            public void run() {
                // 1. Update the id in my_puddles for user
                String uid = FirebaseDB.getCurrentUser().getUid();
                DatabaseReference ref = FirebaseDB.getDataReference("Users").child(uid).child("my_puddles");
                ref.push().setValue(pud_id);

                // 2. Add Puddle count
                DatabaseReference pudRef = FirebaseDB.getDataReference("Puddles").child(pud_id).child("count");
                pudRef.setValue(alreadyJoined ? pud.getCount() : pud.getCount() + 1);

                // 3. Update the members child
                DatabaseReference memRef = FirebaseDB.getDataReference("Members").child(pud_id);
                memRef.push().setValue(uid);
            }
        }).start();
    }

    // To capture all the puddles
    public void fetchAllPuddles() {
        DatabaseReference pudRef = FirebaseDB.getDataReference("Puddles");
        pudRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot snap : snapshot.getChildren()) {
                    Puddle puddle = snap.getValue(Puddle.class);
                    if (puddle != null) {
                        allPuddlesData.put(snap.getKey(), puddle);
                        allPuddleList.add(puddle);
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
    public void fetchMyPuddles() {
        puddleListRecyclerView.setLayoutManager(new GridLayoutManager(PuddleListActivity.this, 2));
        puddleListRecyclerView.setAdapter(new MyPuddlesAdapter(PuddleListActivity.this, myPuddlesData));
        setSelectedButton(myPuddlesBtn);
        setUnselectedButton(nearMeBtn);

        DatabaseReference myPuds = FirebaseDB.getDataReference("Users").child(FirebaseDB.getCurrentUser().getUid()).child("my_puddles");
        myPuds.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Log.d("snap data", snapshot.toString());
                for (DataSnapshot snap : snapshot.getChildren()) {
                    String key = snap.getValue(String.class);
                    if (key != null) {
                        myPuddlesData.put(key, allPuddlesData.get(key));
                    }
                    myPuddlesDataStored = myPuddlesData;
                    puddleListRecyclerView.setLayoutManager(new GridLayoutManager(PuddleListActivity.this, 2));
                    puddleListRecyclerView.setAdapter(new MyPuddlesAdapter(PuddleListActivity.this, myPuddlesData));
                    setSelectedButton(myPuddlesBtn);
                    setUnselectedButton(nearMeBtn);
                }

                if(myPuddlesData.size() == 0){
                    noResultFound.setVisibility(TextView.VISIBLE);
                } else {
                    noResultFound.setVisibility(TextView.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void initializePuddles() {
        for (Category category : Category.values()) {
            categoryPuddlesData.put(category, new ArrayList<>());
        }
    }

    // To categorize puddles for near me screen
    public void categorizePuddles() {
        initializePuddles();
        for (String id : allPuddlesData.keySet()) {
            Puddle puddle = allPuddlesData.get(id);
            Log.d("Puddle", puddle.getCategory());
            categoryPuddlesData.get(Category.valueOf(puddle.getCategory().toUpperCase())).add(puddle);
        }

    }
}