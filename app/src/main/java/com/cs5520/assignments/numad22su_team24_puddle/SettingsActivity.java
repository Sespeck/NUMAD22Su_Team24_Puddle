package com.cs5520.assignments.numad22su_team24_puddle;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.cs5520.assignments.numad22su_team24_puddle.Model.Puddle;
import com.cs5520.assignments.numad22su_team24_puddle.Utils.FirebaseDB;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class SettingsActivity extends AppCompatActivity implements View.OnClickListener {

    TextView sharePuddle;
    String puddleId;
    LinearLayout sharePuddleLayout, leavePuddleLayout, reportPuddleLayout;
    String keyToDelete = "";
    String memToDelete = "";
    HashMap<String, Object> puddleMap = new HashMap<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity_layout);
        puddleId = getIntent().getStringExtra("PuddleId");

        // Initialize Widgets
        sharePuddleLayout = findViewById(R.id.share_puddle_layout);
        leavePuddleLayout = findViewById(R.id.leave_puddle_layout);
        reportPuddleLayout = findViewById(R.id.report_puddle_layout);

        // OnClick Listeners
        sharePuddleLayout.setOnClickListener(this);
        leavePuddleLayout.setOnClickListener(this);
        reportPuddleLayout.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.share_puddle_layout) {
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("text/plain");
            String appLink = "http://puddle-team24-app.com/join/" + puddleId;
            intent.putExtra(Intent.EXTRA_SUBJECT, appLink);
            intent.putExtra(Intent.EXTRA_TEXT, appLink);
            startActivity(Intent.createChooser(intent, "Share using"));
        } else if(view.getId() == R.id.leave_puddle_layout){
            DatabaseReference ref = FirebaseDB.getDataReference("Users").child(FirebaseDB.currentUser.getId()).child("my_puddles");
            DatabaseReference pudRef = FirebaseDB.getDataReference("Puddles");
            DatabaseReference memRef = FirebaseDB.getDataReference("Members").child(puddleId);

            new Thread(new Runnable() {
                @Override
                public void run() {
                    ref.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for(DataSnapshot snap: snapshot.getChildren()){
                                keyToDelete = snap.getKey();
                                String val = snap.getValue(String.class);

                                if(val.equals(puddleId)){
                                    Log.i("puddle id to delete is", keyToDelete);
                                    memRef.addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            for(DataSnapshot snap: snapshot.getChildren()){
                                                String username = snap.child("username").getValue(String.class);
                                                if(username.equals(FirebaseDB.currentUser.getUsername())){
                                                    memToDelete = snap.getKey();


                                                    pudRef.addValueEventListener(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                            for(DataSnapshot snap: snapshot.getChildren()){
                                                                Puddle pud = snap.getValue(Puddle.class);

                                                                if(pud.getId().equals(puddleId)){
                                                                    pudRef.removeEventListener(this);
                                                                    puddleMap.put("id", puddleId);
                                                                    puddleMap.put("name", pud.getName());
                                                                    puddleMap.put("bio", pud.getBio());
                                                                    puddleMap.put("isPrivate", pud.getIsPrivate());
                                                                    puddleMap.put("isGlobal", pud.getIsGlobal());
                                                                    puddleMap.put("bannerUrl", pud.getBannerUrl());
                                                                    puddleMap.put("range", pud.getRange());
                                                                    puddleMap.put("category", pud.getCategory());
                                                                    puddleMap.put("count", Integer.valueOf(pud.getCount()) - 1);
                                                                    puddleMap.put("Location", pud.getLocation());

                                                                    //  1. remove the lisitng from users child
                                                                    if(!keyToDelete.equals(""))   ref.child(keyToDelete).removeValue();

                                                                    // 2. Reduce the count in puddle child by 1.
                                                                    pudRef.child(puddleId).setValue(puddleMap);

                                                                    //  3. Remove the user from members child
                                                                    if(memToDelete != null) memRef.child(memToDelete).removeValue();

                                                                    startActivity(new Intent(SettingsActivity.this, PuddleListActivity.class));
                                                                    finish();
                                                                    break;
                                                                }
                                                            }
                                                        }

                                                        @Override
                                                        public void onCancelled(@NonNull DatabaseError error) {

                                                        }
                                                    });

                                                    break;
                                                }
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    });
                                    break;
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                }
            }).start();




//             1. remove the lisitng from users child
//            if(keyToDelete != "")   ref.child(keyToDelete).removeValue();
//
//             2. Reduce the count in puddle child by 1.
//            pudRef.setValue(puddleMap);
//
//             3. Remove the user from members child
//            if(memToDelete != null) memRef.child(memToDelete).removeValue();


        } else if(view.getId() == R.id.report_puddle_layout){
            Toast.makeText(SettingsActivity.this, "Puddle Reported!!", Toast.LENGTH_SHORT).show();
        }
    }
}
