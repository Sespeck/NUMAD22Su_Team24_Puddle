package com.cs5520.assignments.numad22su_team24_puddle;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.imageview.ShapeableImageView;
import com.cs5520.assignments.numad22su_team24_puddle.Model.PuddleMarker;

public class MapFragment extends Fragment{
    int memberCount;
    String puddleName;
    TextView mapMemberCount, mapPuddleName;
    ImageView mapPuddleBg, mapOpenPuddleButton;

    public MapFragment(){}

    public static MapFragment newInstance(PuddleMarker puddleMarker) {
        MapFragment fragment = new MapFragment();
        fragment.memberCount= puddleMarker.getMemberCount();
        fragment.puddleName= puddleMarker.getName();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.map_puddle, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mapMemberCount= view.findViewById(R.id.map_member_count);
        mapPuddleName= view.findViewById(R.id.map_puddle_name);
        mapPuddleBg= view.findViewById(R.id.map_puddle_bg);
        mapOpenPuddleButton= view.findViewById(R.id.map_open_puddle_button);

        mapMemberCount.setText(String.valueOf(memberCount));
        mapPuddleName.setText(puddleName);
        mapOpenPuddleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Activity activity = getActivity();
                View layoutView = View.inflate(activity, R.layout.puddle_modal, null);
                AlertDialog dialog = new MaterialAlertDialogBuilder(activity).setTitle(puddleName).setView(layoutView).create();
                TextView tv = layoutView.findViewById(R.id.puddle_modal_name_tv);
                tv.setText("Puddle Description");
                ShapeableImageView im = layoutView.findViewById(R.id.puddle_modal_item_image);
                im.setImageBitmap(BitmapFactory.decodeResource(activity.getResources(),
                        R.drawable.pub_night));
                MaterialButton button = layoutView.findViewById(R.id.puddle_modal_join_btn);
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(activity, PuddleChatroomActivity.class);
                        activity.startActivity(intent);
//                       Toast.makeText(getContext(), "You clicked on "+mapPuddleName.getText().toString(), Toast.LENGTH_SHORT).show();
                    }
                });
                dialog.show();
            }
        });
    }
}

