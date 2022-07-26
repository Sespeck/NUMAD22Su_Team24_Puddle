package com.cs5520.assignments.numad22su_team24_puddle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

public class PuddleListActivity extends AppCompatActivity {

    // Widgets
    RecyclerView puddleListRecyclerView;
    PuddleListAdapter puddleListAdapter;
    DrawerLayout drawer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_puddle_list);
        // Changed to incorporate navigation drawer activity
        setContentView(R.layout.nav_activity_main);

        puddleListRecyclerView = findViewById(R.id.puddle_list_rv);
        drawer = findViewById(R.id.drawer_layout);
        puddleListRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        puddleListAdapter = new PuddleListAdapter(this, getPuddleList());
        puddleListRecyclerView.setAdapter(puddleListAdapter);
    }

    private List<List<Puddle>> getPuddleList() {
        List<List<Puddle>> puddlesList= new ArrayList<>();
        for(Category category: Category.values()) {
            List<Puddle> puddleArray = new ArrayList<>();
            puddleArray.add(new Puddle("Number "+(category.id+1), "Number 1", BitmapFactory.decodeResource(this.getResources(), R.drawable.puddle)));
            puddleArray.add(new Puddle("Number "+(category.id+1), "Number 1", BitmapFactory.decodeResource(this.getResources(), R.drawable.puddle)));
            puddleArray.add(new Puddle("Number "+(category.id+1), "Number 1", BitmapFactory.decodeResource(this.getResources(), R.drawable.puddle)));
            puddleArray.add(new Puddle("Number "+(category.id+1), "Number 1", BitmapFactory.decodeResource(this.getResources(), R.drawable.puddle)));
            puddleArray.add(new Puddle("Number "+(category.id+1), "Number 1", BitmapFactory.decodeResource(this.getResources(), R.drawable.puddle)));
            puddleArray.add(new Puddle("Number "+(category.id+1), "Number 1", BitmapFactory.decodeResource(this.getResources(), R.drawable.puddle)));
            puddleArray.add(new Puddle("Number "+(category.id+1), "Number 1", BitmapFactory.decodeResource(this.getResources(), R.drawable.puddle)));
            puddleArray.add(new Puddle("Number "+(category.id+1), "Number 1", BitmapFactory.decodeResource(this.getResources(), R.drawable.puddle)));
            puddleArray.add(new Puddle("Number "+(category.id+1), "Number 1", BitmapFactory.decodeResource(this.getResources(), R.drawable.puddle)));
            puddlesList.add(puddleArray);
        }
        return puddlesList;
    }

    public void clickProfile(View view){
//        nav_view
//        drawer.openDrawer(Gravity.LEFT);
        drawer.openDrawer(GravityCompat.START);
    }
}