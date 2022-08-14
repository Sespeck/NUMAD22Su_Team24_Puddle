package com.cs5520.assignments.numad22su_team24_puddle.chatroom_fragments;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.cs5520.assignments.numad22su_team24_puddle.R;
import com.cs5520.assignments.numad22su_team24_puddle.Utils.FirebaseDB;
import com.cs5520.assignments.numad22su_team24_puddle.Utils.Util;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import de.hdodenhof.circleimageview.CircleImageView;

public class AboutFragment extends Fragment {
    private String puddleID;
    private TextView bioTextView;
    private CircleImageView bannerIcon;
    private TextView tagsTextView;
    private TextView puddleName;
    private Handler handler = new Handler();
    private ShimmerFrameLayout shimmerFrameLayout;
    private final String FRAGMENT_ID = "2";
    private CardView cardView;
    private String profilePicUri;
    private EventsFragment.endShimmerEffectCallback callback = new EventsFragment.endShimmerEffectCallback(){
        @Override
        public void onLayoutInflated() {
            handler.postDelayed((Runnable) () -> {
                shimmerFrameLayout.stopShimmer();
                shimmerFrameLayout.setVisibility(View.GONE);
                cardView.setVisibility(View.VISIBLE);
            }, 800);
        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.about_fragment, container, false);
        puddleID = getArguments().getString("puddleID");
        this.bioTextView = view.findViewById(R.id.about_tab_bio_text_view);
        this.bannerIcon = view.findViewById(R.id.about_tab_banner_pic);
        this.tagsTextView = view.findViewById(R.id.about_tab_tags_text_view);
        this.puddleName = view.findViewById(R.id.about_puddle_name);
        this.shimmerFrameLayout = view.findViewById(R.id.about_shimmer_frame_layout);
        this.cardView = view.findViewById(R.id.about_card_view);
        if (savedInstanceState != null) {
            tagsTextView.setText(savedInstanceState.getString("tags"));
            bioTextView.setText(savedInstanceState.getString("bio"));
            puddleName.setText(savedInstanceState.getString("name"));
            profilePicUri = savedInstanceState.getString("profile_pic_uri");
            Glide.with(requireContext()).load(profilePicUri).into(bannerIcon);
        }
        if (FirebaseDB.currentUser != null && !Util.renderShimmerEffect.containsKey(Util.generateShimmerEffectID(FirebaseDB.currentUser.getUsername(),puddleID,FRAGMENT_ID))){
            callback.onLayoutInflated();
            Util.renderShimmerEffect.put(Util.generateShimmerEffectID(FirebaseDB.currentUser.getUsername(),puddleID,FRAGMENT_ID),true);
        } else{
            shimmerFrameLayout.stopShimmer();
            shimmerFrameLayout.setVisibility(View.GONE);
            cardView.setVisibility(View.VISIBLE);
        }
        FirebaseDB.getDataReference("Puddles").child(puddleID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                class getUri implements Runnable {
                    @Override
                    public void run() {
                        String bio = (String) snapshot.child("bio").getValue(String.class);
                        String name = (String) snapshot.child("name").getValue(String.class);
                        String profilePic = (String) snapshot.child("bannerUrl").getValue(String.class);
                        String tags = (String) snapshot.child("category").getValue(String.class);

                        handler.post(() -> {
                            if (tags != null) tagsTextView.setText(tags);
                            if (bio != null) bioTextView.setText(bio);
                            if (name != null) puddleName.setText(name);
                            if (bannerIcon.getBackground() == null && isAdded()) {
                                profilePicUri = profilePic;
                                Glide.with(requireContext()).load(profilePic).into(bannerIcon);
                            }
                        });
                    }
                }
                Thread getUriThread = new Thread(new getUri());
                getUriThread.start();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        return view;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        if (tagsTextView.getText() != null) outState.putString("tags", tagsTextView.getText().toString());
        if (bioTextView.getText() != null) outState.putString("bio", bioTextView.getText().toString());
        if (puddleName.getText() != null) outState.putString("name", bioTextView.getText().toString());
        if (profilePicUri != null) outState.putString("profile_pic_uri", profilePicUri);
        super.onSaveInstanceState(outState);
    }
}
