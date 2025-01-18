package com.example.fittrack;

import static android.content.Context.ALARM_SERVICE;
import static androidx.core.content.ContextCompat.getSystemService;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import java.util.Calendar;

public class NotificationScheduler {
    public static void scheduleNotification(Context context, long sendTime, int requestCode) {
        Intent intent = new Intent(context, WorkoutNotificationReceiver.class);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, sendTime, pendingIntent);
    }

    public static void sendNotification(Context context, long activityTime) {
        java.util.Calendar calendar = java.util.Calendar.getInstance();
        calendar.setTimeInMillis(activityTime);

        Calendar startTime = Calendar.getInstance();
        startTime.setTimeInMillis(activityTime);
        startTime.set(Calendar.HOUR_OF_DAY, 06);
        startTime.set(Calendar.MINUTE, 0);
        startTime.set(Calendar.SECOND, 0);

        Calendar endTime = Calendar.getInstance();
        endTime.setTimeInMillis(activityTime);
        endTime.set(Calendar.HOUR_OF_DAY, 22);
        endTime.set(Calendar.MINUTE, 0);
        endTime.set(Calendar.SECOND, 0);


        calendar.add(Calendar.HOUR_OF_DAY, -2);
        long twoHourWarning = calendar.getTimeInMillis();
        if(twoHourWarning > startTime.getTimeInMillis() && twoHourWarning < endTime.getTimeInMillis()) {
            scheduleNotification(context, twoHourWarning, 1);
        }

        calendar.add(Calendar.HOUR_OF_DAY, 1);
        long oneHourWarning = calendar.getTimeInMillis();
        if(oneHourWarning > startTime.getTimeInMillis() && oneHourWarning < endTime.getTimeInMillis()) {
            scheduleNotification(context, oneHourWarning, 2);
        }

        calendar.add(Calendar.HOUR_OF_DAY, 1);
        long nowWarning = calendar.getTimeInMillis();
        if(nowWarning > endTime.getTimeInMillis() && nowWarning < endTime.getTimeInMillis()) {
            scheduleNotification(context, nowWarning, 3);
        }

        long recurringTime = activityTime;
        int requestCode = 4;
        while (true) {
            recurringTime += 2 * 60 * 60 * 1000;
            if (recurringTime <  startTime.getTimeInMillis() && recurringTime > endTime.getTimeInMillis()) {
                break;
            }
            scheduleNotification(context, recurringTime, requestCode);
            requestCode++;
        }
    }
}
