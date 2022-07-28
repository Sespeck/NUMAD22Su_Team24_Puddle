package com.cs5520.assignments.numad22su_team24_puddle.chatroom_fragments;

import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cs5520.assignments.numad22su_team24_puddle.R;
import com.cs5520.assignments.numad22su_team24_puddle.chatroom_fragments.adapters.ChatroomAdapter;
import com.cs5520.assignments.numad22su_team24_puddle.chatroom_fragments.adapters.Event;
import com.cs5520.assignments.numad22su_team24_puddle.chatroom_fragments.adapters.EventsAdapter;
import com.cs5520.assignments.numad22su_team24_puddle.chatroom_fragments.adapters.Message;

import java.util.ArrayList;
import java.util.List;

public class ChatroomFragment extends Fragment {
    private RecyclerView recyclerView;
    private Handler handler = new Handler();
    private Fragment currentFragment = this;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.chatroom_fragment,container,false);
        this.recyclerView = view.findViewById(R.id.chatroom_recycler_view);
        recyclerView.hasFixedSize();
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        initializeRecyclerView();
        return view;
    }

    private void initializeRecyclerView(){
        class adapterRunnable implements Runnable{
            @Override
            public void run() {
                List<Message> chatroomList = new ArrayList<>();
                chatroomList.add(new Message("Chris","Yesterday 8:21", "text",
                        BitmapFactory.decodeResource(currentFragment.getResources(), R.drawable.puddle)));
                handler.post(()->{
                    ChatroomAdapter adapter = new ChatroomAdapter(chatroomList);
                    recyclerView.setAdapter(adapter);
                });
            }
        }
        Thread worker = new Thread(new adapterRunnable());
        worker.start();
    }
}
