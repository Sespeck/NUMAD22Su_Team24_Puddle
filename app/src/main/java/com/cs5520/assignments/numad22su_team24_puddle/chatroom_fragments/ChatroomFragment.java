package com.cs5520.assignments.numad22su_team24_puddle.chatroom_fragments;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.cs5520.assignments.numad22su_team24_puddle.Model.User;
import com.cs5520.assignments.numad22su_team24_puddle.R;
import com.cs5520.assignments.numad22su_team24_puddle.Utils.FirebaseDB;
import com.cs5520.assignments.numad22su_team24_puddle.Utils.Util;
import com.cs5520.assignments.numad22su_team24_puddle.chatroom_fragments.adapters.ChatroomAdapter;
import com.cs5520.assignments.numad22su_team24_puddle.chatroom_fragments.adapters.Message;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.installations.Utils;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.net.URI;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class ChatroomFragment extends Fragment {
    private RecyclerView recyclerView;
    private ImageView sendButton;
    private EditText chatEditText;
    private Handler handler = new Handler();
    private Fragment currentFragment = this;
    private ChatroomAdapter adapter;
    private String puddleID;
    private StorageReference storeRef;
    private DatabaseReference imgRef;
    private DatabaseReference messageRef;
    private User currentUser;
    private Uri imageUri;
    private ShimmerFrameLayout shimmerFrameLayout;
    private String FRAGMENT_ID = "1";
    private EventsFragment.endShimmerEffectCallback callback = new EventsFragment.endShimmerEffectCallback(){
        @Override
        public void onLayoutInflated() {
            handler.postDelayed((Runnable) () -> {
                shimmerFrameLayout.stopShimmer();
                shimmerFrameLayout.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
            }, 800);
        }
    };
    ActivityResultLauncher<Intent> startActivityForResult = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == AppCompatActivity.RESULT_OK) {
                    Intent data = result.getData();
                    imageUri = data.getData();
                    class PushNewMsgToDB implements Runnable {
                        @Override
                        public void run() {
                            uploadToFirebase(imageUri);
                            HashMap<String, Object> newMessage = new HashMap<>();
                            newMessage.put("timestamp", Instant.now().toString());
                            FirebaseDB.fetchCurrentUserData();
                            newMessage.put("username", currentUser.getUsername());
                            Log.d("here", imageUri.toString());
                            newMessage.put("body", imageUri.toString());
                            newMessage.put("profile_url", currentUser.getProfile_icon());
                            newMessage.put("isMessage", true);
                            // Add a new message based off current time, the edittext body, the current user's
                            // Pfp and name

                            String id = messageRef.child(puddleID).push().getKey();
                            handler.post(() -> {
                                adapter.addNewMessage(new Message(currentUser.getUsername(), imageUri.toString(), Instant.now().toString(),
                                        currentUser.getProfile_icon(), id, true));
                                recyclerView.scrollToPosition(adapter.getItemCount() - 1);
                            });
                            messageRef.child(puddleID).push().setValue(newMessage);
                        }
                    }
                    if (imageUri != null) {
                        Thread worker = new Thread(new PushNewMsgToDB());
                        worker.start();
                    }
                }
            });

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
        this.shimmerFrameLayout = view.findViewById(R.id.chatroom_shimmer_layout);
        recyclerView.hasFixedSize();
        uploadImageToFb();
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        initializeRecyclerView();
        currentUser = FirebaseDB.currentUser;
        createNewMessageListener();
        if (!Util.renderShimmerEffect.containsKey(Util.generateShimmerEffectID(FirebaseDB.currentUser.getUsername(),puddleID,FRAGMENT_ID))) {
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
            Util.renderShimmerEffect.put(Util.generateShimmerEffectID(FirebaseDB.currentUser.getUsername(),puddleID,FRAGMENT_ID), true);
        } else{
            shimmerFrameLayout.stopShimmer();
            shimmerFrameLayout.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
        view.findViewById(R.id.add_image_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent gallery = new Intent();
                gallery.setAction(Intent.ACTION_GET_CONTENT);
                gallery.setType("image/*");
                startActivityForResult.launch(gallery);
            }
        });
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
                        newMessage.put("profile_url",currentUser.getProfile_icon());
                        newMessage.put("isMessage",false);
                        // Add a new message based off current time, the edittext body, the current user's
                        // Pfp and name

                        String id = messageRef.child(puddleID).push().getKey();
                        handler.post(()-> {
                            adapter.addNewMessage(new Message(currentUser.getUsername() ,textResult,Instant.now().toString(),
                                    currentUser.getProfile_icon(), id, false));
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
                            String body = snap.child("body").getValue(String.class);
                            String profile_url = snap.child("profile_url").getValue(String.class);
                            String timestamp = Util.convertTocurrentDateTime(snap.child("timestamp").getValue(String.class));
                            Boolean isMessage = snap.child("isMessage").getValue(Boolean.class);
                            chatroomList.add(new Message(username, body, timestamp, profile_url,snap.getKey(),isMessage));
                        }
                        handler.post(()->{
                            adapter = new ChatroomAdapter(chatroomList,getContext(), messageRef.child(puddleID));
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

    public void uploadImageToFb() {
        imgRef = FirebaseDB.getDataReference("images");
        imgRef.setValue("url");

        storeRef = FirebaseDB.storageRef;
    }

    public void uploadToFirebase(Uri uri) {
        StorageReference ref = storeRef.child(System.currentTimeMillis() + "." +
                getFileExtension(uri));
        ref.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                Log.d("here",uri.toString());
                                imageUri = uri;
                            }
                        }).start();
                    }

                });
            }
        });
    }

    public String getFileExtension(Uri muri) {
        ContentResolver cr = getActivity().getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cr.getType(muri));
    }
}
