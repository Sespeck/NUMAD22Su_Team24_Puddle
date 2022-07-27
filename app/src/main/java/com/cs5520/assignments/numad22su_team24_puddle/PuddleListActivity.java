package com.cs5520.assignments.numad22su_team24_puddle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import java.util.ArrayList;
import java.util.List;

public class PuddleListActivity extends AppCompatActivity {

    RecyclerView puddleListRecyclerView;
    PuddleListAdapter puddleListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_puddle_list);

        puddleListRecyclerView = findViewById(R.id.puddle_list_rv);
        puddleListRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        puddleListAdapter = new PuddleListAdapter(this, getPuddleList());
        puddleListRecyclerView.setAdapter(puddleListAdapter);
    }

    private List<List<Puddle>> getPuddleList() {
        List<List<Puddle>> puddlesList= new ArrayList<>();
        Bitmap bitmap = BitmapFactory.decodeResource(this.getResources(), R.drawable.puddle);
        for(Category category: Category.values()) {
            List<Puddle> puddleArray = new ArrayList<>();
            puddleArray.add(new Puddle("Number "+(category.id+1), "Number 1", bitmap));
            puddleArray.add(new Puddle("Number "+(category.id+1), "Number 1", bitmap));
            puddleArray.add(new Puddle("Number "+(category.id+1), "Number 1", bitmap));
            puddleArray.add(new Puddle("Number "+(category.id+1), "Number 1", bitmap));
            puddleArray.add(new Puddle("Number "+(category.id+1), "Number 1", bitmap));
            puddleArray.add(new Puddle("Number "+(category.id+1), "Number 1", bitmap));
            puddleArray.add(new Puddle("Number "+(category.id+1), "Number 1", bitmap));
            puddleArray.add(new Puddle("Number "+(category.id+1), "Number 1", bitmap));
            puddleArray.add(new Puddle("Number "+(category.id+1), "Number 1", bitmap));
            puddlesList.add(puddleArray);
        }
        return puddlesList;
    }
}