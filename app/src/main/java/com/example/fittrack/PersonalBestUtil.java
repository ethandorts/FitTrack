package com.example.fittrack;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;

public class PersonalBestUtil {
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private String UserID = mAuth.getUid();
    private static final double aerobic_scaling_factor = 5.0;
    private static final double elevation_scaling_factor = 5.0;
    private static final double anaerobic_scaling_factor = 5.0;


    public void findFastest5K(String UserID, int distance, String activityType) {
        Query query = db.collection("Activities")
                .whereEqualTo("UserID", UserID)
                .whereEqualTo("type", activityType);

        query.get().addOnSuccessListener(querySnapshot -> {
            Log.d("Personal Best", "Activities Found: " + querySnapshot.getDocuments().size());
            int fastestTime = Integer.MAX_VALUE;

            List<DocumentSnapshot> activityDocuments = querySnapshot.getDocuments();

            for (DocumentSnapshot activity : activityDocuments) {
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

                                // Update the fastest time if this segment is faster
                                if (segmentTime < fastestTime) {
                                    fastestTime = segmentTime;
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
            if (fastestTime == Integer.MAX_VALUE) {
                System.out.println("Distance requirements not met or no valid splits found");
            } else {
                System.out.println("Fastest Time: " + formatTime(fastestTime));
            }
        }).addOnFailureListener(e -> {
            System.out.println("Error fetching activities: " + e.getMessage());
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
        int minutes = seconds / 60;
        int remainingSeconds = seconds % 60;
        return String.format("%d min %02d sec", minutes, remainingSeconds);
    }
}
