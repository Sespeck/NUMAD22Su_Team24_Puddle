package com.cs5520.assignments.numad22su_team24_puddle.chatroom_fragments.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.cs5520.assignments.numad22su_team24_puddle.R;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MembersAdapter extends RecyclerView.Adapter<MembersAdapter.MembersViewHolder> {
    private List<Member> memberList;
    private Context context;

    public MembersAdapter(List<Member> memberList, Context context){
        this.memberList = memberList;
        this.context = context;
    }

    public static class MembersViewHolder extends RecyclerView.ViewHolder {
        TextView  username;
        CircleImageView profileIcon;
        public MembersViewHolder(@NonNull View itemView) {
            super(itemView);
            username = itemView.findViewById(R.id.members_username);
            profileIcon = itemView.findViewById(R.id.members_profile_pic);
        }
    }
    @NonNull
    @Override
    public MembersAdapter.MembersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MembersViewHolder(LayoutInflater.from(parent.getContext()).
                inflate(R.layout.members_recycler_view_item,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull MembersAdapter.MembersViewHolder holder, int position) {
        Glide.with(context).load(memberList.get(position).profileUrl).into(holder.profileIcon);
        holder.username.setText(memberList.get(position).username);
    }

    @Override
    public int getItemCount() {
        return memberList.size();
    }
}
