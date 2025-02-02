package com.example.fittrack;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SaveActivityDialog extends DialogFragment {
    private FirebaseUser mAuth;
    private String UserID;
    private FirebaseFirestore db;
    private static final DecimalFormat TwoDecimalRounder = new DecimalFormat("0.00");
    private CaloriesCalculator caloriesCalculator = new CaloriesCalculator();
    private double distance;
    private double time;
    private String type;
    private List<Long> splits = new ArrayList<>();
    private LocalDate date;
    Map<String, Object> data;
    private List<Parcelable> activityLocations = new ArrayList<>();
    private ActivityLocationsDao activityLocationsDao;
    private List<LatLng> locations;

    @Override
    public Dialog onCreateDialog(Bundle SavedInstanceState) {
        Activity activity = getActivity();
        System.out.println("Activity: " + activity);
        mAuth = FirebaseAuth.getInstance().getCurrentUser();
        UserID = mAuth.getUid();
        db = FirebaseFirestore.getInstance();

        NotificationUtil.createSavedWorkoutNotificationChannel(activity);

        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(true)
                .build();
        db.setFirestoreSettings(settings);


        activityLocationsDao = ActivityLocationsDatabase.getActivityLocationsDatabase(getContext()).activityLocationsDao();

        if (getArguments() != null) {
            distance = getArguments().getDouble("distance");
            time = getArguments().getDouble("time");
            type = getArguments().getString("type");
            //activityLocations = getArguments().getParcelableArrayList("activityLocations");
            long[] splitsArray = getArguments().getLongArray("splits");

            List<Long> splitsList = new ArrayList<>();
            for (long split : splitsArray) {
                splitsList.add(split);
            }
            splits = splitsList;
        } else {
            Log.d("getArguments", "Arguments are null");
        }

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        Date today = new Date();
        String shortDate = dateFormat.format(today);

            data = new HashMap<>();
            data.put("distance", TwoDecimalRounder.format(distance));
            data.put("time", time);
            data.put("UserID", UserID);
            data.put("date", Timestamp.now());
            data.put("shortDate", shortDate);
            data.put("pace", (calculateAveragePace(distance, time)));
            data.put("type", type);
            data.put("activityCoordinates", locations);
            data.put("splits", splits);
            data.put("caloriesBurned", caloriesCalculator.calculateCalories(time, (calculateAveragePace(distance, time)), type, 88));

        new Thread(new Runnable() {
            @Override
            public void run() {
                List<Map<String, Object>> activityCoordinates = new ArrayList<>();
                List<ActivityLocationsEntity> entitiesList = activityLocationsDao.retrieveAllLocations();

                if (entitiesList != null && !entitiesList.isEmpty()) {
                    for (ActivityLocationsEntity entity : entitiesList) {
                        Map<String, Object> coordinates = new HashMap<>();
                        coordinates.put("latitude", entity.getLatitude());
                        coordinates.put("longitude", entity.getLongitude());
                        activityCoordinates.add(coordinates);
                    }
                    data.put("activityCoordinates", activityCoordinates);
                } else {
                    Log.e("SaveActivityDialog", "No locations retrieved from database.");
                }
            }
        }).start();

            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setMessage("Do you want to save your activity?")
                    .setPositiveButton("Save Activity", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            String ActivityID = DocumentIDGenerator.GenerateActivityID();
                            data.put("ActivityID", ActivityID);

                            db.collection("Activities")
                                    .document(ActivityID)
                                    .set(data)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            NotificationUtil.showSavedActivityNotification(activity);
                                            OneTimeWorkRequest checkGoalsRequest = new OneTimeWorkRequest.Builder(GoalCompletedChecker.class)
                                                    .build();
                                            WorkManager.getInstance(getContext()).enqueue(checkGoalsRequest);
                                            Log.d("Activity Successfully Written", "Activity Successfully Written");
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.d("Activity Write Failure", "Activity Write Failure");
                                        }
                                    });
                        }
                    })
                    .setNegativeButton("Resume Activity", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            // Clock and Distance will continue recording activity.
                        }
                    });
            return builder.create();
        }

        private String calculateAveragePace (double distance, double time){
            double speed = distance / time;
            double kilometresPerHour = speed * 3.6;
            double averagePace = Double.parseDouble(TwoDecimalRounder.format(60 / kilometresPerHour));
            int seconds = (int) (averagePace % 1 * 60);
            int minutes = (int) averagePace;
            String formattedPace = String.format("%d:%02d", minutes, seconds);

            return formattedPace;
        }
}