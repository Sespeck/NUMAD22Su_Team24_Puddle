package com.cs5520.assignments.numad22su_team24_puddle.chatroom_fragments;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
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
import com.cs5520.assignments.numad22su_team24_puddle.chatroom_fragments.adapters.Member;
import com.cs5520.assignments.numad22su_team24_puddle.chatroom_fragments.adapters.MembersAdapter;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MembersFragment extends Fragment {
    private DatabaseReference membersRef;
    private String puddleID;
    private Handler handler = new Handler();
    private RecyclerView recyclerView;
    private MembersAdapter membersAdapter;
    private final String FRAGMENT_ID = "3";
    private Context context;
    private ShimmerFrameLayout shimmerFrameLayout;
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

    public interface endShimmerEffectCallback {
        void onLayoutInflated();
    }



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.members_fragment,container,false);
        puddleID = getArguments().getString("puddleID");
        membersRef = FirebaseDB.getDataReference("Members").child(puddleID);
        recyclerView = view.findViewById(R.id.members_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.hasFixedSize();
        shimmerFrameLayout = view.findViewById(R.id.members_shimmer_frame_layout);
        if (FirebaseDB.currentUser != null && !Util.renderShimmerEffect.containsKey(Util.generateShimmerEffectID(FirebaseDB.currentUser.getUsername(),puddleID,FRAGMENT_ID)) && getArguments().getString("new_chatroom") == null) {
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
        context = getContext();
        initializeRecyclerView();
        return view;
    }

    private void initializeRecyclerView(){
        class getMembersRunnable implements Runnable{
            @Override
            public void run() {
                membersRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        List<Member> memberList = new ArrayList<>();
                        for (DataSnapshot snap: snapshot.getChildren()) {
                            String username = snap.child("username").getValue(String.class);
                            String profile_url = FirebaseDB.allUserData.get(username).getProfile_icon(); //snap.child("profile_url").getValue(String.class);
                            memberList.add(new Member(username,profile_url));
                        }
                        handler.post(() -> {
                            membersAdapter = new MembersAdapter(memberList,context);
                            recyclerView.setAdapter(membersAdapter);
                        });
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
            }
        }
        Thread worker = new Thread(new getMembersRunnable());
        worker.start();
    }
}
