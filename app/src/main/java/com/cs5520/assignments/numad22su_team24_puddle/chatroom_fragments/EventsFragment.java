package com.cs5520.assignments.numad22su_team24_puddle.chatroom_fragments;

import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cs5520.assignments.numad22su_team24_puddle.R;
import com.cs5520.assignments.numad22su_team24_puddle.chatroom_fragments.adapters.Event;
import com.cs5520.assignments.numad22su_team24_puddle.chatroom_fragments.adapters.EventsAdapter;

import java.util.ArrayList;
import java.util.List;

public class EventsFragment extends Fragment {
    RecyclerView recyclerView;
    EventsAdapter eventsAdapter;
    Handler handler = new Handler();
    Fragment currentFragment = this;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.events_fragment,container,false);
        this.recyclerView = view.findViewById(R.id.event_recycler_view);
        recyclerView.hasFixedSize();
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        initializeRecyclerView();
        initializeFragmentResultListener();
        return view;
    }

    private void initializeFragmentResultListener(){
        getParentFragmentManager().setFragmentResultListener("event_creation_result",this,((requestKey, result) -> {

        }));
    }



    private void initializeRecyclerView(){
         class adapterRunnable implements Runnable{
             @Override
             public void run() {
                 List<Event> eventList = new ArrayList<>();
                 eventList.add(new Event("NYC BLOCK PARTY ALL COUPLES WELCOME WHOO","Thurs, Aug 28: 7:00 PM",null,"partytime", BitmapFactory.decodeResource(currentFragment.getResources(),R.drawable.puddle),0));
                 eventList.add(new Event("Party2","time",null,"partytime", BitmapFactory.decodeResource(currentFragment.getResources(),R.drawable.puddle),0));
                 handler.post(()->{
                     eventsAdapter = new EventsAdapter(eventList);
                     recyclerView.setAdapter(eventsAdapter);
                 });
             }
         }
         Thread worker = new Thread(new adapterRunnable());
         worker.start();
    }
}
