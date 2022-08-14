package com.cs5520.assignments.numad22su_team24_puddle;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.cs5520.assignments.numad22su_team24_puddle.Model.User;
import com.cs5520.assignments.numad22su_team24_puddle.Utils.FirebaseDB;
import com.cs5520.assignments.numad22su_team24_puddle.Utils.Util;
import com.cs5520.assignments.numad22su_team24_puddle.chatroom_fragments.AboutFragment;
import com.cs5520.assignments.numad22su_team24_puddle.chatroom_fragments.ChatroomFragment;
import com.cs5520.assignments.numad22su_team24_puddle.chatroom_fragments.MessageNotification;
import com.cs5520.assignments.numad22su_team24_puddle.chatroom_fragments.dialogs.AddNewEventDialog;
import com.cs5520.assignments.numad22su_team24_puddle.chatroom_fragments.EventsFragment;
import com.cs5520.assignments.numad22su_team24_puddle.chatroom_fragments.MembersFragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class PuddleChatroomActivity extends AppCompatActivity {
    private TabLayout tabLayout;
    private TabLayout.Tab currentTab;
    private FloatingActionButton fab;
    private Toolbar toolbar;
    private String puddleID;
    private Boolean justOpened;
    private ChatroomFragment fragment;
    private MessageNotification notification;
    private Handler handler = new Handler();
    private ValueEventListener valueEventListener;
    private DatabaseReference userRef;
    private ArrayList<ValueEventListener> valueEventListeners = new ArrayList<>();
    private ArrayList<DatabaseReference> references = new ArrayList<>();

    ActivityResultLauncher<Intent> startActivityForResult = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK){
                    Intent intent = result.getData();
                    if (intent.getExtras() != null) {
                        getSupportFragmentManager().setFragmentResult("event_creation_result", intent.getExtras());
                    }
                }
            });

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        puddleID = getIntent().getStringExtra("puddleID");
        setContentView(R.layout.puddle_chatroom_activity);
        tabLayout = findViewById(R.id.tabLayout);
        toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_baseline_arrow_back_24);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
        notification = new MessageNotification(this);
        Util.isForeground = true;
        justOpened = false;
        Util.foregroundedPuddle = puddleID;
        this.fab = findViewById(R.id.fab);
        if (savedInstanceState != null){
            puddleID = savedInstanceState.getString("puddleID");
            currentTab = tabLayout.getTabAt(savedInstanceState.getInt("current_tab"));
            Log.d("current_tab",String.valueOf(currentTab.getPosition()));
            tabLayout.selectTab(currentTab);
            completeFragmentNavigation(currentTab,0);
        }
        FirebaseDB.getDataReference("Puddles").child(puddleID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String name = snapshot.child("name").getValue(String.class);
                if (name != null) toolbar.setTitle(name);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        initializeOnTabSelectedListener();
        // Opens the full screen add new event modal
        fab.setOnClickListener(v -> {
            currentTab = tabLayout.getTabAt(3);
            Intent intent = new Intent(this, AddNewEventDialog.class);
            startActivityForResult.launch(intent);
        });
    }


    private void initializeNotificationListener() {
        valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getChildrenCount() != 0) {
                    for (DataSnapshot snap :
                            snapshot.getChildren()) {
                        String puddleID = snap.getValue(String.class);
                        ValueEventListener listener = new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        for (DataSnapshot snap :
                                                snapshot.getChildren()) {
                                            String senderUsername = snap.child("username").getValue(String.class);
                                            String body = snap.child("body").getValue(String.class);
                                            Boolean isImage = snap.child("isMessage").getValue(Boolean.class);
                                            Boolean isNew = snap.child("isNew").getValue(Boolean.class);
                                            if (isNew != null && ((!senderUsername.equals(FirebaseDB.getLocalUser().getUsername()) && isNew && !Util.isForeground)
                                                    || ((!senderUsername.equals(FirebaseDB.getLocalUser().getUsername())) && isNew && !justOpened &&
                                                    !Util.foregroundedPuddle.equals(snapshot.getRef().getKey())))) {
                                                Log.d("here","puddlechatroom");
                                                snap.getRef().child("isNew").setValue(false);
                                                FirebaseDB.getDataReference("Puddles").child(puddleID).child("name").
                                                        addValueEventListener(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
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
                                        };

                        FirebaseDB.getDataReference("Messages").child(puddleID).orderByKey().limitToLast(1).addValueEventListener(listener);
                        valueEventListeners.add(listener);
                        references.add( FirebaseDB.getDataReference("Messages").child(puddleID));


                    }
                }
            }
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        userRef = FirebaseDB.getDataReference("Users").child(FirebaseDB.getLocalUser().getId()).child("my_puddles");

        userRef.addValueEventListener(valueEventListener);

        if (currentTab.getPosition() != 0 && Util.isForeground){
            handler.postDelayed(() -> Util.isForeground = false,1000);
        }
    }


    private void completeFragmentNavigation(TabLayout.Tab tab, int flag) {
        Bundle bundle = new Bundle();
        bundle.putString("puddleID",puddleID);
        if (flag == 1) bundle.putString("new_chatroom","dont_animate_shimmer");
        if (tab.getPosition() == 0) {
            fab.setVisibility(View.INVISIBLE);
            fragment  = new ChatroomFragment();
            fragment.setArguments(bundle);
            changeVisibleFragment(R.id.chat_tab, fragment, "chatroom");
        } else if (tab.getPosition() == 1) {
            fab.setVisibility(View.INVISIBLE);
            AboutFragment aboutFragment = new AboutFragment();
            aboutFragment.setArguments(bundle);
            changeVisibleFragment(R.id.about_tab,  aboutFragment,"about");
        } else if (tab.getPosition() == 2) {
            fab.setVisibility(View.INVISIBLE);
            MembersFragment membersFragment = new MembersFragment();
            membersFragment.setArguments(bundle);
            changeVisibleFragment(R.id.members_tab, membersFragment, "members");
        } else if (tab.getPosition() == 3) {
            fab.setVisibility(View.VISIBLE);
            EventsFragment eventsFragment = new EventsFragment();
            eventsFragment.setArguments(bundle);
            changeVisibleFragment(R.id.events_tab,  eventsFragment, "events");
        }
    }


    private void initializeOnTabSelectedListener(){
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                currentTab = tab;
                completeFragmentNavigation(tab,0);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    private void changeVisibleFragment(int id, Fragment fragment, String fragmentName){
        getSupportFragmentManager().beginTransaction().replace(R.id.chatroom_fragment_container,
                fragment, fragmentName).commit();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putString("puddleID",puddleID);
        outState.putInt("current_tab",currentTab.getPosition());
//        Util.isForeground = true;
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (currentTab == null){
            currentTab = tabLayout.getTabAt(0);
        }
        if (getIntent().getStringExtra("new_chatroom") != null) {
            completeFragmentNavigation(currentTab, 1);
        } else{
            completeFragmentNavigation(currentTab, 0);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Util.isForeground = true;
        initializeNotificationListener();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        for (int i=0; i<references.size(); i++){
            references.get(i).removeEventListener(valueEventListeners.get(i));
        }
        userRef.removeEventListener(valueEventListener);
    }

    public void navigateHome(MenuItem item) {
        Intent intent = new Intent(this, PuddleListActivity.class);
        for (int i=0; i<references.size(); i++){
           references.get(i).removeEventListener(valueEventListeners.get(i));
        }
        userRef.removeEventListener(valueEventListener);
        startActivity(intent);
    }


    public void navigateToMap(MenuItem item) {
        Intent intent = new Intent(this, MapActivity.class);
        for (int i=0; i<references.size(); i++){
            references.get(i).removeEventListener(valueEventListeners.get(i));
        }
        userRef.removeEventListener(valueEventListener);
        startActivity(intent);
    }


    public void navigateToSettings(MenuItem item) {
        Intent intent = new Intent(this, SettingsActivity.class);
        for (int i=0; i<references.size(); i++){
            references.get(i).removeEventListener(valueEventListeners.get(i));
        }
        intent.addFlags((Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
        intent.putExtra("PuddleId", puddleID);
        startActivity(intent);
    }
}