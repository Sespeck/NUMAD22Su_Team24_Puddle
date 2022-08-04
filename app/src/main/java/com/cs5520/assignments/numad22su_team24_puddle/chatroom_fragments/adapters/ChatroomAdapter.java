package com.cs5520.assignments.numad22su_team24_puddle.chatroom_fragments.adapters;

import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.cs5520.assignments.numad22su_team24_puddle.R;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatroomAdapter extends RecyclerView.Adapter<ChatroomAdapter.ChatroomViewHolder> {
    private List<Message> messageList;


    public ChatroomAdapter(List<Message> messageList){
        this.messageList = messageList;
    }

    public static class ChatroomViewHolder extends RecyclerView.ViewHolder {
        CircleImageView profilePicture;
        TextView timestamp;
        TextView messageBody;
        TextView username;
        public ChatroomViewHolder(@NonNull View itemView) {
            super(itemView);
            profilePicture = itemView.findViewById(R.id.chatroom_message_profile_pic);
            timestamp = itemView.findViewById(R.id.chatroom_message_datetime);
            messageBody = itemView.findViewById(R.id.chatroom_message_body);
            username = itemView.findViewById(R.id.chatroom_message_item_username);
        }
    }

    @NonNull
    @Override
    public ChatroomAdapter.ChatroomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ChatroomAdapter.ChatroomViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.chatroom_recycler_view_message,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull ChatroomAdapter.ChatroomViewHolder holder, int position) {
//        holder.profilePicture.setImageBitmap(messageList.get(position).getProfilePicture());
        holder.messageBody.setText(messageList.get(position).text);
    }

    public void addNewMessage(Message message){
        messageList.add(message);
        notifyItemChanged(getItemCount()-1);
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }
}
