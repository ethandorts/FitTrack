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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class GoalCheckerUtil {
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    String currentUser = mAuth.getUid();
    public GoalCheckerUtil () {

    }

    public void checkDistanceGoalIsAchieved(String UserID, NotificationCallback callback) {
        List<Double> distances = new ArrayList<>();

        Query query = db.collection("Users")
                .document(UserID)
                .collection("Goals")
                .whereEqualTo("goalType", "Distance")
                .whereEqualTo("status", "In Progress");

        query.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot querySnapshot) {
                List<DocumentSnapshot> goalDocuments = querySnapshot.getDocuments();
                System.out.println("Number of  distance goal documents: " + goalDocuments.size());
                for(DocumentSnapshot snapshot : goalDocuments) {
                    System.out.println(snapshot.get("startDate") + ": startDate");
                    System.out.println(snapshot.get("endDate") + ": endDate");
                    Timestamp startDate = (Timestamp) snapshot.get("startDate");
                    Timestamp endDate = (Timestamp) snapshot.get("endDate");
                    double targetDistance = (double) snapshot.get("targetDistance");

                    Query activitiesQuery = db.collection("Activities")
                            .whereEqualTo("UserID", UserID)
                            .whereGreaterThanOrEqualTo("date", startDate)
                            .whereLessThanOrEqualTo("date", endDate);

                    activitiesQuery.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot querySnapshot) {
                            System.out.println("Suitable Distance Activities: " + querySnapshot.size());
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
                                callback.onCallback(true, snapshot.getId());

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

    public void checkTimeGoalIsAchieved(String UserID, NotificationCallback callback) {
        Query query = db.collection("Users")
                .document(UserID)
                .collection("Goals")
                .whereEqualTo("goalType", "Time")
                .whereEqualTo("status", "In Progress");

        query.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot querySnapshot) {
                List<DocumentSnapshot> goalDocuments = querySnapshot.getDocuments();
                System.out.println("Number of goal documents: " + goalDocuments.size());

                for (DocumentSnapshot snapshot : goalDocuments) {
                    Timestamp startDate = (Timestamp) snapshot.get("startDate");
                    Timestamp endDate = (Timestamp) snapshot.get("endDate");
                    double targetDistance = (double) snapshot.get("targetDistance");
                    double targetTime = (double) snapshot.get("targetTime");

                    System.out.println("Target Distance (km): " + targetDistance);
                    System.out.println("Target Time (seconds): " + targetTime);

                    Query activitiesQuery = db.collection("Activities")
                            .whereEqualTo("UserID", UserID)
                            .whereGreaterThanOrEqualTo("date", startDate)
                            .whereLessThanOrEqualTo("date", endDate);

                    activitiesQuery.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot querySnapshot) {
                            System.out.println("Number of activities found: " + querySnapshot.getDocuments().size());
                            List<DocumentSnapshot> activityDocuments = querySnapshot.getDocuments();
                            boolean achieved = false;

                            for (DocumentSnapshot activity : activityDocuments) {
                                double totalDistance = Double.parseDouble((String) activity.get("distance"));
                                totalDistance = totalDistance / 1000;
                                List<Long> splits = (List<Long>) activity.get("splits");

                                if (splits != null && splits.size() > 0) {
                                    int validSplits = (int) Math.floor(totalDistance);

                                    System.out.println("Total Distance (km): " + totalDistance);
                                    System.out.println("Valid Full Splits: " + validSplits);

                                    int requiredSplits = (int) targetDistance;
                                    System.out.println(requiredSplits + " : Required Splits");
                                    for (int i = 0; i < validSplits; i++) {
                                        double segmentTime = 0;
                                        for (int j = i; j < validSplits; j++) {
                                            int seconds = convertLongtoSeconds(splits.get(j));
                                            segmentTime += seconds;
                                        }
                                        System.out.println("Segment Time" + segmentTime);
                                        if (segmentTime <= targetTime) {
                                            System.out.println("Achieved with segment starting at split " + i + " and time: " + segmentTime);
                                            achieved = true;
                                            break;
                                        }
                                    }
                                    if (achieved) break;
                                }
                            }
                            if (achieved) {
                                System.out.println("Time goal achieved");
                                callback.onCallback(true, snapshot.getId());
                            } else {
                                System.out.println("Time goal not achieved");
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.e("Activities Failure", "Error retrieving activities: " + e.getMessage());
                        }
                    });
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e("Goals Failure", "Error retrieving goals: " + e.getMessage());
            }
        });
    }




    public void checkCalorieGoalIsAchieved(String UserID, NotificationCallback callback) {
        Query query = db.collection("Users")
                .document(UserID)
                .collection("Goals")
                .whereEqualTo("goalType", "Calorie")
                .whereEqualTo("status", "In Progress");

        query.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot querySnapshot) {
                List<DocumentSnapshot> goalDocuments = querySnapshot.getDocuments();
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
                Date today = new Date();
                String shortDate = dateFormat.format(today);
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
                            .document(shortDate)
                            .collection("Meals");

                    nutritionQuery.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot querySnapshot) {
                            boolean notify = false;
//                            if(querySnapshot.getDocuments().size() == 0) {
//                                notify = false;
//                            }
                            System.out.println(querySnapshot.size());
                            double nutritionTotal = 0;
                            List<DocumentSnapshot> nutritionDocuments = querySnapshot.getDocuments();
                            for(DocumentSnapshot nutritionDocument : nutritionDocuments) {
                                nutritionTotal = nutritionTotal + nutritionDocument.getDouble("calories");
                            }
                            System.out.println("Nutrition Total: " + nutritionTotal);
                            if(nutritionTotal < minimum || nutritionTotal > maximum) {
                                System.out.println("Calorie count not met");
                            } else {
                                System.out.println("Calorie count met");
                                notify = true;
                                callback.onCallback(notify, snapshot.getId());
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

    public int convertLongtoSeconds(long milliseconds) {
        int seconds = (int) Math.round(milliseconds / 1000.0);
        return seconds;
    }

    public interface NotificationCallback {
        void onCallback(boolean isTrue, String GoalID);
    }
}
