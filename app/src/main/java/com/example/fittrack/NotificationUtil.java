package com.example.fittrack;

import static androidx.core.content.ContextCompat.getSystemService;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

public class NotificationUtil {
    private static final String SAVED_ACTIVITY_ID = "save_activity_channel";
    private static final String PROGRESS_TRACKING_ID = "progress_tracking_channel";
    private static final String WORKOUT_REMINDER_ID = "workout_reminder_channel";
    public static void createSavedWorkoutNotificationChannel(Context context) {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence channelName = "Saved Activity Notifications";
            String description = "Notifications for saving fitness activities";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(SAVED_ACTIVITY_ID, channelName, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    public static void showSavedActivityNotification(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (ContextCompat.checkSelfPermission(context, "android.permission.POST_NOTIFICATIONS")
                    != PackageManager.PERMISSION_GRANTED) {
                Log.w("Notification Permission Not Granted", "POST_NOTIFICATIONS permission was not granted");
                return;
            }
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, SAVED_ACTIVITY_ID)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle("Fitness Activity Saved")
                .setContentText("Your fitness activity has been successfully saved!")
                .setPriority(NotificationCompat.PRIORITY_HIGH);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(1, builder.build());
    }

    public static void createProgressNotificationChannel(Context context) {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence channelName = "Progress Tracking Notifications";
            String description = "Notifications for Progress Tracking";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(PROGRESS_TRACKING_ID, channelName, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    public static void showProgressTrackingNotification(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (ContextCompat.checkSelfPermission(context, "android.permission.POST_NOTIFICATIONS")
                    != PackageManager.PERMISSION_GRANTED) {
                Log.w("Notification Permission Not Granted", "POST_NOTIFICATIONS permission was not granted");
                return;
            }
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, PROGRESS_TRACKING_ID)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle("Progress Tracking")
                .setContentText("Progress Update Alert")
                .setPriority(NotificationCompat.PRIORITY_HIGH);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(1, builder.build());
    }

    public static void createWorkoutReminderNotificationChannel(Context context) {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence channelName = "Workout Reminder Notifications";
            String description = "Notifications for Workout Reminders";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(WORKOUT_REMINDER_ID, channelName, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    public static void showWorkoutReminderNotification(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (ContextCompat.checkSelfPermission(context, "android.permission.POST_NOTIFICATIONS")
                    != PackageManager.PERMISSION_GRANTED) {
                Log.w("Notification Permission Not Granted", "POST_NOTIFICATIONS permission was not granted");
                return;
            }
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, WORKOUT_REMINDER_ID)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle("Workout Reminder")
                .setContentText("Workout Reminder Content")
                .setPriority(NotificationCompat.PRIORITY_HIGH);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(1, builder.build());
    }
}
