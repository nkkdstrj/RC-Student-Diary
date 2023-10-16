package com.rcdiarycollegedept.rcstudentdiary;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class PublicReminderReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        // Retrieve the event name from the intent
        String eventName = intent.getStringExtra("event_name");

        // Call the sendPublicReminderNotification method to display the notification
        sendPublicReminderNotification(context, eventName);
    }

    private void sendPublicReminderNotification(Context context, String eventName) {
        // Create a unique notification ID
        int notificationId = (int) System.currentTimeMillis();

        // Create a notification channel (for Android 8.0 and higher)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("public_reminder", "Public Reminder Channel", NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }

        // Create a notification using NotificationCompat.Builder
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "public_reminder")
                .setSmallIcon(R.drawable.baseline_notifications_24) // Set your notification icon
                .setContentTitle("Public Reminder")
                .setContentText(eventName)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        // Build the notification
        Notification notification = builder.build();

        // Get the NotificationManagerCompat and notify with the unique ID
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(notificationId, notification);
    }
}

