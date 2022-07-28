package com.cs5520.assignments.numad22su_team24_puddle.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.cs5520.assignments.numad22su_team24_puddle.R;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class MyPuddlesAdapter extends RecyclerView.Adapter<MyPuddlesAdapter.MyPudAdapter> {

    Context ct;

    public MyPuddlesAdapter(Context ct){
        this.ct = ct;
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
//        holder.puddleName.setText("Android App Development");
        Glide.with(this.ct).load("https://cdn.pixabay.com/photo/2016/05/05/02/37/sunset-1373171__480.jpg").into(holder.puddleBg);
    }

    @Override
    public int getItemCount() {
        return 5;
    }

    public class MyPudAdapter extends RecyclerView.ViewHolder {

        ImageView puddleBg;
        TextView puddleName;

        public MyPudAdapter(@NonNull View itemView) {
            super(itemView);
            puddleBg = itemView.findViewById(R.id.puddle_bg);
//            puddleName = itemView.findViewById(R.id.puddle_name);
        }
    }
}
