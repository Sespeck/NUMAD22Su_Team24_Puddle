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
            new Thread(new Runnable() {
                @Override
                public void run() {
                    DatabaseReference ref = FirebaseDB.getDataReference("Users").child(FirebaseDB.getLocalUser().getId()).child("my_puddles");
                    DatabaseReference pudRef = FirebaseDB.getDataReference("Puddles");
                    DatabaseReference memRef = FirebaseDB.getDataReference("Members").child(puddleId);


                    ValueEventListener puddleValueEventListener = new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for(DataSnapshot snap: snapshot.getChildren()){
                                Puddle pud = snap.getValue(Puddle.class);

                                if(pud.getId().equals(puddleId)){
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

                                    pudRef.removeEventListener(this);

                                    if(!keyToDelete.equals(""))   ref.child(keyToDelete).removeValue();

                                    pudRef.child(puddleId).setValue(puddleMap);

                                    if(!memToDelete.equals("")) memRef.child(memToDelete).removeValue();


                                    Intent intent = new Intent(SettingsActivity.this, PuddleListActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(intent);

                                    finish();
                                    break;
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    };


                    ValueEventListener memValueEventListener = new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for(DataSnapshot snap: snapshot.getChildren()){
                                String username = snap.child("username").getValue(String.class);
                                if(username.equals(FirebaseDB.getLocalUser().getUsername())){
                                    memToDelete = snap.getKey();
                                    memRef.removeEventListener(this);
                                    pudRef.addValueEventListener(puddleValueEventListener);
                                    break;
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    };

                    ValueEventListener refValueEventListener = new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for(DataSnapshot snap: snapshot.getChildren()){
                                String val = snap.getValue(String.class);

                                if(val.equals(puddleId)){
                                    keyToDelete = snap.getKey();
                                    ref.removeEventListener(this);
                                    memRef.addValueEventListener(memValueEventListener);
                                    break;
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    };

                    ref.addValueEventListener(refValueEventListener);
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
