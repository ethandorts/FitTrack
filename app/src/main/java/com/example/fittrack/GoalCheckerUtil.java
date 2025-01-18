package com.example.fittrack;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import org.w3c.dom.Document;

import java.util.ArrayList;
import java.util.List;

public class GoalCheckerUtil {
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    String currentUser = mAuth.getUid();
    public GoalCheckerUtil () {

    }

    public void checkDistanceGoalIsAchieved(String UserID) {
        List<Double> distances = new ArrayList<>();

        Query query = db.collection("Users")
                .document(UserID)
                .collection("Goals")
                .whereEqualTo("goalType", "Distance");

        query.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot querySnapshot) {
                List<DocumentSnapshot> goalDocuments = querySnapshot.getDocuments();
                for(DocumentSnapshot snapshot : goalDocuments) {
                    System.out.println(snapshot.get("startDate"));
                    System.out.println(snapshot.get("endDate"));
                    Timestamp startDate = (Timestamp) snapshot.get("startDate");
                    Timestamp endDate = (Timestamp) snapshot.get("endDate");
                    double targetDistance = (double) snapshot.get("targetDistance") * 1000;

                    Query activitiesQuery = db.collection("Activities")
                            .whereEqualTo("UserID", UserID)
                            .whereGreaterThanOrEqualTo("date", startDate)
                            .whereLessThanOrEqualTo("date", endDate);

                    activitiesQuery.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot querySnapshot) {
                            System.out.println(querySnapshot.size());
                            List<DocumentSnapshot> activityDocuments = querySnapshot.getDocuments();
                            double totalDistance = 0;
                            for(DocumentSnapshot snapshot : activityDocuments) {
                                String distance = (String) snapshot.get("distance");
                                totalDistance = totalDistance + Double.parseDouble(distance);
                            }
                            if(totalDistance >= targetDistance) {
                                System.out.println("Total Distance: " + totalDistance);
                                System.out.println("Target Distance: " + targetDistance);
                                System.out.println("Goal Successfully Passed");
                            } else {
                                System.out.println("Total Distance: " + totalDistance);
                                System.out.println("Target Distance: " + targetDistance);
                                System.out.println("Goal Not Passed");
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.e("Distance Failure", "Distance Failures");
                        }
                    });
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e("Users Failure", "User Goals Failure: " + e);
            }
        });
    }

    public void checkTimeGoalIsAchieved(String UserID) {
        Query query = db.collection("Users")
                .document(UserID)
                .collection("Goals")
                .whereEqualTo("goalType", "Time");

        query.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot querySnapshot) {
                List<DocumentSnapshot> goalDocuments = querySnapshot.getDocuments();
                for(DocumentSnapshot snapshot : goalDocuments) {
                    System.out.println(snapshot.get("startDate"));
                    System.out.println(snapshot.get("endDate"));
                    Timestamp startDate = (Timestamp) snapshot.get("startDate");
                    Timestamp endDate = (Timestamp) snapshot.get("endDate");
                    double targetDistance = (double) snapshot.get("targetDistance") * 1000;
                    double targetTime = (double) snapshot.get("targetTime");

                    Query activitiesQuery = db.collection("Activities")
                            .whereEqualTo("UserID", UserID)
                            .whereGreaterThanOrEqualTo("date", startDate)
                            .whereLessThanOrEqualTo("date", endDate)
                            .whereGreaterThanOrEqualTo("distance", targetDistance);

                    activitiesQuery.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot querySnapshot) {
                            System.out.println(querySnapshot.size());
                            List<DocumentSnapshot> activityDocuments = querySnapshot.getDocuments();
                            for(DocumentSnapshot activityDocument : activityDocuments) {
                                List<Long> splits = (List<Long>) activityDocument.get("splits");
                                double originalTargetDistance = (double) snapshot.get("splits");
                                if(splits != null && splits.size() >= originalTargetDistance) {
                                    boolean achieved = false;
                                    for(int i = 0; i <= splits.size() - targetDistance; i++) {
                                        double segmentTime = 0;
                                        for(int x = i; x < i + targetDistance; x++) {
                                            segmentTime += splits.get(x);
                                        }
                                        if(segmentTime < targetTime) {
                                            System.out.println(segmentTime);
                                            achieved = true;
                                            break;
                                        }
                                    }
                                    if(!achieved) {
                                        System.out.println("Time goal not achieved");
                                    } else {
                                        System.out.println("Time goal achieved");
                                    }
                                }
                            }

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.e("Time Failure", "Time Failures");
                        }
                    });
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e("Users Failure", "User Goals Failure: " + e);
            }
        });
    }


    public void checkCalorieGoalIsAchieved(String UserID) {
        Query query = db.collection("Users")
                .document(UserID)
                .collection("Goals")
                .whereEqualTo("goalType", "Calorie");

        query.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot querySnapshot) {
                List<DocumentSnapshot> goalDocuments = querySnapshot.getDocuments();
                for(DocumentSnapshot snapshot : goalDocuments) {
                    System.out.println(snapshot.get("startDate"));
                    System.out.println(snapshot.get("endDate"));
                    Timestamp startDate = (Timestamp) snapshot.get("startDate");
                    Timestamp endDate = (Timestamp) snapshot.get("endDate");
                    double targetCalories = (double) snapshot.get("targetCalories");
                    double percent = targetCalories * 0.05;
                    double minimum = targetCalories - percent;
                    double maximum = targetCalories + percent;

                    Query nutritionQuery = db.collection("Users")
                            .document(UserID)
                            .collection("Nutrition")
                            .document(String.valueOf(Timestamp.now()))
                            .collection("Meals");

                    nutritionQuery.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot querySnapshot) {
                            System.out.println(querySnapshot.size());
                            double nutritionTotal = 0;
                            List<DocumentSnapshot> nutritionDocuments = querySnapshot.getDocuments();
                            for(DocumentSnapshot nutritionDocument : nutritionDocuments) {
                                nutritionTotal = nutritionTotal + nutritionDocument.getDouble("calories");
                            }
                            if(nutritionTotal < minimum || nutritionTotal > maximum) {
                                System.out.println("Calorie count not met");
                            } else {
                                System.out.println("Calorie count met");
                            }

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.e("Calorie Failure", "Calorie Failures");
                        }
                    });
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e("Users Failure", "User Goals Failure: " + e);
            }
        });
    }
}
