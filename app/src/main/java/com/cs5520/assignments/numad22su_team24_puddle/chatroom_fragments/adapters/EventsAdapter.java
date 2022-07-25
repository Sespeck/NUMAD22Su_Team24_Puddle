package com.cs5520.assignments.numad22su_team24_puddle.chatroom_fragments.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.cs5520.assignments.numad22su_team24_puddle.R;

import java.util.List;

public class EventsAdapter extends RecyclerView.Adapter<EventsAdapter.EventViewHolder> {
    private List<Event> eventList;


    public EventsAdapter(List<Event> eventList){
        this.eventList = eventList;
    }

    public static class EventViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView name;
        public EventViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.recycler_view_item_event_name);
            imageView = itemView.findViewById(R.id.recycler_view_item_bg_image);
        }
    }

    @NonNull
    @Override
    public EventsAdapter.EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new EventViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.event_recycler_view_item,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull EventsAdapter.EventViewHolder holder, int position) {
        Event event = eventList.get(position);
        holder.imageView.setImageBitmap(event.backgroundImg);
        holder.name.setText(event.getName());
    }

    @Override
    public int getItemCount() {
        return eventList.size();
    }
}
