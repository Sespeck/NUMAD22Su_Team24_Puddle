package com.cs5520.assignments.numad22su_team24_puddle.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.cs5520.assignments.numad22su_team24_puddle.Puddle;
import com.cs5520.assignments.numad22su_team24_puddle.R;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class PuddleAdapter extends RecyclerView.Adapter<PuddleAdapter.PuddleViewHolder> {

    Context context;
    List<Puddle> puddleList;

    public class PuddleViewHolder extends RecyclerView.ViewHolder {
        TextView puddleName;
        ImageView puddleImage;
        public PuddleViewHolder(@NonNull View itemView) {
            super(itemView);
            puddleName = itemView.findViewById(R.id.puddle_name_tv);
            puddleImage = itemView.findViewById(R.id.puddle_item_image);
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
//        holder.puddleImage.setImageBitmap(puddle.getDisplayImage());
    }

    @Override
    public int getItemCount() {
        return puddleList.size();
    }

}
