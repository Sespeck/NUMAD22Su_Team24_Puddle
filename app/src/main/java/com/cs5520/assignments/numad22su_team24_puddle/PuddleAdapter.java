package com.cs5520.assignments.numad22su_team24_puddle;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.imageview.ShapeableImageView;

import java.util.List;

public class PuddleAdapter extends RecyclerView.Adapter<PuddleAdapter.PuddleViewHolder> {

    Context context;
    List<Puddle> puddleList;

    public class PuddleViewHolder extends RecyclerView.ViewHolder {
        TextView puddleName;
        ShapeableImageView puddleImage;
        public PuddleViewHolder(@NonNull View itemView) {
            super(itemView);
            puddleName = itemView.findViewById(R.id.puddle_name_tv);
            puddleImage = itemView.findViewById(R.id.puddle_item_image);
        }
    }

    PuddleAdapter(Context context, List<Puddle> puddleList) {
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
        holder.puddleImage.setImageBitmap(puddle.getDisplayImage());
        holder.puddleImage.setShapeAppearanceModel(holder.puddleImage.getShapeAppearanceModel().withCornerSize(25));
    }

    @Override
    public int getItemCount() {
        return puddleList.size();
    }

}
