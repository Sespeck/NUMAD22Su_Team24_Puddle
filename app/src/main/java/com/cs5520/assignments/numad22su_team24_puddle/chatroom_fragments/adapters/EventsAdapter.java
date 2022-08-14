package com.cs5520.assignments.numad22su_team24_puddle.chatroom_fragments.adapters;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.cs5520.assignments.numad22su_team24_puddle.R;
import com.cs5520.assignments.numad22su_team24_puddle.Utils.FirebaseDB;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DatabaseReference;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;

import java.io.IOException;
import java.util.List;
import java.util.logging.Handler;

public class EventsAdapter extends RecyclerView.Adapter<EventsAdapter.EventViewHolder> {
    private List<Event> eventList;
    private Context context;
    private DatabaseReference eventsRef;
    private Activity activity;


    public EventsAdapter(List<Event> eventList, Context context, DatabaseReference eventsRef){
        this.context = context;
        this.eventList = eventList;
        this.eventsRef = eventsRef;
    }

    public static class EventViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView name;
        TextView startingDate;
        TextView endingDate;
        TextView attendanceCounter;
        public EventViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.recycler_view_item_event_name);
            startingDate = itemView.findViewById(R.id.recycler_view_event_starting_datetime);
            endingDate = itemView.findViewById(R.id.recycler_view_event_ending_datetime);
            imageView = itemView.findViewById(R.id.recycler_view_item_bg_image);
            attendanceCounter = itemView.findViewById(R.id.attendance_counter);
        }
    }

    @NonNull
    @Override
    public EventsAdapter.EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new EventViewHolder(LayoutInflater.from(parent.getContext()).
                inflate(R.layout.event_recycler_view_item,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull EventsAdapter.EventViewHolder holder, int position) {
        Event event = eventList.get(position);
        if (event.createdBy.equals(FirebaseDB.getLocalUser().getUsername())){
            holder.imageView.setOnClickListener(v -> {
                new MaterialAlertDialogBuilder(context,
                        R.style.Body_ThemeOverlay_MaterialComponents_MaterialAlertDialog).setTitle(event.name).
                        setMessage(event.description + "\n\n" + event.location + "\n\n" + event.startingDatetime + "\n" +
                                event.endingDatetime).setPositiveButton("RSVP", (dialog, which) -> {
                                    if (!event.hasRsvped.contains(FirebaseDB.currentUser.getUsername())) {
                                        String newCounterValue =
                                                String.valueOf(Integer.parseInt(holder.attendanceCounter.getText().toString()) + 1);
                                        holder.attendanceCounter.setText(newCounterValue);
                                        notifyItemChanged(position);
                                        eventsRef.child(event.id).child("attendance_counter").setValue(newCounterValue);
                                        eventsRef.child(event.id).child("has_rsvped").child(FirebaseDB.currentUser.getUsername()).setValue(true);
                                        Toast.makeText(context, "Successfully RSVPed!",Toast.LENGTH_SHORT).show();
                                    } else{
                                        Toast.makeText(context, "You are no longer RSVPed",Toast.LENGTH_SHORT).show();
                                        String newCounterValue =
                                                String.valueOf(Integer.parseInt(holder.attendanceCounter.getText().toString()) - 1);
                                        eventsRef.child(event.id).child("attendance_counter").setValue(newCounterValue);
                                        eventsRef.child(event.id).child("has_rsvped").child(FirebaseDB.currentUser.getUsername()).removeValue();
                                        event.hasRsvped.remove(FirebaseDB.currentUser.getUsername());
                                    }
                        }).setNegativeButton("Delete", ((dialog, which) -> {
                            eventsRef.child(event.id).removeValue((error, ref) -> {

                            });
                            eventList.remove(event);
                            notifyDataSetChanged();
                        })).show();
            });
        } else{
            holder.imageView.setOnClickListener(v -> {
                new MaterialAlertDialogBuilder(context,
                        R.style.Body_ThemeOverlay_MaterialComponents_MaterialAlertDialog).setTitle(event.name).
                        setMessage(event.description + "\n\n" + event.location + "\n\n" + event.startingDatetime + "\n" +
                                event.endingDatetime).setPositiveButton("RSVP", (dialog, which) -> {
                            if (!event.hasRsvped.contains(FirebaseDB.currentUser.getUsername())) {
                                String newCounterValue =
                                        String.valueOf(Integer.parseInt(holder.attendanceCounter.getText().toString()) + 1);
                                holder.attendanceCounter.setText(newCounterValue);
                                notifyItemChanged(position);
                                eventsRef.child(event.id).child("attendance_counter").setValue(newCounterValue);
                                eventsRef.child(event.id).child("has_rsvped").child(FirebaseDB.currentUser.getUsername()).setValue(true);
                                Toast.makeText(context, "Successfully RSVPed!",Toast.LENGTH_SHORT).show();
                            } else{
                                Toast.makeText(context, "You are no longer RSVPed",Toast.LENGTH_SHORT).show();
                                String newCounterValue = (Integer.parseInt(holder.attendanceCounter.getText().toString()) - 1 <= 0) ? String.valueOf(0) :
                                        String.valueOf(Integer.parseInt(holder.attendanceCounter.getText().toString()) - 1);
                                eventsRef.child(event.id).child("attendance_counter").setValue(newCounterValue);
                                eventsRef.child(event.id).child("has_rsvped").child(FirebaseDB.currentUser.getUsername()).removeValue();
                                event.hasRsvped.remove(FirebaseDB.currentUser.getUsername());
                            }
                        }).show();
            });
        }
        Glide.with(context).load(event.backgroundImgUri).dontAnimate().into(holder.imageView);
        holder.imageView.setColorFilter(R.color.black);
        holder.name.setText(event.getName());
        holder.startingDate.setText(event.getStartingDatetime());
        holder.endingDate.setText(event.getEndingDatetime());
        holder.attendanceCounter.setText(String.valueOf(event.getAttendanceCount()));
    }

    public void addNewEvent(Event event){
        eventList.add(event);
        notifyItemChanged(getItemCount());
    }

    @Override
    public int getItemCount() {
        return eventList.size();
    }
}
