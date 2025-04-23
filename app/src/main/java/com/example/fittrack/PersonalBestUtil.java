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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class PersonalBestUtil {
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private String UserID = mAuth.getUid();
    private static final double aerobic_scaling_factor = 5.0;
    private static final double elevation_scaling_factor = 5.0;
    private static final double anaerobic_scaling_factor = 5.0;


    public void findFastestDistanceTime(String UserID, int distance, String activityType, PersonalBestCallback callback) {
        Query query = db.collection("Activities")
                .whereEqualTo("UserID", UserID)
                .whereEqualTo("type", activityType);

        query.get().addOnSuccessListener(querySnapshot -> {
            Log.d("Personal Best", "Activities Found: " + querySnapshot.getDocuments().size());

            int fastestTime = Integer.MAX_VALUE;
            DocumentSnapshot fastestActivityDoc = null;

            for (DocumentSnapshot activity : querySnapshot.getDocuments()) {
                String stringDistance = (String) activity.get("distance");
                double totalDistanceMeters = Double.parseDouble(stringDistance);
                double totalDistanceKm = totalDistanceMeters / 1000.0;

                if (totalDistanceMeters >= distance) {
                    List<Long> splits = (List<Long>) activity.get("splits");

                    if (splits != null && splits.size() > 0) {
                        int validSplits = (int) Math.floor(totalDistanceKm);
                        int requiredSplits = distance / 1000;

                        Log.d("Valid Splits", "Valid Splits: " + validSplits + ", Required Splits: " + requiredSplits);
                        if (validSplits >= requiredSplits) {
                            for (int i = 0; i <= validSplits - requiredSplits; i++) {
                                int segmentTime = 0;

                                for (int j = i; j < i + requiredSplits; j++) {
                                    segmentTime += convertLongtoSeconds(splits.get(j));
                                }

                                if (segmentTime < fastestTime) {
                                    fastestTime = segmentTime;
                                    fastestActivityDoc = activity;
                                    Log.d("New Fastest", "Updated Fastest Time: " + formatTime(fastestTime));
                                }
                            }
                        } else {
                            Log.d("Skipped Activity", "Not enough valid splits for required distance");
                        }
                    } else {
                        Log.d("Skipped Activity", "No splits found for activity");
                    }
                }
            }

            if (fastestTime == Integer.MAX_VALUE || fastestActivityDoc == null) {
                callback.onCallback("-", "-");
            } else {
                String formattedTime = formatTime(fastestTime);
                String formattedDate = "-";

                if (fastestActivityDoc.contains("date")) {
                    Timestamp timestamp = fastestActivityDoc.getTimestamp("date");
                    if (timestamp != null) {
                        Date date = timestamp.toDate();
                        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                        formattedDate = sdf.format(date);
                    }
                }
                callback.onCallback(formattedTime, formattedDate);
            }
        }).addOnFailureListener(e -> {
            Log.e("Personal Best", "Error fetching activities: " + e.getMessage());
            callback.onCallback("-", "-");
        });
    }


    // Based on Firstbeat Analytics (also used by Garmin)
    // Seiler, S. (2010) "Quantifying Training Load for Endurance Athletes"
    // Ainsworth et al. (2011) "Compendium of Physical Activities"

    public void calculateAerobicTrainingEffect() {

    }


    public int convertLongtoSeconds(long milliseconds) {
        int seconds = (int) Math.round(milliseconds / 1000.0);
        return seconds;
    }

    public String formatTime(int seconds) {
        int hours = seconds / 3600;
        int minutes = (seconds % 3600) / 60;
        int remainingSeconds = seconds % 60;

        if (hours > 0) {
            return String.format("%02d:%02d:%02d", hours, minutes, remainingSeconds);
        } else {
            return String.format("%02d:%02d", minutes, remainingSeconds);
        }
    }

    public interface PersonalBestCallback {
        void onCallback(String PB, String date);
    }
}
