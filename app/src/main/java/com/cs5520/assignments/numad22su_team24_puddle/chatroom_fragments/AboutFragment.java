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
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.cs5520.assignments.numad22su_team24_puddle.R;
import com.cs5520.assignments.numad22su_team24_puddle.Utils.FirebaseDB;
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

    public AboutFragment(String puddleID){
        this.puddleID = puddleID;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.about_fragment,container,false);
        this.bioTextView = view.findViewById(R.id.about_tab_bio_text_view);
        this.bannerIcon = view.findViewById(R.id.about_tab_banner_pic);
        this.tagsTextView = view.findViewById(R.id.about_tab_tags_text_view);
        this.puddleName = view.findViewById(R.id.about_puddle_name);
        FirebaseDB.getDataReference("Puddles").child(puddleID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                class getUri implements Runnable {
                    @Override
                    public void run() {
                        String bio = (String) snapshot.child("bio").getValue(String.class);
                        String name = (String) snapshot.child("name").getValue(String.class);
                        String profilePic = (String) snapshot.child("bannerUrl").getValue(String.class);
                        handler.post(() -> {
                            if (bio != null) bioTextView.setText(bio);
                            if (name != null) puddleName.setText(name);
//                            Picasso.get().load(profilePic).into(bannerIcon);
                            Glide.with(getContext()).load(profilePic).into(bannerIcon);
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
}
