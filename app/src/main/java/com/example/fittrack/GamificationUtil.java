package com.example.fittrack;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class GamificationUtil {
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    public void calculateUserDistancePerMonth(String UserID, UserDistancePerMonthCallback callback) {

        Calendar calendar = Calendar.getInstance();
        Date today = calendar.getTime();

        calendar.add(Calendar.MONTH, -1);
        Date lastMonth = calendar.getTime();

        System.out.println(today);
        System.out.println(lastMonth);

        db.collection("Activities")
                .whereEqualTo("UserID", UserID)
                .whereLessThanOrEqualTo("date", today)
                .whereGreaterThanOrEqualTo("date", lastMonth)
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot querySnapshot) {
                        double distance = 0;
                        List<DocumentSnapshot> activityDocuments = querySnapshot.getDocuments();
                        for(DocumentSnapshot document: activityDocuments) {
                            double distanceRetrieved = Double.parseDouble(String.valueOf(document.get("distance")));
                            distance += distanceRetrieved;
                        }
                        callback.onCallback(distance);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("User Distance Per Month Failure", "Failure to get total user distance per month: " + e);
                    }
                });
    }

    public void calculateUserDistancePerWeek(String UserID, UserDistancePerMonthCallback callback) {

        Calendar calendar = Calendar.getInstance();
        Date today = calendar.getTime();

        calendar.add(Calendar.DAY_OF_MONTH, -7);
        Date lastWeek = calendar.getTime();

        System.out.println(today);
        System.out.println(lastWeek);

        db.collection("Activities")
                .whereEqualTo("UserID", UserID)
                .whereLessThanOrEqualTo("date", today)
                .whereGreaterThanOrEqualTo("date", lastWeek)
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot querySnapshot) {
                        double distance = 0;
                        List<DocumentSnapshot> activityDocuments = querySnapshot.getDocuments();
                        for(DocumentSnapshot document: activityDocuments) {
                            double distanceRetrieved = Double.parseDouble(String.valueOf(document.get("distance")));
                            distance += distanceRetrieved;
                        }
                        callback.onCallback(distance);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("User Distance Per Week Failure", "Failure to get total user distance per week: " + e);
                    }
                });
    }

    public void calculateUserActivitiesPerMonth(String UserID, UserDistancePerMonthCallback callback) {

        Calendar calendar = Calendar.getInstance();
        Date today = calendar.getTime();

        calendar.add(Calendar.MONTH, -1);
        Date lastWeek = calendar.getTime();

        System.out.println(today);
        System.out.println(lastWeek);

        db.collection("Activities")
                .whereEqualTo("UserID", UserID)
                .whereLessThanOrEqualTo("date", today)
                .whereGreaterThanOrEqualTo("date", lastWeek)
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot querySnapshot) {
                        int size = querySnapshot.getDocuments().size();
                        callback.onCallback(size);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("User Distance Per Week Failure", "Failure to get total user distance per week: " + e);
                    }
                });
    }

    public void calculateUserActivitiesPerWeek(String UserID, UserDistancePerMonthCallback callback) {

        Calendar calendar = Calendar.getInstance();
        Date today = calendar.getTime();

        calendar.add(Calendar.DAY_OF_MONTH, -7);
        Date lastWeek = calendar.getTime();

        System.out.println(today);
        System.out.println(lastWeek);

        db.collection("Activities")
                .whereEqualTo("UserID", UserID)
                .whereLessThanOrEqualTo("date", today)
                .whereGreaterThanOrEqualTo("date", lastWeek)
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot querySnapshot) {
                        int size = querySnapshot.getDocuments().size();
                        callback.onCallback(size);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("User Activities Per Week Failure", "Failure to get total user distance per week: " + e);
                    }
                });
    }

    public void calculateAverageUserPacePerWeek(String UserID, AverageUserPaceCallback callback) {
        Calendar calendar = Calendar.getInstance();
        Date today = calendar.getTime();

        calendar.add(Calendar.DAY_OF_MONTH, -7);
        Date lastWeek = calendar.getTime();

        System.out.println(today);
        System.out.println(lastWeek);

        db.collection("Activities")
                .whereEqualTo("UserID", UserID)
                .whereLessThanOrEqualTo("date", today)
                .whereGreaterThanOrEqualTo("date", lastWeek)
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot querySnapshot) {
                        int totalPace = 0;
                        int activityCount = 0;
                        List<DocumentSnapshot> documents = querySnapshot.getDocuments();
                        for(DocumentSnapshot snapshot : documents) {
                            String pace = (String) snapshot.get("pace");
                            if(pace != null) {
                                int total = convertPacetoSeconds(pace);
                                activityCount++;
                                totalPace += total;
                            }
                        }
                        int averagePaceActivities = totalPace / activityCount;
                        String averagePaceinString = convertSecondstoPace(averagePaceActivities);
                        callback.onCallback(averagePaceinString);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("User Pace Per Week Failure", "Failure to get total user average pace per week: " + e);
                    }
                });
    }

    public void calculateAverageUserPacePerMonth(String UserID, AverageUserPaceCallback callback) {
        Calendar calendar = Calendar.getInstance();
        Date today = calendar.getTime();

        calendar.add(Calendar.MONTH, -1);
        Date lastMonth = calendar.getTime();

        System.out.println(today);
        System.out.println(lastMonth);

        db.collection("Activities")
                .whereEqualTo("UserID", UserID)
                .whereLessThanOrEqualTo("date", today)
                .whereGreaterThanOrEqualTo("date", lastMonth)
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot querySnapshot) {
                        int totalPace = 0;
                        int activityCount = 0;
                        List<DocumentSnapshot> documents = querySnapshot.getDocuments();
                        for(DocumentSnapshot snapshot : documents) {
                            String pace = (String) snapshot.get("pace");
                            if(pace != null) {
                                int total = convertPacetoSeconds(pace);
                                activityCount++;
                                totalPace += total;
                            }
                        }
                        int averagePaceActivities = totalPace / activityCount;
                        String averagePaceinString = convertSecondstoPace(averagePaceActivities);
                        callback.onCallback(averagePaceinString);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("User Pace Per Month Failure", "Failure to get total user average pace per month: " + e);
                    }
                });
    }

    public void retrieveActivitySplits(String ActivityID, ActivitySplitsCallback callback) {
        ArrayList<String> paceValues = new ArrayList<>();
        db.collection("Activities")
                .whereEqualTo("ActivityID", ActivityID)
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot querySnapshot) {
                        List<DocumentSnapshot> documents = querySnapshot.getDocuments();
                        for(DocumentSnapshot snapshot : documents) {
                            List<Long> splits = (List<Long>) snapshot.get("splits");
                            if(splits != null) {
                                for(Long split : splits) {
                                    String formattedSplit = longToTimeConversion(split);
                                    paceValues.add(formattedSplit);
                                }
                            }
                        }
                        callback.onCallback(paceValues);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("Activity Splits Retrieval Failure", "Failure to get splits: " + e);
                    }
                });
    }

    public int convertPacetoSeconds(String pace) {
        String [] paceParts = pace.split(":");
        int minutes = Integer.parseInt(paceParts[0]);
        int seconds = Integer.parseInt(paceParts[1]);
        return minutes * 60 + seconds;
    }

    public String convertSecondstoPace(int paceSeconds) {
        int minutes = paceSeconds / 60;
        int seconds = paceSeconds % 60;
        return String.format("%d:%02d", minutes, seconds);
    }

    private String longToTimeConversion(long longValue) {
        longValue = longValue / 1000;
        long hours = longValue / 3600;
        long minutes = (longValue % 3600) / 60;
        long seconds = longValue % 60;

        if (hours > 0) {
            return String.format("%02d:%02d:%02d", hours, minutes, seconds);
        } else {
            return String.format("%02d:%02d", minutes, seconds);
        }
    }

    public interface UserDistancePerMonthCallback {
        void onCallback(double distancePerMonth);
    }

    public interface AverageUserPaceCallback {
        void onCallback(String pace);
    }

    public interface ActivitySplitsCallback {
        void onCallback(List<String> splits);
    }
}
