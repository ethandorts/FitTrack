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
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class GoalCompletedChecker extends Worker {
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private String UserID = mAuth.getUid();
    private GoalCheckerUtil checkerUtil = new GoalCheckerUtil();


    public GoalCompletedChecker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        NotificationUtil.createCalorieGoalSuccessfulNotificationChannel(getApplicationContext());
        NotificationUtil.createDistanceGoalSuccessfulNotificationChannel(getApplicationContext());
        NotificationUtil.createTimeGoalSuccessfulNotificationChannel(getApplicationContext());
    }

    @NonNull
    @Override
    public Result doWork() {
        Log.d("Goal Completed Checker", "Goal Checker is Running...");
//        Query query = db.collection("Users")
//                .document(UserID)
//                .collection("Goals")
//                .whereEqualTo("goalType", "Time")
//                .whereEqualTo("status", "In Progress");
//
//        query.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
//            @Override
//            public void onSuccess(QuerySnapshot querySnapshot) {
//                List<TimeGoal> timeGoals = new ArrayList<>();
//                List<DocumentSnapshot> documents = querySnapshot.getDocuments();
//
//                for(DocumentSnapshot snapshot : documents) {
//                    timeGoals.add(new TimeGoal((Double) snapshot.get("targetTime"), (Double) snapshot.get("targetDistance")));
//                }
//
//
//            }
//        }).addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception e) {
//
//            }
//        });

        checkerUtil.checkTimeGoalIsAchieved(UserID, new GoalCheckerUtil.NotificationCallback() {
            @Override
            public void onCallback(boolean notify, String GoalID) {
                if(notify) {
                    NotificationUtil.showTimeGoalSuccessfulNotification(getApplicationContext());
                    DocumentReference docRef = db.collection("Users")
                            .document(UserID)
                            .collection("Goals")
                            .document(GoalID);

                    docRef.update("status", "Completed").addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Log.d("Goal Success", "Goal Success");
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d("Failure to update Goal Success", e.getMessage());
                        }
                    });
                }
            }
        });
        checkerUtil.checkDistanceGoalIsAchieved(UserID, new GoalCheckerUtil.NotificationCallback() {
            @Override
            public void onCallback(boolean notify, String GoalID) {
                if(notify) {
                    NotificationUtil.showDistanceGoalSuccessfulNotification(getApplicationContext());
                    DocumentReference docRef = db.collection("Users")
                            .document(UserID)
                            .collection("Goals")
                            .document(GoalID);

                    docRef.update("status", "Completed").addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Log.d("Goal Success", "Goal Success");
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d("Failure to update Goal Success", e.getMessage());
                        }
                    });
                }
            }
        });
        checkerUtil.checkCalorieGoalIsAchieved(UserID, new GoalCheckerUtil.NotificationCallback() {
            @Override
            public void onCallback(boolean notify, String GoalID) {
                if(notify) {
                    NotificationUtil.showCalorieGoalSuccessfulNotification(getApplicationContext());
                    DocumentReference docRef = db.collection("Users")
                            .document(UserID)
                            .collection("Goals")
                            .document(GoalID);

                    docRef.update("status", "Completed").addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Log.d("Goal Success", "Goal Success");
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d("Failure to update Goal Success", e.getMessage());
                        }
                    });
                }
            }
        });



        return Result.success();
    }
}
