package com.example.fittrack;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import org.w3c.dom.Document;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class BadgesUtil {
    // Distance Badges for a Month
    // Time Badges
    // First Cycling / Running / Walking Activity

    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private String UserID = mAuth.getUid();

    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    public void checkDistanceBadges(String activityType) {
        Calendar startCalendar = Calendar.getInstance();
        startCalendar.set(Calendar.DAY_OF_MONTH, 1);

        Calendar endCalendar = Calendar.getInstance();
        endCalendar.set(Calendar.DAY_OF_MONTH, endCalendar.getActualMaximum(Calendar.DAY_OF_MONTH));


        Date startDate = startCalendar.getTime();
        Timestamp startTimestamp = new Timestamp(startDate);
        Date endDate = endCalendar.getTime();
        Timestamp endTimestamp = new Timestamp(endDate);

        Query query = db.collection("Activities")
                .whereEqualTo("UserID", UserID)
                .whereEqualTo("type", activityType)
                .whereGreaterThanOrEqualTo("date", startTimestamp)
                .whereLessThanOrEqualTo("date", endTimestamp);

        query.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot querySnapshot) {
                double monthDistance = 0.0;
                List<DocumentSnapshot> snapshots = querySnapshot.getDocuments();
                for(DocumentSnapshot snapshot :snapshots) {
                    String stringDistance = (String) snapshot.get("distance");
                    Double distance = Double.parseDouble(stringDistance);
                    monthDistance += distance;
                }
                System.out.println("Total month distance: " + monthDistance);

                DocumentReference badgesQuery = db.collection("Badges")
                        .document(new SimpleDateFormat("MMMM").format(startCalendar.getTime()) + " " + startCalendar.get(Calendar.YEAR));

                double finalMonthDistance = monthDistance;
                badgesQuery.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        List<String> userBadges  = (List<String>) documentSnapshot.get("Badges");
                        if(userBadges == null) {
                            userBadges = new ArrayList<>();
                        }
                        int badgeTarget = 25000;
                        int badgesEarned = (int) (finalMonthDistance / badgeTarget);
                        System.out.println("Badges Earned " + badgesEarned);

                        boolean BadgeAdded = false;
                        for(int i = 1; i < badgesEarned + 1; i++) {
                            String badgeName = "Ran " + (i * 25) + " KM in " +
                                    new SimpleDateFormat("MMMM").format(startCalendar.getTime()) + " " + startCalendar.get(Calendar.YEAR);
                            if(!userBadges.contains(badgeName)) {
                                userBadges.add(badgeName);
                                BadgeAdded = true;
                            }
                        }

                        if(BadgeAdded) {
                            DocumentReference docRef = db.collection("Users").document(UserID).collection("Badges")
                                    .document(new SimpleDateFormat("MMMM").format(startCalendar.getTime()) + " " + startCalendar.get(Calendar.YEAR));


                            List<String> finalUserBadges = userBadges;
                            docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                @Override
                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                    HashMap<String, Object> badgeMap = new HashMap<>();
                                    badgeMap.put("Badges", finalUserBadges);
                                    if(!documentSnapshot.exists()) {
                                                docRef.set(badgeMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void unused) {
                                                        System.out.println("On successful created");
                                                    }
                                                }).addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        System.out.println(e.getMessage());
                                                    }
                                                });
                                    } else {
                                        docRef.update("Badges", finalUserBadges)
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void unused) {
                                                        Log.d("User Badges Updated", "User Badges Successfully Updated");
                                                    }
                                                }).addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        Log.e("User Badges Failure", "User Badges Failure: " + e.getMessage());
                                                    }
                                                });
                                    }
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    System.out.println(e.getMessage());
                                }
                            });
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("Badge Retrieval Failure", e.getMessage());
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                System.out.println(e.getMessage());
            }
        });
    }
}
