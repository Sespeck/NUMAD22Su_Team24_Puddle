package com.cs5520.assignments.numad22su_team24_puddle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.cs5520.assignments.numad22su_team24_puddle.Adapter.PuddleListAdapter;
import com.cs5520.assignments.numad22su_team24_puddle.Utils.FirebaseDB;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PuddleListActivity extends AppCompatActivity {

    // Widgets
    RecyclerView puddleListRecyclerView;
    PuddleListAdapter puddleListAdapter;
    DrawerLayout drawer;
    NavigationView navView;
    TextView user_email;
    TextView user_name;

    // Firebase
    FirebaseUser current_user;
    DatabaseReference userRef;

    private HashMap<String, String> userDetails;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_puddle_list);
        userDetails = new HashMap<>();

        // Api Calls
        fetchCurrentUserData();

        // Initializing Widgets
        puddleListRecyclerView = findViewById(R.id.puddle_list_rv);

        // Initializing RecyclerView
        puddleListRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        puddleListAdapter = new PuddleListAdapter(this, getPuddleList());
        puddleListRecyclerView.setAdapter(puddleListAdapter);
    }

    private List<List<Puddle>> getPuddleList() {
        List<List<Puddle>> puddlesList= new ArrayList<>();
        Bitmap bitmap = BitmapFactory.decodeResource(this.getResources(), R.drawable.puddle);
        for(Category category: Category.values()) {
            List<Puddle> puddleArray = new ArrayList<>();
            puddleArray.add(new Puddle("Number "+(category.id+1), "Number "+(category.id+1), bitmap));
            puddleArray.add(new Puddle("Number "+(category.id+1), "Number "+(category.id+1), bitmap));
            puddleArray.add(new Puddle("Number "+(category.id+1), "Number "+(category.id+1), bitmap));
            puddleArray.add(new Puddle("Number "+(category.id+1), "Number "+(category.id+1), bitmap));
            puddleArray.add(new Puddle("Number "+(category.id+1), "Number "+(category.id+1), bitmap));
            puddleArray.add(new Puddle("Number "+(category.id+1), "Number "+(category.id+1), bitmap));
            puddleArray.add(new Puddle("Number "+(category.id+1), "Number "+(category.id+1), bitmap));
            puddleArray.add(new Puddle("Number "+(category.id+1), "Number "+(category.id+1), bitmap));
            puddleArray.add(new Puddle("Number "+(category.id+1), "Number "+(category.id+1), bitmap));
            puddlesList.add(puddleArray);
        }
        return puddlesList;
    }

    public void clickProfile(View view){
//        drawer.openDrawer(GravityCompat.START);
        Intent intent = new Intent(this, MyPuddles.class);
        startActivity(intent);
    }

    public void fetchCurrentUserData(){
        current_user = FirebaseDB.getCurrentUser();

        userRef = FirebaseDB.getDataReference(getString(R.string.users)).child(current_user.getUid());
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot snap: snapshot.getChildren()){
                    userDetails.put(snap.getKey(), snap.getValue(String.class));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }
}