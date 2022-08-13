package com.cs5520.assignments.numad22su_team24_puddle.Adapter;

import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.cs5520.assignments.numad22su_team24_puddle.Model.Puddle;
import com.cs5520.assignments.numad22su_team24_puddle.PuddleChatroomActivity;
import com.cs5520.assignments.numad22su_team24_puddle.R;
import com.cs5520.assignments.numad22su_team24_puddle.Utils.Util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class MyPuddlesAdapter extends RecyclerView.Adapter<MyPuddlesAdapter.MyPudAdapter> {

    Context ct;
    HashMap<String, Puddle> myPuddlesData;
    List<Puddle> myPuddleList = new ArrayList<>();
    List<String> myPuddleKeys = new ArrayList<>();

    public MyPuddlesAdapter(Context ct, HashMap<String, Puddle> myPuddlesData){
        this.ct = ct;
        this.myPuddlesData = myPuddlesData;

        for(Map.Entry<String, Puddle> map: myPuddlesData.entrySet()) {
            myPuddleKeys.add(map.getKey());
            myPuddleList.add(map.getValue());
        }

    }

    @NonNull
    @Override
    public MyPudAdapter onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(this.ct);
        View view = layoutInflater.inflate(R.layout.my_puddles, parent, false);
        return new MyPudAdapter(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyPudAdapter holder, int position) {
        Puddle myPud = myPuddleList.get(position);

        holder.puddleName.setText(myPud.getName());
        holder.memberCount.setText(String.valueOf(myPud.getCount()) + " Members");
        Glide.with(this.ct).load(myPud.getBannerUrl()).into(holder.puddleBg);
        holder.privateLock.setVisibility(myPud.getIsPrivate().equals("true") ? ImageView.VISIBLE : ImageView.GONE);
        holder.global.setVisibility(myPud.getIsGlobal().equals("true") ? ImageView.VISIBLE : ImageView.GONE);

        holder.itemView.setOnClickListener((v) -> {
            Util.isPuddleListForeground = false;
            Intent intent = new Intent(ct, PuddleChatroomActivity.class);
            intent.putExtra("puddleID", myPuddleKeys.get(position));
            ct.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return myPuddleList.size();
    }

    public class MyPudAdapter extends RecyclerView.ViewHolder {

        ImageView puddleBg;
        TextView puddleName;
        TextView memberCount;
        ImageView privateLock;
        ImageView global;

        public MyPudAdapter(@NonNull View itemView) {
            super(itemView);
            puddleBg = itemView.findViewById(R.id.puddle_bg);
            puddleBg.setColorFilter(R.color.black);
            puddleName = itemView.findViewById(R.id.puddle_name);
            memberCount = itemView.findViewById(R.id.members_count);
            privateLock = itemView.findViewById(R.id.private_icon);
            global = itemView.findViewById(R.id.global_icon);
        }
    }
}
