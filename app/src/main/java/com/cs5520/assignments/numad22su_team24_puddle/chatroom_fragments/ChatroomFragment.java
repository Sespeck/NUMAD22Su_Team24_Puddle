package com.cs5520.assignments.numad22su_team24_puddle.chatroom_fragments;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cs5520.assignments.numad22su_team24_puddle.Model.User;
import com.cs5520.assignments.numad22su_team24_puddle.R;
import com.cs5520.assignments.numad22su_team24_puddle.Utils.FirebaseDB;
import com.cs5520.assignments.numad22su_team24_puddle.Utils.Util;
import com.cs5520.assignments.numad22su_team24_puddle.chatroom_fragments.adapters.ChatroomAdapter;
import com.cs5520.assignments.numad22su_team24_puddle.chatroom_fragments.adapters.Message;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.installations.Utils;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ChatroomFragment extends Fragment {
    private RecyclerView recyclerView;
    private ImageView sendButton;
    private EditText chatEditText;
    private Handler handler = new Handler();
    private Fragment currentFragment = this;
    private ChatroomAdapter adapter;
    private String puddleID;
    private DatabaseReference messageRef;
    private User currentUser;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.chatroom_fragment,container,false);
        puddleID = getArguments().getString("puddleID");
        this.recyclerView = view.findViewById(R.id.chatroom_recycler_view);
        this.sendButton = view.findViewById(R.id.chat_room_send_button);
        this.chatEditText = view.findViewById(R.id.chat_room_edit_text);
        this.messageRef =  FirebaseDB.getDataReference("Messages");
        recyclerView.hasFixedSize();
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        initializeRecyclerView();
        currentUser = FirebaseDB.currentUser;
        createNewMessageListener();
        return view;
    }

    private void createNewMessageListener(){
        sendButton.setOnClickListener(v -> {
            if (chatEditText.getText() != null){
                class PushNewMsgToDB implements Runnable{
                    @Override
                    public void run() {
                        String textResult = chatEditText.getText().toString();
                        HashMap<String, Object> newMessage = new HashMap<>();
                        newMessage.put("timestamp", Instant.now().toString());
                        FirebaseDB.fetchCurrentUserData();
                        newMessage.put("username", currentUser.getUsername());
                        newMessage.put("body",textResult);
                        newMessage.put("profile_url","https://firebasestorage.googleapis.com/v0/b/android-chat-85561.appspot.com/o/1659737464403.jpg?alt=media&token=65a5f276-7954-4e07-a236-1b50732b0e6e");
                        // Add a new message based off current time, the edittext body, the current user's
                        // Pfp and name
                        handler.post(()-> {
                            adapter.addNewMessage(new Message(currentUser.getUsername() ,textResult,Instant.now().toString(),
                                    FirebaseDB.currentUser.getProfile_icon()));
                            recyclerView.scrollToPosition(adapter.getItemCount()-1);
                            chatEditText.getText().clear();
                        });
                        messageRef.child(puddleID).push().setValue(newMessage);
                    }
                }
                Thread worker = new Thread(new PushNewMsgToDB());
                worker.start();
            }
        });
    }

    private void initializeRecyclerView(){
        class adapterRunnable implements Runnable{
            @Override
            public void run() {
               messageRef.child(puddleID).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        List<Message> chatroomList = new ArrayList<>();
                        for (DataSnapshot snap: snapshot.getChildren()){
                            String username = snap.child("username").getValue(String.class);
                            String profile_url = FirebaseDB.allUserData.get(username).getProfile_icon();
                            String body = snap.child("body").getValue(String.class);
//                            String profile_url = snap.child("profile_url").getValue(String.class);
                            String timestamp = Util.convertTocurrentDateTime(snap.child("timestamp").getValue(String.class));
                            chatroomList.add(new Message(username, body, timestamp, profile_url));
                        }
                        handler.post(()->{
                            adapter = new ChatroomAdapter(chatroomList,getContext());
                            recyclerView.setAdapter(adapter);
                            recyclerView.scrollToPosition(adapter.getItemCount()-1);
                        });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        }
        Thread worker = new Thread(new adapterRunnable());
        worker.start();
    }
}
