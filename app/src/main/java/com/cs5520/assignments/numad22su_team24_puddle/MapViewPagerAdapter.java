package com.cs5520.assignments.numad22su_team24_puddle;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.cs5520.assignments.numad22su_team24_puddle.Model.PuddleMarker;

import java.util.ArrayList;
import java.util.List;

public class MapViewPagerAdapter extends FragmentPagerAdapter {
    List<PuddleMarker> list ;
    List<MapFragment> fragmentList = new ArrayList<>();

    public MapViewPagerAdapter(FragmentManager fm, List<PuddleMarker> list) {
        super(fm);
        this.list = list;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        MapFragment fragment = MapFragment.newInstance(list.get(position));
        fragmentList.add(fragment);
        return fragment;

    }


    @Override
    public int getCount() {
        return list.size();
    }

}

