package com.cs5520.assignments.numad22su_team24_puddle.chatroom_fragments;

import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.cs5520.assignments.numad22su_team24_puddle.PuddleChatroomActivity;
import com.cs5520.assignments.numad22su_team24_puddle.R;


public class MessageNotification {
    private static final String CHANNEL_1_ID = "CHANNEL1";
    private static final int NOTIFICATION_ID = 1;
    private Activity activity;

    public MessageNotification(Activity activity){
        this.activity = activity;
        registerNotificationChannel();
    }


    public void createNotification(String username, String body, int id){
        Intent notifyIntent = new Intent(activity, PuddleChatroomActivity.class);
        notifyIntent.putExtra("username", username);
        notifyIntent.putExtra("body", body);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(activity);
        stackBuilder.addNextIntentWithParentStack(notifyIntent);
        PendingIntent notifyPendingIntent =
                stackBuilder.getPendingIntent(0,
                        PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        NotificationCompat.Builder builder = new
                NotificationCompat.Builder(activity, CHANNEL_1_ID)
                .setContentTitle(username).setAutoCancel(true).
                setContentText(body).setContentIntent(notifyPendingIntent).setSmallIcon(R.drawable.puddle).
                setPriority(NotificationCompat.PRIORITY_DEFAULT);
        NotificationManagerCompat notificationManager =
                NotificationManagerCompat.from(activity);
        notificationManager.notify(NOTIFICATION_ID,builder.build());
    }

    /**
     * Registers a notification channel. Necessary so phones running newer APIs
     * receive the notifications.
     */
    private void registerNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(CHANNEL_1_ID, "Channel 1", importance);
            channel.setDescription("Notification channel 1");
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = activity.
                    getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}
