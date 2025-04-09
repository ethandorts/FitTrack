package com.example.fittrack;

import static com.example.fittrack.ConversionUtil.convertLongtoSeconds;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class GamificationUtil {
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseDatabaseHelper usersUtil = new FirebaseDatabaseHelper(db);
    public void calculateUserDistancePerMonth(String UserID, String activityType, UserDistancePerMonthCallback callback) {

        Calendar calendar = Calendar.getInstance();
        Date today = calendar.getTime();

        calendar.add(Calendar.MONTH, -1);
        Date lastMonth = calendar.getTime();

        System.out.println(today);
        System.out.println(lastMonth);

        db.collection("Activities")
                .whereEqualTo("UserID", UserID)
                .whereEqualTo("type", activityType)
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

    public void calculateUserDistancePerWeek(String UserID, String activityType, UserDistancePerMonthCallback callback) {

        Calendar calendar = Calendar.getInstance();
        Date today = calendar.getTime();

        calendar.add(Calendar.DAY_OF_MONTH, -7);
        Date lastWeek = calendar.getTime();

        System.out.println(today);
        System.out.println(lastWeek);

        db.collection("Activities")
                .whereEqualTo("UserID", UserID)
                .whereEqualTo("type", activityType)
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

    public void calculateUserActivitiesPerMonth(String UserID, String activityType, ActivityFrequencyCallback callback) {
        Calendar calendar = Calendar.getInstance();
        Date today = calendar.getTime();

        calendar.add(Calendar.MONTH, -1);
        Date lastWeek = calendar.getTime();

        System.out.println(today);
        System.out.println(lastWeek);

        db.collection("Activities")
                .whereEqualTo("UserID", UserID)
                .whereEqualTo("type", activityType)
                .whereLessThanOrEqualTo("date", today)
                .whereGreaterThanOrEqualTo("date", lastWeek)
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot querySnapshot) {
                        int size = querySnapshot.getDocuments().size();
                        System.out.println("Number of activity frequency: " + size);
                        callback.onCallback(size);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("User Distance Per Week Failure", "Failure to get total user distance per week: " + e);
                    }
                });
    }

    public void calculateUserActivitiesPerWeek(String UserID, String activityType, ActivityFrequencyCallback callback) {
        Calendar calendar = Calendar.getInstance();
        Date today = calendar.getTime();

        calendar.add(Calendar.DAY_OF_MONTH, -7);
        Date lastWeek = calendar.getTime();

        System.out.println(today);
        System.out.println(lastWeek);

        db.collection("Activities")
                .whereEqualTo("UserID", UserID)
                .whereEqualTo("type", activityType)
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

    public void collectDistanceTime(ArrayList<String> runners, String ActivityType, double distance, boolean isWeek, FastestTimeCallback callback) {
        Calendar calendar = Calendar.getInstance();
        Date today = calendar.getTime();
        Date timeFrame;

        ArrayList<LeaderboardModel> timeLeaderboard = new ArrayList<>();
        AtomicInteger timesProcessed = new AtomicInteger(0);
        int requiredSplits = (int) (distance / 1000);

        if (isWeek) {
            calendar.add(Calendar.DAY_OF_MONTH, -7);
            Date lastWeek = calendar.getTime();
            timeFrame = lastWeek;
        } else {
            calendar.add(Calendar.MONTH, -1);
            Date lastMonth = calendar.getTime();
            timeFrame = lastMonth;
        }

        for (String runner : runners) {
            db.collection("Activities")
                    .whereEqualTo("UserID", runner)
                    .whereEqualTo("type", ActivityType)
                    .whereLessThanOrEqualTo("date", today)
                    .whereGreaterThan("date", timeFrame)
                    .get()
                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot querySnapshot) {
                            int fastestSegmentTime = Integer.MAX_VALUE;
                            String fastestActivityID = null;

                            for (DocumentSnapshot activity : querySnapshot.getDocuments()) {
                                String stringDistance = activity.getString("distance");
                                String activityID = activity.getString("ActivityID");

                                if (stringDistance == null || activityID == null) continue;

                                double totalDistanceMeters = Double.parseDouble(stringDistance);
                                double totalDistanceKm = totalDistanceMeters / 1000.0;

                                if (totalDistanceKm < distance / 1000.0) continue;

                                List<Long> splits = (List<Long>) activity.get("splits");
                                if (splits == null || splits.size() < requiredSplits) continue;

                                int validSplits = (int) Math.floor(totalDistanceKm);

                                for (int i = 0; i <= validSplits - requiredSplits; i++) {
                                    int segmentTime = 0;

                                    for (int j = i; j < i + requiredSplits; j++) {
                                        segmentTime += convertLongtoSeconds(splits.get(j));
                                    }

                                    if (segmentTime < fastestSegmentTime) {
                                        fastestSegmentTime = segmentTime;
                                        fastestActivityID = activityID;
                                    }
                                }
                            }

                            if (fastestSegmentTime != Integer.MAX_VALUE && fastestActivityID != null) {
                                timeLeaderboard.add(new LeaderboardModel(runner, fastestSegmentTime, fastestActivityID));
                            }

                            if (timesProcessed.incrementAndGet() == runners.size()) {
                                Collections.sort(timeLeaderboard, new Comparator<LeaderboardModel>() {
                                    @Override
                                    public int compare(LeaderboardModel a, LeaderboardModel b) {
                                        return Double.compare(a.getDistance(), b.getDistance());
                                    }
                                });
                                callback.onCallback(timeLeaderboard);
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                        }
                    });
        }
    }



    public void calculateAverageUserPacePerWeek(String UserID, String activityType, AverageUserPaceCallback callback) {
        Calendar calendar = Calendar.getInstance();
        Date today = calendar.getTime();

        calendar.add(Calendar.DAY_OF_MONTH, -7);
        Date lastWeek = calendar.getTime();

        System.out.println(today);
        System.out.println(lastWeek);

        db.collection("Activities")
                .whereEqualTo("UserID", UserID)
                .whereEqualTo("type", activityType)
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

    public void getActivityProgressData(String UserID, Timestamp startDate, Timestamp endDate, ProgressCallback callback) {

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

    public void retrieveElevationStats(String ActivityID, ActivityElevationCallback callback) {
        ArrayList<Double> elevationValues = new ArrayList<>();
        db.collection("Activities")
                .whereEqualTo("ActivityID", ActivityID)
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot querySnapshot) {
                        List<DocumentSnapshot> documents = querySnapshot.getDocuments();
                        double time = 0;
                        for(DocumentSnapshot snapshot : documents) {
                            List<Double> stats = (List<Double>) snapshot.get("Elevation");
                            time = (double) snapshot.get("time");
                            if(stats != null) {
                                for(Double stat : stats) {
                                    elevationValues.add(stat);
                                }
                            }
                        }
                        callback.onCallback(elevationValues, time);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("Activity Elevation Stats Retrieval Failure", "Failure to get elevation stats: " + e);
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

    public interface FastestTimeCallback {
        void onCallback(ArrayList<LeaderboardModel> timeData);
    }

    public interface ActivityFrequencyCallback {
        void onCallback(int activityNumber);
    }

    public interface AverageUserPaceCallback {
        void onCallback(String pace);
    }

    public interface ActivitySplitsCallback {
        void onCallback(List<String> splits);
    }

    public interface ActivityElevationCallback {
        void onCallback(List<Double> elevationStats, double time);
    }

    public interface ProgressCallback {
        void onCallback(ArrayList<ActivityModel> activities);
    }
}
