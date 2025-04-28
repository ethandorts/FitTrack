package com.example.fittrack;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.WorkManager;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;

public class GoalsReminder extends Worker {
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private GoalsUtil goalsUtil = new GoalsUtil(db);
    private String UserID = mAuth.getUid();

    public GoalsReminder(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        Log.d("Goals Reminder Started", "Goals Reminder Loop Start");
        NotificationUtil.createGoalReminderNotificationChannel(getApplicationContext());
        goalsUtil.retrieveUserGoals(UserID, new GoalsUtil.GoalsCallback() {
            @Override
            public void onCallback(List<String> goals) {
                for(String goal : goals) {
                    NotificationUtil.showGoalReminderNotification(getApplicationContext(), goal);
                }
            }
        });

        Log.d("Goals Request Finished", "Goals Request Finished");

        return Result.success();
    }
}
