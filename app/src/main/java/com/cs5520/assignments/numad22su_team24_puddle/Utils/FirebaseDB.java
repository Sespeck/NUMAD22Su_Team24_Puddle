package com.cs5520.assignments.numad22su_team24_puddle.Utils;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.cs5520.assignments.numad22su_team24_puddle.Model.User;
import com.cs5520.assignments.numad22su_team24_puddle.ProfileActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;

public class FirebaseDB {

    public static StorageReference storageRef = FirebaseStorage.getInstance().getReference();
    public static User currentUser;
    public static HashMap<String, User> allUserData;


    public static FirebaseAuth getInstanceFirebaseAuth() {
        return FirebaseAuth.getInstance();
    }

    public static FirebaseUser getCurrentUser() {
        return FirebaseAuth.getInstance().getCurrentUser();
    }

    public static DatabaseReference getDataReference(String path) {
        return FirebaseDatabase.getInstance().getReference(path);
    }

    public static void fetchAllUsers(){
        allUserData = new HashMap<>();
        new Thread(new Runnable() {
            @Override
            public void run() {
                DatabaseReference ref = FirebaseDB.getDataReference("Users");
                ref.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for(DataSnapshot snap: snapshot.getChildren()){
                            User user = snap.getValue(User.class);
                            if(user != null){
                                allUserData.put(user.getUsername(), user);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        }).start();
    }

    public static void registerUser(String email, String password, String username, Context ct) {
        getInstanceFirebaseAuth().createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    FirebaseUser firebaseUser = getCurrentUser();
                    String userid = firebaseUser.getUid();
                    DatabaseReference ref = getDataReference("Users").child(userid);

                    User user = new User(userid, username, password, email);
                    HashMap<String, Object> hashMap = user.getUserMap();

                    ref.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                FirebaseDB.currentUser = user;
                                Intent intent = new Intent(ct, ProfileActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                intent.putExtra("new_user","dont_animate_shimmer");
                                ct.startActivity(intent);
                            } else {
                                Toast.makeText(ct, "Error registering user!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } else {
                    Log.d("exception", task.getException().getMessage());
                    Toast.makeText(ct, task.getException().getMessage(),
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public static void logout() {
        getInstanceFirebaseAuth().signOut();
    }

    public static void fetchCurrentUserData() {
        FirebaseUser current_user = FirebaseDB.getCurrentUser();

        DatabaseReference userRef = FirebaseDB.getDataReference("Users").child(current_user.getUid());
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                for (DataSnapshot snap : snapshot) {
//                    userDetails.put(snap.getKey(), snap.getValue(String.class));
//                }
                currentUser = snapshot.getValue(User.class);
                Log.d("currentUser", currentUser.toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
