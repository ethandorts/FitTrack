package com.example.fittrack;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class WorkoutNotificationReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        NotificationUtil.createWorkoutReminderNotificationChannel(context);
        NotificationUtil.showWorkoutReminderNotification(context);
    }
}
