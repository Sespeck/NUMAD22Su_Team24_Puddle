package com.cs5520.assignments.numad22su_team24_puddle;

import android.util.Log;

import androidx.annotation.NonNull;

import com.cs5520.assignments.numad22su_team24_puddle.Utils.FirebaseDB;
import com.cs5520.assignments.numad22su_team24_puddle.Utils.Util;
import com.cs5520.assignments.numad22su_team24_puddle.chatroom_fragments.MessageNotification;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class NotificationListener {
    private ValueEventListener valueEventListener;
    private DatabaseReference userRef;
    private ArrayList<ValueEventListener> valueEventListeners = new ArrayList<>();
    private ArrayList<DatabaseReference> references = new ArrayList<>();
    private boolean isRegistered = false;


    public Boolean isRegistered(){
        return isRegistered;
    }
    public void registerListener(MessageNotification notification) {
            valueEventListener = new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.getChildrenCount() != 0) {
                        for (DataSnapshot snap :
                                snapshot.getChildren()) {
                            String puddleID = snap.getValue(String.class);
                            ValueEventListener listener = new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    for (DataSnapshot snap :
                                            snapshot.getChildren()) {
                                        String senderUsername = snap.child("username").getValue(String.class);
                                        String body = snap.child("body").getValue(String.class);
                                        Boolean isImage = snap.child("isMessage").getValue(Boolean.class);
                                        Boolean isNew = snap.child("isNew").getValue(Boolean.class);
                                        if (isNew != null && isNew && !senderUsername.equals(FirebaseDB.getLocalUser().getUsername())
                                                && Util.isPuddleListForeground) {
                                            Log.d("here", "notificationlistener");
                                            snap.getRef().child("isNew").setValue(false);
                                            FirebaseDB.getDataReference("Puddles").child(puddleID).child("name").
                                                    addValueEventListener(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                            String name = snapshot.getValue(String.class);
                                                            if (isImage != null && isImage) {
                                                                notification.createNotification(senderUsername, senderUsername +
                                                                        " sent a new image!", puddleID, name);
                                                            } else {
                                                                notification.createNotification(senderUsername, body, puddleID, name);
                                                            }
                                                        }


                                                        @Override
                                                        public void onCancelled(@NonNull DatabaseError error) {

                                                        }
                                                    });
                                        }
                                        snap.getRef().child("isNew").setValue(false);
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            };

                            FirebaseDB.getDataReference("Messages").child(puddleID).orderByKey().limitToLast(1).addValueEventListener(listener);
                            valueEventListeners.add(listener);
                            references.add(FirebaseDB.getDataReference("Messages").child(puddleID));


                        }
                    }
                }

                public void onCancelled(@NonNull DatabaseError error) {

                }
            };
            userRef = FirebaseDB.getDataReference("Users").child(FirebaseDB.getLocalUser().getId()).child("my_puddles");

            userRef.addValueEventListener(valueEventListener);
            isRegistered = true;
        }


        public void setState(Boolean state){
            isRegistered = state;
        }

    public void unregisterListener() {
        for (int i=0; i<references.size(); i++){
            references.get(i).removeEventListener(valueEventListeners.get(i));
        }
        userRef.removeEventListener(valueEventListener);
        isRegistered = false;
    }
}
