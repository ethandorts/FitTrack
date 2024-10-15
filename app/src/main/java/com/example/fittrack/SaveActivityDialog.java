package com.example.fittrack;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.internal.ParcelableSparseArray;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.type.LatLng;

import java.lang.reflect.Array;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
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

    private double distance;
    private double time;
    private LocalDate date;
    private List<Parcelable> activityLocations = new ArrayList<>();
    @Override
    public Dialog onCreateDialog(Bundle SavedInstanceState) {
        mAuth = FirebaseAuth.getInstance().getCurrentUser();
        UserID = mAuth.getUid();
        db = FirebaseFirestore.getInstance();

        if(getArguments() != null) {
            distance = getArguments().getDouble("distance");
            time = getArguments().getDouble("time");
            activityLocations = getArguments().getParcelableArrayList("activityLocations");
        } else {
            Log.d("getArguments", "Arguments are null");
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());


        Map<String, Object> data = new HashMap<>();
        data.put("distance", TwoDecimalRounder.format(distance));
        data.put("time", time);
        data.put("UserID", UserID);
        data.put("date", Timestamp.now());
        data.put("pace", (calculateAveragePace(distance, time)));
        data.put("type", "Running");
        data.put("activityCoordinates", activityLocations);
        //data.put("caloriesBurned", )

        builder.setMessage("Do you want to save your activity?")
                .setPositiveButton("Save Activity", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        db.collection("Activities")
                                .add(data)
                                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                    @Override
                                    public void onSuccess(DocumentReference documentReference) {
                                        Log.d("Successful Activity Write", "Activity Written");
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.e("Error", "Error");
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