package com.example.fittrack;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class GoalsUtil {
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    public GoalsUtil(FirebaseFirestore db) {
        this.db = db;
    }

    public void setDistanceGoal(String UserID, Timestamp startDate, Timestamp endDate, double targetDistance, String status, double currentProgress, String goalDescription) {

        HashMap<String, Object> distanceGoalMap = new HashMap<>();
        distanceGoalMap.put("goalType", "Distance");
        distanceGoalMap.put("startDate", startDate);
        distanceGoalMap.put("endDate", endDate);
        distanceGoalMap.put("targetDistance", targetDistance);
        distanceGoalMap.put("status", status);
        distanceGoalMap.put("currentProgress", currentProgress);
        distanceGoalMap.put("goalDescription", goalDescription);


        db.collection("Users")
                .document(UserID)
                .collection("Goals")
                .add(distanceGoalMap)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d("Distance Goal Successfully Added","Added Distance Fitness Goal" + documentReference.getId());
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("Distance Goal Failure", "Failure to add a distance goal: " + e);
                    }
                });
    }

    public void setTimeGoal(String UserID, Timestamp startDate, Timestamp endDate, double targetTime, double targetDistance, String status, double currentProgress, String goalDescription) {

        HashMap<String, Object> timeGoalMap = new HashMap<>();
        timeGoalMap.put("goalType", "Time");
        timeGoalMap.put("startDate", startDate);
        timeGoalMap.put("endDate", endDate);
        timeGoalMap.put("targetTime", targetTime);
        timeGoalMap.put("targetDistance", targetDistance);
        timeGoalMap.put("status", status);
        timeGoalMap.put("currentProgress", currentProgress);
        timeGoalMap.put("goalDescription", goalDescription);


        db.collection("Users")
                .document(UserID)
                .collection("Goals")
                .add(timeGoalMap)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d("Time Goal Successfully Added","Added Time Fitness Goal" + documentReference.getId());
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("Time Goal Failure", "Failure to add a time goal: " + e);
                    }
                });
    }

    public void setCalorieGoal(String UserID, Timestamp startDate, Timestamp endDate, double targetCalories, String status, double currentProgress, String goalDescription) {

        HashMap<String, Object> calorieGoalMap = new HashMap<>();
        calorieGoalMap.put("goalType", "Calorie");
        calorieGoalMap.put("startDate", startDate);
        calorieGoalMap.put("endDate", endDate);
        calorieGoalMap.put("targetCalories", targetCalories);
        calorieGoalMap.put("status", status);
        calorieGoalMap.put("currentProgress", currentProgress);
        calorieGoalMap.put("goalDescription", goalDescription);

        db.collection("Users")
                .document(UserID)
                .collection("Goals")
                .add(calorieGoalMap)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d("Calorie Goal Successfully Added","Added Calorie Fitness Goal" + documentReference.getId());
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("Calorie Goal Failure", "Failure to add a calorie goal: " + e);
                    }
                });

    }

    public void updateGoalStatus(String UserID, String GoalID, String newStatus) {
        db.collection("Users")
                .document(UserID)
                .collection("Goals")
                .document(GoalID)
                .update("status", newStatus)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d("Goal Status Updated Successfully", "Updated goal status successfully");
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("Update Status Failed", "Failure to update goal status: " + e);
                    }
                });
    }

    public void retrieveUserGoals(String UserID, GoalsCallback callback) {
        db.collection("Users")
                .document(UserID)
                .collection("Goals")
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot querySnapshot) {
                        List<String> goals = new ArrayList<>();
                        if(!querySnapshot.getDocuments().isEmpty()) {
                            for(QueryDocumentSnapshot snapshot : querySnapshot) {
                                String status = (String) snapshot.get("status");
                                if(status.equals("In Progress")) {
                                    String goal = (String) snapshot.get("goalDescription");
                                    goals.add(goal);
                                }
                            }
                            callback.onCallback(goals);
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("Unable to retrieve goals", e.getMessage());
                    }
                });
    }

    public void retrieveUserCalorieGoals(String UserID, GoalsCallback callback) {
        db.collection("Users")
                .document(UserID)
                .collection("Goals")
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot querySnapshot) {
                        List<String> goals = new ArrayList<>();
                        if(!querySnapshot.getDocuments().isEmpty()) {
                            for(QueryDocumentSnapshot snapshot : querySnapshot) {
                                String status = (String) snapshot.get("status");
                                String type = (String) snapshot.get("goalType");
                                if(status.equals("In Progress") && type.equals("Calorie")) {
                                    String goal = (String) snapshot.get("goalDescription");
                                    goals.add(goal);
                                }
                                callback.onCallback(goals);
                            }
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("Unable to retrieve goals", e.getMessage());
                    }
                });
    }

    public interface GoalsCallback {
        void onCallback(List<String> goals);
    }
}
