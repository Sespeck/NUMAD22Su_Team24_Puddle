package com.cs5520.assignments.numad22su_team24_puddle;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class SettingsActivity extends AppCompatActivity implements View.OnClickListener {

    TextView sharePuddle;
    String puddleId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity_layout);
        sharePuddle = findViewById(R.id.settings_share_puddle);
        puddleId = getIntent().getStringExtra("PuddleId");
        sharePuddle.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.settings_share_puddle) {
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("text/plain");
            String appLink = "http://puddle-team24-app.com/join/" + puddleId;
            intent.putExtra(Intent.EXTRA_SUBJECT, appLink);
            intent.putExtra(Intent.EXTRA_TEXT, appLink);
            startActivity(Intent.createChooser(intent, "Share using"));
        }
    }
}
