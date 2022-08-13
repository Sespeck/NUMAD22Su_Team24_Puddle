package com.cs5520.assignments.numad22su_team24_puddle.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.cs5520.assignments.numad22su_team24_puddle.Model.Puddle;
import com.cs5520.assignments.numad22su_team24_puddle.PuddleListActivity;
import com.cs5520.assignments.numad22su_team24_puddle.R;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.cs5520.assignments.numad22su_team24_puddle.Utils.FirebaseDB;
import com.google.android.material.imageview.ShapeableImageView;

import java.util.List;

public class PuddleAdapter extends RecyclerView.Adapter<PuddleAdapter.PuddleViewHolder> {

    Context context;
    List<Puddle> puddleList;

    public class PuddleViewHolder extends RecyclerView.ViewHolder {
        TextView puddleName;
        ShapeableImageView puddleImage;
        TextView memCount;
        ImageView globalIcon;

        public PuddleViewHolder(@NonNull View itemView) {
            super(itemView);
            puddleName = itemView.findViewById(R.id.puddle_name_tv);
            puddleImage = itemView.findViewById(R.id.puddle_item_image);
            memCount = itemView.findViewById(R.id.members_count);
            globalIcon = itemView.findViewById(R.id.global_icon);
        }
    }

    public PuddleAdapter(Context context, List<Puddle> puddleList) {
        this.context = context;
        this.puddleList = puddleList;
    }

    @NonNull
    @Override
    public PuddleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new PuddleViewHolder(LayoutInflater.from(context).inflate(R.layout.puddle_rv_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull PuddleViewHolder holder, int position) {
        Puddle puddle = puddleList.get(position);
        holder.puddleName.setText(puddle.getName());
        holder.memCount.setText(puddle.getCount() + " Members");

        holder.globalIcon.setVisibility(puddle.getIsGlobal().equals("true") ? ImageView.VISIBLE : ImageView.GONE);

        Glide.with(context).load(puddle.getBannerUrl()).into(holder.puddleImage);
        holder.puddleImage.setShapeAppearanceModel(holder.puddleImage.getShapeAppearanceModel().withCornerSize(25));
        holder.puddleImage.setColorFilter(R.color.black);
        holder.itemView.setOnClickListener(view -> {
            ((PuddleListActivity) context).showJoinPuddleDialogue(context, puddle);
        });
    }

    @Override
    public int getItemCount() {
        return puddleList.size();
    }

}
