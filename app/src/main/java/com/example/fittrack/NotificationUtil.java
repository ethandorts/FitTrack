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
    private static final String LOG_FOOD_REMINDER_ID = "log_food_remainder_channel";
    private static final String CALORIE_GOAL_SUCCESSFUL_ID = "calorie_goal_successful_channel";
    private static final String DISTANCE_GOAL_SUCCESSFUL_ID ="distance_goal_successful_channel";
    private static final String TIME_GOAL_SUCCESSFUL_ID ="time_goal_successful_channel";

    public static void createNotificationChannel(Context context, String channelID, String name, String channelDescription, int importanceLevel) {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence channelName = name;
            String description = channelDescription;
            int importance = importanceLevel;
            NotificationChannel channel = new NotificationChannel(channelID, channelName, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    public static void showNotification(Context context, String channelID, String contentTitle, String contentMessage, int priorityLevel) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (ContextCompat.checkSelfPermission(context, "android.permission.POST_NOTIFICATIONS")
                    != PackageManager.PERMISSION_GRANTED) {
                Log.w("Notification Permission Not Granted", "POST_NOTIFICATIONS permission was not granted");
                return;
            }
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channelID)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle(contentTitle)
                .setContentText(contentMessage)
                .setPriority(priorityLevel);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(1, builder.build());
    }


    public static void createSavedWorkoutNotificationChannel(Context context) {
        createNotificationChannel(context,
                SAVED_ACTIVITY_ID,
                "Saved Activity Notifications",
                "Notifications for saving fitness activities",
                NotificationManager.IMPORTANCE_HIGH);
    }

    public static void showSavedActivityNotification(Context context) {
        showNotification(context,SAVED_ACTIVITY_ID, "Fitness Activity Saved","Your fitness activity has been successfully saved!", NotificationCompat.PRIORITY_HIGH);
    }

    public static void createProgressNotificationChannel(Context context) {
        createNotificationChannel(context,
                PROGRESS_TRACKING_ID,
                "Progress Tracking Notifications",
                "Notifications for Progress Tracking",
                NotificationManager.IMPORTANCE_HIGH);
    }

    public static void showProgressTrackingNotification(Context context) {
        showNotification(context,PROGRESS_TRACKING_ID, "Progress Tracking","Progress Update Alert", NotificationCompat.PRIORITY_HIGH);
    }

    public static void createWorkoutReminderNotificationChannel(Context context) {
        createNotificationChannel(context, WORKOUT_REMINDER_ID,
                "Workout Reminder Notifications",
                "Notifications for Workout Reminders",
                NotificationManager.IMPORTANCE_HIGH);
    }

    public static void showWorkoutReminderNotification(Context context) {
        showNotification(context, WORKOUT_REMINDER_ID, "Workout Reminder","Workout Reminder Content", NotificationCompat.PRIORITY_HIGH);
    }

    public static void createLogFoodNotificationChannel(Context context) {
        createNotificationChannel(context, LOG_FOOD_REMINDER_ID,"Log Food Reminder","You haven't logged any food for your Breakfast", NotificationManager.IMPORTANCE_HIGH);
    }

    public static void showLogFoodNotification(Context context, String mealType) {
        showNotification(context,LOG_FOOD_REMINDER_ID, "Log Food for " + mealType+ " Reminder", "You haven't logged any food for your " + mealType, NotificationCompat.PRIORITY_HIGH);
    }

    public static void createCalorieGoalSuccessfulNotificationChannel(Context context) {
        createNotificationChannel(context, CALORIE_GOAL_SUCCESSFUL_ID, "Calorie Goal Successfully Completed", "Calorie Goal Successfully Completed", NotificationManager.IMPORTANCE_HIGH);
    }

    public static void showCalorieGoalSuccessfulNotification(Context context) {
        showNotification(context, CALORIE_GOAL_SUCCESSFUL_ID, "Calorie Goal Successfully Completed", "Calorie Goal Successfully Completed", NotificationCompat.PRIORITY_HIGH);
    }

    public static void createDistanceGoalSuccessfulNotificationChannel(Context context) {
        createNotificationChannel(context, DISTANCE_GOAL_SUCCESSFUL_ID, "Distance Goal Successfully Completed", "Distance Goal Successfully Completed", NotificationManager.IMPORTANCE_HIGH);
    }

    public static void showDistanceGoalSuccessfulNotification(Context context) {
        showNotification(context, DISTANCE_GOAL_SUCCESSFUL_ID, "Distance Goal Successfully Completed", "Distance Goal Successfully Completed", NotificationCompat.PRIORITY_HIGH);
    }

    public static void createTimeGoalSuccessfulNotificationChannel(Context context) {
        createNotificationChannel(context, TIME_GOAL_SUCCESSFUL_ID, "Time Goal Successfully Completed", "Time Goal Successfully Completed", NotificationManager.IMPORTANCE_HIGH);
    }

    public static void showTimeGoalSuccessfulNotification(Context context) {
        showNotification(context, TIME_GOAL_SUCCESSFUL_ID, "Time Goal Successfully Completed", "Time Goal Successfully Completed", NotificationCompat.PRIORITY_HIGH);
    }
}
