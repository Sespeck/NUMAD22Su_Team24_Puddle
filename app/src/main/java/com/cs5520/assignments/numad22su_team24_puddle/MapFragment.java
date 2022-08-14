package com.cs5520.assignments.numad22su_team24_puddle;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.cs5520.assignments.numad22su_team24_puddle.Model.Puddle;
import com.cs5520.assignments.numad22su_team24_puddle.Model.User;
import com.cs5520.assignments.numad22su_team24_puddle.Utils.FirebaseDB;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.imageview.ShapeableImageView;
import com.cs5520.assignments.numad22su_team24_puddle.Model.PuddleMarker;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.List;

public class MapFragment extends Fragment{
    List<PuddleMarker> myPuddlesData;
    PuddleMarker puddle;
    int memberCount;
    String puddleName, puddleDescription, bannerURL, puddleId;
    TextView mapMemberCount, mapPuddleName;
    ImageView mapPuddleBg, mapOpenPuddleButton;

    public MapFragment(){}

    public static MapFragment newInstance(PuddleMarker puddleMarker, List<PuddleMarker> list) {
        MapFragment fragment = new MapFragment();
        fragment.myPuddlesData = list;
        fragment.puddle = puddleMarker;
        fragment.memberCount= puddleMarker.getMemberCount();
        fragment.puddleName= puddleMarker.getName();
        fragment.bannerURL = puddleMarker.getBackground();
        fragment.puddleDescription= puddleMarker.getDescription();
        fragment.puddleId = puddleMarker.getPuddleId();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.map_puddle, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mapMemberCount= view.findViewById(R.id.map_member_count);
        mapPuddleName= view.findViewById(R.id.map_puddle_name);
        mapPuddleBg= view.findViewById(R.id.map_puddle_bg);
        Glide.with(getContext()).load(bannerURL).centerCrop().into(mapPuddleBg);
        mapOpenPuddleButton= view.findViewById(R.id.map_open_puddle_button);

        mapMemberCount.setText(String.valueOf(memberCount));
        mapPuddleName.setText(puddleName);
        mapOpenPuddleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showJoinPuddleDialogue(getContext(), puddle);
//                Activity activity = getActivity();
//                View layoutView = View.inflate(activity, R.layout.puddle_modal, null);
//                AlertDialog dialog = new MaterialAlertDialogBuilder(activity).setTitle(puddleName).setView(layoutView).create();
//                TextView tv = layoutView.findViewById(R.id.puddle_modal_name_tv);
//                tv.setText(puddleDescription);
//                ShapeableImageView im = layoutView.findViewById(R.id.puddle_modal_item_image);
//                Glide.with(getContext()).load(bannerURL). into(im);
//                MaterialButton button = layoutView.findViewById(R.id.puddle_modal_join_btn);
//                button.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        dialog.dismiss();
//
//                        Intent intent = new Intent(getContext(), PuddleChatroomActivity.class);
//                        intent.putExtra("puddleID", puddleId);
//
//                        getContext().startActivity(intent);
//                    }
//                });
//                dialog.show();
            }
        });
    }

    public void showJoinPuddleDialogue(Context context, PuddleMarker puddle) {

        if (FirebaseDB.getLocalUser() == null) {
            FirebaseUser current_user = FirebaseDB.getCurrentUser();
            DatabaseReference userRef = FirebaseDB.getDataReference("Users").child(current_user.getUid());
            userRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    FirebaseDB.currentUser = snapshot.getValue(User.class);
                    if (FirebaseDB.getLocalUser() != null) {
                        showJoinPuddle(context, puddle);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        } else {
            showJoinPuddle(context, puddle);
        }

    }

    public void showJoinPuddle(Context context, PuddleMarker puddle) {


        if (FirebaseDB.getLocalUser().getMy_puddles() != null &&
                FirebaseDB.getLocalUser().getMy_puddles().containsValue(puddle.getPuddleId())) {
            Intent intent = new Intent(context, PuddleChatroomActivity.class);
            intent.putExtra("puddleID", puddle.getPuddleId());
            context.startActivity(intent);
        } else {
            View layoutView = View.inflate(context, R.layout.puddle_modal, null);
            AlertDialog dialog = new MaterialAlertDialogBuilder(context).setTitle(puddle.getName()).setView(layoutView).create();
            TextView tv = layoutView.findViewById(R.id.puddle_modal_name_tv);
            tv.setText(puddle.getDescription());
            ShapeableImageView image = layoutView.findViewById(R.id.puddle_modal_item_image);
            Glide.with(context).load(puddle.getBackground()).into(image);
            MaterialButton button = layoutView.findViewById(R.id.puddle_modal_join_btn);
            button.setOnClickListener(v -> {
                dialog.dismiss();
                Intent intent = new Intent(context, PuddleChatroomActivity.class);
                intent.putExtra("puddleID", puddle.getPuddleId());
                addPuddlesToList(puddle.getPuddleId(), puddle); // api call
                context.startActivity(intent);
            });
            dialog.show();
        }
    }

    public void addPuddlesToList(String pud_id, PuddleMarker pud) {
        boolean alreadyJoined = FirebaseDB.getLocalUser().getMy_puddles().containsKey(pud_id);

        new Thread(new Runnable() {
            @Override
            public void run() {
                // 1. Update the id in my_puddles for user
                String uid = FirebaseDB.getCurrentUser().getUid();
                DatabaseReference ref = FirebaseDB.getDataReference("Users").child(uid).child("my_puddles");
                ref.push().setValue(pud_id);

                // 2. Add Puddle count
                DatabaseReference pudRef = FirebaseDB.getDataReference("Puddles").child(pud_id).child("count");
                pudRef.setValue(alreadyJoined ? pud.getMemberCount() : pud.getMemberCount() + 1);

                // 3. Update the members child
                HashMap<String, String> userData = new HashMap<>();
                userData.put("username", FirebaseDB.getLocalUser().getUsername());
                userData.put("profile_url", FirebaseDB.getLocalUser().getProfile_icon());
                DatabaseReference memRef = FirebaseDB.getDataReference("Members").child(pud_id);
                memRef.push().setValue(userData);
            }
        }).start();
    }
}


