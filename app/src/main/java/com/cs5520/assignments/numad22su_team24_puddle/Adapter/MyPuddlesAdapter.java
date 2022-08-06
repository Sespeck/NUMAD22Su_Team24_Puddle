package com.cs5520.assignments.numad22su_team24_puddle.Adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.cs5520.assignments.numad22su_team24_puddle.Model.Puddles;
import com.cs5520.assignments.numad22su_team24_puddle.PuddleChatroomActivity;
import com.cs5520.assignments.numad22su_team24_puddle.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class MyPuddlesAdapter extends RecyclerView.Adapter<MyPuddlesAdapter.MyPudAdapter> {

    Context ct;
    HashMap<String, Puddles> myPuddlesData;
    List<Puddles> myPuddleList = new ArrayList<>();
    List<String> myPuddleKeys = new ArrayList<>();

    public MyPuddlesAdapter(Context ct, HashMap<String, Puddles> myPuddlesData){
        this.ct = ct;
        this.myPuddlesData = myPuddlesData;

        for(Map.Entry<String, Puddles> map: myPuddlesData.entrySet()){
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
        Puddles myPud = myPuddleList.get(position);

        holder.puddleName.setText(myPud.getName());
        holder.memberCount.setText(String.valueOf(myPud.getCount()) + " Members");
        Glide.with(this.ct).load(myPud.getBannerUrl()).into(holder.puddleBg);

        holder.itemView.setOnClickListener((v) -> {
            Intent intent = new Intent(ct, PuddleChatroomActivity.class);
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

        public MyPudAdapter(@NonNull View itemView) {
            super(itemView);
            puddleBg = itemView.findViewById(R.id.puddle_bg);
            puddleBg.setColorFilter(R.color.black);
            puddleName = itemView.findViewById(R.id.puddle_name);
            memberCount = itemView.findViewById(R.id.members_count);
        }
    }
}
