package com.cs5520.assignments.numad22su_team24_puddle;

import android.content.Context;
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
import androidx.fragment.app.Fragment;

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
               Toast.makeText(getContext(), "You clicked on "+mapPuddleName.getText().toString(), Toast.LENGTH_SHORT).show();
           }
         }
        );

    }
}
