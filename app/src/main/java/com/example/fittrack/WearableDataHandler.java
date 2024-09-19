package com.example.fittrack;


import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class WearableDataHandler {
    private ArrayList<Map<String, Object>> wearableData;
    private static final DecimalFormat TwoDecimalRounder = new DecimalFormat("0.00");
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    private void clearWearableData () {
        wearableData.clear();
    }

    public void saveWearableData(String UserID, double distance, int time ) {
        Map<String, Object> data = new HashMap<>();
        data.put("distance", TwoDecimalRounder.format(distance));
        data.put("time", time);
        data.put("UserID", UserID);
        data.put("date", Timestamp.now());
        data.put("pace", (calculateAveragePace(distance, time)));
        data.put("type", "Running");
        //data.put("activityCoordinates", activityLocations);

        db.collection("Activities")
                .add(data)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d("Successful Wearable Activity Write", "Activity Written");
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("Wearable Write Failure", "Error");
                    }
                });
    }


    // added in at the moment to ensure functionality is working
    // in the future, we could create a file with various methods
    // for code reusability
    private String calculateAveragePace(double distance, double time) {
        double speed = distance / time;
        double kilometresPerHour = speed * 3.6;
        double averagePace = Double.parseDouble(TwoDecimalRounder.format(60 / kilometresPerHour));
        int seconds = (int) (averagePace % 1 * 60);
        int minutes = (int) averagePace;
        String formattedPace = minutes + ":" + seconds;

        return String.valueOf(formattedPace);
    }

}
