package com.example.fittrack;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ActivityCompletedChecker extends Worker {
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private String UserID = mAuth.getUid();
    public ActivityCompletedChecker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        Log.d("WorkRequest", "WorkRequest is Running...");
        Query eventsQuery = db.collection("Users")
                .document(UserID)
                .collection("Calendar")
                .whereEqualTo("DateTime", dateFormatter(Timestamp.now()));

        eventsQuery.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot querySnapshot) {
                System.out.println(querySnapshot.getDocuments().size());
            }
        });

        eventsQuery.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot querySnapshot) {
                Date date = new Date();
                String format = String.valueOf(date.getYear() + 1900) + "-" +  String.format("%02d", date.getMonth() + 1) + "-" + String.valueOf(date.getDate());
                System.out.println("FormatDate: " + format);
                if(querySnapshot.getDocuments().size() > 0) {
                    Query todayActivitiesQuery = db.collection("Activities")
                            .whereEqualTo("shortDate", format);
                    todayActivitiesQuery.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot querySnapshot) {
                            System.out.println("Number: " + querySnapshot.getDocuments().size());
                            if(querySnapshot.getDocuments().size() == 0) {
                                NotificationUtil.createWorkoutReminderNotificationChannel(getApplicationContext());
                                NotificationUtil.showWorkoutReminderNotification(getApplicationContext());
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d("Exception","Exception: " + e);
                        }
                    });
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
        return Result.success();
    }

    private String dateFormatter(Timestamp timestamp) {
        Date date = timestamp.toDate();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        String formattedDate = formatter.format(date);

        return formattedDate;
    }
}
