package com.cs5520.assignments.numad22su_team24_puddle;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.cs5520.assignments.numad22su_team24_puddle.chatroom_fragments.AboutFragment;
import com.cs5520.assignments.numad22su_team24_puddle.chatroom_fragments.ChatroomFragment;
import com.cs5520.assignments.numad22su_team24_puddle.chatroom_fragments.dialogs.AddNewEventDialog;
import com.cs5520.assignments.numad22su_team24_puddle.chatroom_fragments.EventsFragment;
import com.cs5520.assignments.numad22su_team24_puddle.chatroom_fragments.MembersFragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;

public class PuddleChatroomActivity extends AppCompatActivity {
    private TabLayout tabLayout;
    private TabLayout.Tab currentTab;
    private FloatingActionButton fab;
    private Toolbar toolbar;

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
        // Opens the full screen add new event modal
        fab.setOnClickListener(v -> {
            currentTab = tabLayout.getTabAt(3);
            FragmentManager fragmentManager = getSupportFragmentManager();
            AddNewEventDialog fragment = new AddNewEventDialog(this);
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
            transaction.add(android.R.id.content, fragment).addToBackStack(null).commit();
        });
    }

    private void completeFragmentNavigation(TabLayout.Tab tab) {
        if (tab.getPosition() == 0) {
            fab.setVisibility(View.INVISIBLE);
            changeVisibleFragment(R.id.chat_tab, new ChatroomFragment(), "chatroom");
        } else if (tab.getPosition() == 1) {
            fab.setVisibility(View.INVISIBLE);
            changeVisibleFragment(R.id.about_tab, new AboutFragment(), "about");
        } else if (tab.getPosition() == 2) {
            fab.setVisibility(View.INVISIBLE);
            changeVisibleFragment(R.id.members_tab, new MembersFragment(), "members");
        } else if (tab.getPosition() == 3) {
            fab.setVisibility(View.VISIBLE);
            changeVisibleFragment(R.id.events_tab, new EventsFragment(), "events");
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
    protected void onStart() {
        super.onStart();
        if (currentTab == null){
            currentTab = tabLayout.getTabAt(0);
        }
        completeFragmentNavigation(currentTab);
    }
}
