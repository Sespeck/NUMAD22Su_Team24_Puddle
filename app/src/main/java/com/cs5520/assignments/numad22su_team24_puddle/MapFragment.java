package com.cs5520.assignments.numad22su_team24_puddle;

import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.cs5520.assignments.numad22su_team24_puddle.model.PuddleMarker;

public class MapFragment extends Fragment{
    Context context;
    int memberCount;
    String puddleName;
    TextView mapMemberCount, mapPuddleName;

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
        mapMemberCount.setText(String.valueOf(memberCount));
        mapPuddleName.setText(puddleName);

    }


}
