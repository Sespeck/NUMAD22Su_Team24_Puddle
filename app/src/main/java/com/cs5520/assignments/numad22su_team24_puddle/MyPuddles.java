package com.cs5520.assignments.numad22su_team24_puddle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.cs5520.assignments.numad22su_team24_puddle.Adapter.MyPuddlesAdapter;

public class MyPuddles extends AppCompatActivity {

    // Widgets
    RecyclerView myPuddles;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_puddles);

        // Initializing Widgets
        myPuddles = findViewById(R.id.my_puddles_rview);

        // Initializing RecyclerView
        MyPuddlesAdapter adapter = new MyPuddlesAdapter(MyPuddles.this);
        myPuddles.setAdapter(adapter);
        myPuddles.setLayoutManager(new GridLayoutManager(this, 2));
    }
}