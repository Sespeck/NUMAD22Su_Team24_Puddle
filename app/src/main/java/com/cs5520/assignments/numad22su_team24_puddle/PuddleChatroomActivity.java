package com.cs5520.assignments.numad22su_team24_puddle;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
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

import com.cs5520.assignments.numad22su_team24_puddle.Utils.FirebaseDB;
import com.cs5520.assignments.numad22su_team24_puddle.chatroom_fragments.AboutFragment;
import com.cs5520.assignments.numad22su_team24_puddle.chatroom_fragments.ChatroomFragment;
import com.cs5520.assignments.numad22su_team24_puddle.chatroom_fragments.dialogs.AddNewEventDialog;
import com.cs5520.assignments.numad22su_team24_puddle.chatroom_fragments.EventsFragment;
import com.cs5520.assignments.numad22su_team24_puddle.chatroom_fragments.MembersFragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

public class PuddleChatroomActivity extends AppCompatActivity {
    private TabLayout tabLayout;
    private TabLayout.Tab currentTab;
    private FloatingActionButton fab;
    private Toolbar toolbar;
    private String puddleID ="-N8lixPAncVn1ilKLArZ";
    private boolean modalOpen = false;

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
        setContentView(R.layout.puddle_chatroom_activity);
        tabLayout = findViewById(R.id.tabLayout);
        toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_baseline_arrow_back_24);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
        initializeOnTabSelectedListener();
        this.fab = findViewById(R.id.fab);
        if (savedInstanceState != null){
            puddleID = savedInstanceState.getString("puddleID");
            currentTab = tabLayout.getTabAt(savedInstanceState.getInt("current_tab"));
            tabLayout.selectTab(currentTab);
            completeFragmentNavigation(currentTab);
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
        // Opens the full screen add new event modal
        fab.setOnClickListener(v -> {
            currentTab = tabLayout.getTabAt(3);
            Intent intent = new Intent(this, AddNewEventDialog.class);
            startActivityForResult.launch(intent);
        });
    }

    private void completeFragmentNavigation(TabLayout.Tab tab) {
        Bundle bundle = new Bundle();
        bundle.putString("puddleID",puddleID);
        if (tab.getPosition() == 0) {
            fab.setVisibility(View.INVISIBLE);
            ChatroomFragment chatroomFragment = new ChatroomFragment();
            chatroomFragment.setArguments(bundle);
            changeVisibleFragment(R.id.chat_tab, chatroomFragment, "chatroom");
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
                completeFragmentNavigation(tab);
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
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (currentTab == null){
            currentTab = tabLayout.getTabAt(0);
        }
        completeFragmentNavigation(currentTab);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}