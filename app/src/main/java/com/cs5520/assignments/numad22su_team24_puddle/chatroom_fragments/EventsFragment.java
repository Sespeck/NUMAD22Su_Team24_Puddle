package com.cs5520.assignments.numad22su_team24_puddle.chatroom_fragments;

import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cs5520.assignments.numad22su_team24_puddle.R;
import com.cs5520.assignments.numad22su_team24_puddle.Utils.FirebaseDB;
import com.cs5520.assignments.numad22su_team24_puddle.chatroom_fragments.adapters.Event;
import com.cs5520.assignments.numad22su_team24_puddle.chatroom_fragments.adapters.EventsAdapter;
import com.cs5520.assignments.numad22su_team24_puddle.chatroom_fragments.dialogs.DateTimeFormatUtil;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class EventsFragment extends Fragment {
    private RecyclerView recyclerView;
    private EventsAdapter eventsAdapter;
    private Handler handler = new Handler();
    private String puddleID;
    private DatabaseReference eventsRef;


    public EventsFragment(String puddleID){
        this.puddleID = puddleID;
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.events_fragment,container,false);
        this.recyclerView = view.findViewById(R.id.event_recycler_view);
        recyclerView.hasFixedSize();
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        eventsRef = FirebaseDB.getDataReference("Events");
        initializeRecyclerView();
        initializeFragmentResultListener();
        return view;
    }

    private void initializeFragmentResultListener(){
        getParentFragmentManager().setFragmentResultListener("event_creation_result",this,((requestKey, result) -> {
            String startingDate = result.getString("starting_date");
            String endingDate = result.getString("ending_date");
            String backgroundImgUri = result.getString("image_uri").equals("load_default_image") ?
                    Uri.parse("android.resource://"+ Objects.requireNonNull(R.class.getPackage()).getName()+"/"
                            +R.drawable.puddle).toString() : result.getString("image_uri");
            String startingTime = result.getString("starting_time");
            String endingTime = result.getString("ending_time");
            String description = result.getString("description") == null ? "We don't know anything about this event, but we " +
                    "know it'll be great!" : result.getString("description");
            String title = result.getString("title");
            String startingTimestamp = DateTimeFormatUtil.formatEventDate(startingDate) + " : " + startingTime;
            String endingTimestamp = DateTimeFormatUtil.formatEventDate(endingDate) +" : " +endingTime;
            HashMap<String, Object> newEvent = new HashMap<>();
            newEvent.put("title",title);
            newEvent.put("description",description);
            newEvent.put("starting_timestamp", startingTimestamp);
            newEvent.put("ending_timestamp", endingTimestamp);
            newEvent.put("image_uri",backgroundImgUri);
            newEvent.put("attendance_counter","0");
            handler.post(()->{
                eventsAdapter.addNewEvent(new Event(title,startingTimestamp,endingTimestamp,null,description,backgroundImgUri,0));
            });
            eventsRef.child(puddleID).push().setValue(newEvent);
        }));
    }



    private void initializeRecyclerView(){
         class adapterRunnable implements Runnable{
             @Override
             public void run() {
                 List<Event> eventList = new ArrayList<>();
                 handler.post(()->{
                     eventsAdapter = new EventsAdapter(eventList, getContext());
                     recyclerView.setAdapter(eventsAdapter);
                 });
             }
         }
         Thread worker = new Thread(new adapterRunnable());
         worker.start();
    }
}
