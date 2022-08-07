package com.cs5520.assignments.numad22su_team24_puddle;

import android.content.Context;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import com.google.android.material.imageview.ShapeableImageView;

public class PuddleDialog extends AlertDialog {
    Context context;
    Puddle puddle;


    protected PuddleDialog(@NonNull Context context) {
        super(context);
    }

    public void setPuddle(Puddle puddle) {
        this.puddle = puddle;
    }
}
