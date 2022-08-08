package com.cs5520.assignments.numad22su_team24_puddle.chatroom_fragments;

import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cs5520.assignments.numad22su_team24_puddle.R;
import com.cs5520.assignments.numad22su_team24_puddle.Utils.FirebaseDB;
import com.cs5520.assignments.numad22su_team24_puddle.Utils.Util;
import com.cs5520.assignments.numad22su_team24_puddle.chatroom_fragments.adapters.Event;
import com.cs5520.assignments.numad22su_team24_puddle.chatroom_fragments.adapters.EventsAdapter;
import com.cs5520.assignments.numad22su_team24_puddle.chatroom_fragments.dialogs.DateTimeFormatUtil;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

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
    private ShimmerFrameLayout shimmerFrameLayout;
    private endShimmerEffectCallback callback = new endShimmerEffectCallback(){
        @Override
        public void onLayoutInflated() {
            handler.postDelayed((Runnable) () -> {
                shimmerFrameLayout.stopShimmer();
                shimmerFrameLayout.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
            }, 800);
        }
    };

    public interface endShimmerEffectCallback {
        void onLayoutInflated();
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.events_fragment,container,false);
        puddleID = getArguments().getString("puddleID");
        this.recyclerView = view.findViewById(R.id.event_recycler_view);
        shimmerFrameLayout = view.findViewById(R.id.events_shimmer_layout);
        recyclerView.hasFixedSize();
        if (!Util.eventsPopulated) {
            shimmerFrameLayout.startShimmer();
            shimmerFrameLayout.setVisibility(View.VISIBLE);
            recyclerView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    if (callback != null) {
                        callback.onLayoutInflated();
                    }
                    recyclerView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                }
            });
            Util.eventsPopulated = true;
        } else{
            shimmerFrameLayout.stopShimmer();
            shimmerFrameLayout.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(layoutManager);
        eventsRef = FirebaseDB.getDataReference("Events");
        initializeRecyclerView();
        initializeFragmentResultListener();
        return view;
    }

    private void initializeFragmentResultListener(){
        getParentFragmentManager().setFragmentResultListener("event_creation_result",this,((requestKey, result) -> {
            class CreateNewEventRunnable implements Runnable{
                @Override
                public void run() {
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
                        eventsAdapter.addNewEvent(new Event(title,startingTimestamp,endingTimestamp,null,description,result.getString("image_uri"),0));
                    });
                    eventsRef.child(puddleID).push().setValue(newEvent);
                }
            }
            Thread worker = new Thread(new CreateNewEventRunnable());
            worker.start();
        }));
    }



    private void initializeRecyclerView() {
        eventsRef.child(puddleID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                class GetAllEventsFromDB implements Runnable {

                    @Override
                    public void run() {
                        List<Event> eventList = new ArrayList<>();
                        for (DataSnapshot snap : snapshot.getChildren()) {
                            String title = snap.child("title").getValue(String.class);
                            String description = snap.child("description").getValue(String.class);
                            String startingTimestamp = snap.child("starting_timestamp").getValue(String.class);
                            String endingTimestamp = snap.child("ending_timestamp").getValue(String.class);
                            String imageUri = snap.child("image_uri").getValue(String.class);
                            int attendanceCounter = Integer.parseInt(Objects.requireNonNull(snap.child("attendance_counter").getValue(String.class)));
                            eventList.add(new Event(title, startingTimestamp, endingTimestamp, null, description, imageUri, attendanceCounter));
                        }
                        handler.post(() -> {
                            eventsAdapter = new EventsAdapter(eventList, getContext(), eventsRef);
                            recyclerView.setAdapter(eventsAdapter);
                        });
                    }
                }
                Thread worker = new Thread(new GetAllEventsFromDB());
                worker.start();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
