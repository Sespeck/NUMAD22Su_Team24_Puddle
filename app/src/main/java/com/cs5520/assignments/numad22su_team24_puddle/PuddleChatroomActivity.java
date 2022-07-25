package com.cs5520.assignments.numad22su_team24_puddle;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.cs5520.assignments.numad22su_team24_puddle.chatroom_fragments.AboutFragment;
import com.cs5520.assignments.numad22su_team24_puddle.chatroom_fragments.ChatroomFragment;
import com.cs5520.assignments.numad22su_team24_puddle.chatroom_fragments.EventsFragment;
import com.cs5520.assignments.numad22su_team24_puddle.chatroom_fragments.MembersFragment;
import com.google.android.material.tabs.TabLayout;

public class PuddleChatroomActivity extends AppCompatActivity {
    private TabLayout tabLayout;
    private TabLayout.Tab currentTab;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.puddle_chatroom_activity);
        tabLayout = findViewById(R.id.tabLayout);
        initializeOnTabSelectedListener();

        // Need to dynamically pull the puddle BGImage from DB
        // Need to inflate the recycler view with msgs
    }


    private void initializeOnTabSelectedListener(){
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                Log.d("here",String.valueOf(tab.getPosition()));
                currentTab = tab;
                if (tab.getPosition() == 0){
                    changeVisibleFragment(R.id.chat_tab,new ChatroomFragment(),"chatroom");
                }
                else if (tab.getPosition() == 1){
                    changeVisibleFragment(R.id.about_tab,new AboutFragment(),"about");
                }
                else if (tab.getPosition() == 2){
                    changeVisibleFragment(R.id.members_tab,new MembersFragment(), "members");
                }
                else if (tab.getPosition() == 3){
                    changeVisibleFragment(R.id.events_tab,new EventsFragment(),"events");
                }
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
                fragment, fragmentName).addToBackStack(fragmentName).commit();

    }

    @Override
    protected void onStart() {
        super.onStart();
        if (currentTab == null) {
            currentTab = tabLayout.getTabAt(0);
        }
        if (currentTab != null) {
            currentTab.select();
        }
    }
}
