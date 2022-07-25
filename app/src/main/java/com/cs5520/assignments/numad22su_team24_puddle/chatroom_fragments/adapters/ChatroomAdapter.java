package com.cs5520.assignments.numad22su_team24_puddle.chatroom_fragments.adapters;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ChatroomAdapter extends RecyclerView.Adapter<ChatroomAdapter.ChatroomViewHolder> {

    public static class ChatroomViewHolder extends RecyclerView.ViewHolder {
        public ChatroomViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }

    @NonNull
    @Override
    public ChatroomAdapter.ChatroomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull ChatroomAdapter.ChatroomViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }
}
