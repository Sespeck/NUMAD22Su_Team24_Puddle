package com.cs5520.assignments.numad22su_team24_puddle.chatroom_fragments.adapters;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.cs5520.assignments.numad22su_team24_puddle.R;
import com.cs5520.assignments.numad22su_team24_puddle.Utils.FirebaseDB;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatroomAdapter extends RecyclerView.Adapter<ChatroomAdapter.ChatroomViewHolder> {
    private List<Message> messageList;
    private Context context;
    private DatabaseReference messageRef;
    private final int IMAGE = 1;
    private final int MESSAGE = 2;


    public ChatroomAdapter(List<Message> messageList, Context context, DatabaseReference messageRef){
        this.messageList = messageList;
        this.context = context;
        this.messageRef = messageRef;
    }

    public static class ChatroomViewHolder extends RecyclerView.ViewHolder {
        CircleImageView profilePicture;
        TextView timestamp;
        TextView messageBody;
        ImageView imageBody;
        TextView username;
        ConstraintLayout layout;
        public ChatroomViewHolder(@NonNull View itemView) {
            super(itemView);
            profilePicture = itemView.findViewById(R.id.chatroom_message_profile_pic);
            timestamp = itemView.findViewById(R.id.chatroom_message_datetime);
            messageBody = itemView.findViewById(R.id.chatroom_message_body);
            username = itemView.findViewById(R.id.chatroom_message_item_username);
            layout = itemView.findViewById(R.id.chatroom_full_message);
        }

        public ChatroomViewHolder(@NonNull View itemView, int alternateConstructor) {
            super(itemView);
            profilePicture = itemView.findViewById(R.id.chatroom_message_profile_pic);
            timestamp = itemView.findViewById(R.id.chatroom_message_datetime);
            imageBody = itemView.findViewById(R.id.chatroom_message_body);
            username = itemView.findViewById(R.id.chatroom_message_item_username);
            layout = itemView.findViewById(R.id.chatroom_full_image_message);
        }
    }

    @NonNull
    @Override
    public ChatroomAdapter.ChatroomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == IMAGE) {
            return new ChatroomAdapter.ChatroomViewHolder(LayoutInflater.from(parent.getContext()).
                    inflate(R.layout.chatroom_recycler_view_item_image_message, parent, false),1);
        } else{
            return new ChatroomAdapter.ChatroomViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.chatroom_recycler_view_message,parent,false));
        }
    }

    @Override
    public int getItemViewType(int position) {
        Message currentMessage = messageList.get(position);
        if (currentMessage.isImage){
            return IMAGE;
        } else{
            return MESSAGE;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull ChatroomAdapter.ChatroomViewHolder holder, int position) {
        Message message = messageList.get(position);
        Log.d("imageId",message.body);
        Glide.with(context).load(messageList.get(position).profilePicture).into(holder.profilePicture);
        if (message.isImage){
            Glide.with(context).load(messageList.get(position).body).into(holder.imageBody);
        }else {
            holder.messageBody.setText(messageList.get(position).body);
        }
        holder.username.setText(messageList.get(position).username);
        holder.timestamp.setText(messageList.get(position).timestamp);
        holder.layout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (holder.username.getText().equals(FirebaseDB.currentUser.getUsername())) {
                    PopupMenu menu = new PopupMenu(context, v);
                    MenuInflater inflater = menu.getMenuInflater();
                    inflater.inflate(R.menu.chatroom_delete_menu, menu.getMenu());
                    menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            switch(item.getItemId()){
                                case R.id.chatroom_delete_message:
                                    messageRef.child(message.getDbKey()).removeValue((error, ref) -> {

                                    });

                            }
                            messageList.remove(message);
                            notifyDataSetChanged();
                            return false;
                        }
                    });
                    menu.show();

                }
                return false;
            }
        });
    }

    public void addNewMessage(Message message){
        messageList.add(message);
        notifyItemInserted(getItemCount()-1);
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }
}
