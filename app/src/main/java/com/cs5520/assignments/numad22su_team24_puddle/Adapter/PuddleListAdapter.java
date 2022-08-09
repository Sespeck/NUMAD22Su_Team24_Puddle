package com.cs5520.assignments.numad22su_team24_puddle.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cs5520.assignments.numad22su_team24_puddle.Category;
import com.cs5520.assignments.numad22su_team24_puddle.Model.Puddle;
import com.cs5520.assignments.numad22su_team24_puddle.R;
import com.cs5520.assignments.numad22su_team24_puddle.Utils.Util;

import java.util.List;

public class PuddleListAdapter extends RecyclerView.Adapter<PuddleListAdapter.PuddleListViewHolder> {

    Context context;
    List<List<Puddle>> puddlesList;

    public class PuddleListViewHolder extends RecyclerView.ViewHolder {
        TextView puddleCategoryName;
        RecyclerView puddleItemRecyclerView;
        PuddleAdapter puddleAdapter;
        public PuddleListViewHolder(@NonNull View itemView) {
            super(itemView);
            puddleCategoryName = itemView.findViewById(R.id.puddle_category_name);
            puddleItemRecyclerView = itemView.findViewById(R.id.puddles_rv);
        }
    }

    public PuddleListAdapter(Context context, List<List<Puddle>> puddlesList) {
        this.context = context;
        this.puddlesList = puddlesList;
    }

    @NonNull
    @Override
    public PuddleListAdapter.PuddleListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new PuddleListViewHolder(LayoutInflater.from(context).inflate(R.layout.puddle_category_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull PuddleListAdapter.PuddleListViewHolder holder, int position) {
        Category category = Util.categoryMap.get(position);
        holder.puddleCategoryName.setText(category.toString());
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        holder.puddleItemRecyclerView.setLayoutManager(linearLayoutManager);
        holder.puddleAdapter = new PuddleAdapter(context, puddlesList.get(position));
        holder.puddleItemRecyclerView.setAdapter(holder.puddleAdapter);
    }

    @Override
    public int getItemCount() {
        return puddlesList.size();
    }


}
