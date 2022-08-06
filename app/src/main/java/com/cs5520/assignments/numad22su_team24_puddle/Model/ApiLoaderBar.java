package com.cs5520.assignments.numad22su_team24_puddle.Model;

import android.app.Activity;
import android.app.AlertDialog;
import android.view.LayoutInflater;

import com.cs5520.assignments.numad22su_team24_puddle.R;

public class ApiLoaderBar {
    Activity activity;
    AlertDialog dialog;

    public ApiLoaderBar(Activity a){
        this.activity = a;
    }

    public void showDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this.activity);
        LayoutInflater inflater = activity.getLayoutInflater();
        builder.setView(inflater.inflate(R.layout.progress_bar_layout, null));

        dialog = builder.create();
        dialog.show();
    }

    public void dismissBar(){
        dialog.dismiss();
    }
}
