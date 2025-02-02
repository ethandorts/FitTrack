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
import com.google.firebase.firestore.SetOptions;

import org.w3c.dom.Document;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

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

    public void checkFirstDistanceBadges(String activityType, double targetDistance) {
        String distanceBadge = " ";
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

        String month = new SimpleDateFormat("MMMM").format(startCalendar.getTime()) + " " + startCalendar.get(Calendar.YEAR);
        DocumentReference docRef = db.collection("Users").document(UserID).collection("Badges").document(month);

        query.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot querySnapshot) {
                final String[] distanceBadge = {" "};
                AtomicReference<List<String>> badgeListRef = new AtomicReference<>(new ArrayList<>());
                docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if(documentSnapshot.exists() && documentSnapshot.contains("Badges")) {
                            List<String> badgesList = (List<String>) documentSnapshot.get("Badges");
                            if(badgesList != null) {
                                badgeListRef.set(badgesList);
                            }
                        }

                        boolean distancePassed = false;
                        if(activityType == "Running") {
                            distanceBadge[0] = getRunningDistanceBadgeMessage(targetDistance, "Running");
                        } else {
                            distanceBadge[0] = "Completed a " + targetDistance + " KM " + activityType + " Activity";
                        }

                        for(DocumentSnapshot document : querySnapshot) {
                            String stringDistance = (String) document.get("distance");
                            double distance = Double.parseDouble(stringDistance);
                            if(distance >= targetDistance) {
                                if(!badgeListRef.get().contains(distanceBadge[0])) {
                                    badgeListRef.get().add(distanceBadge[0]);
                                    distancePassed = true;
                                }
                            }
                        }

                        if(distancePassed) {
                            Map<String, Object> dataToAdd = new HashMap<>();
                            dataToAdd.put("Badges", badgeListRef.get());
                            docRef.set(dataToAdd, SetOptions.merge())
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            System.out.println("Specific Distance Passed");
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            System.out.println("Specific Distance Failed");
                                        }
                                    });
                        }
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

    public void checkFirstActivityofMonth() {

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
                .whereGreaterThanOrEqualTo("date", startTimestamp)
                .whereLessThanOrEqualTo("date", endTimestamp);

        query.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot querySnapshot) {
                if(!querySnapshot.isEmpty()) {
                    db.collection("Users")
                            .document(UserID)
                            .collection("Badges")
                            .document("January 2025")
                            .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                @Override
                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                    HashMap<String, Object> badgeMap = new HashMap<>();
                                    List<String> badges = new ArrayList<>();
                                    badges.add("Completed First Fitness Activity for February 2025");
                                    badgeMap.put("Badges", new ArrayList<>());
                                    if(!documentSnapshot.exists()) {
                                        db.collection("Users").document(UserID).collection("Badges")
                                                .document(new SimpleDateFormat("MMMM")
                                                .format(startCalendar.getTime()) + " " + startCalendar.get(Calendar.YEAR))
                                                .set(badgeMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void unused) {

                                                    }
                                                }).addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {

                                                    }
                                                });
                                    }
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {

                                }
                            });
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }

    public void checkActivityTime(int time) {
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
                .whereGreaterThanOrEqualTo("time", time)
                .whereGreaterThanOrEqualTo("date", startTimestamp)
                .whereLessThanOrEqualTo("date", endTimestamp);

        query.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot querySnapshot) {
                if(!querySnapshot.isEmpty()) {
                    String month = new SimpleDateFormat("MMMM").format(startCalendar.getTime()) + " " + startCalendar.get(Calendar.YEAR);
                    DocumentReference docRef = db.collection("Users").document(UserID).collection("Badges").document(month);

                    docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            List<String> badgeList;
                            if (documentSnapshot.exists() && documentSnapshot.contains("Badges")) {
                                badgeList = (List<String>) documentSnapshot.get("Badges");
                                if(badgeList == null) {
                                    badgeList = new ArrayList<>();
                                }
                            } else {
                                badgeList = new ArrayList<>();
                            }

                            String firstBadge = "First Fitness Activity Completed for: " + month;
                            if(!badgeList.contains(firstBadge)) {
                                badgeList.add(firstBadge);
                            }
                            String timeBadge = "Completed a Fitness Activity of over 45 minutes";
                            if(!badgeList.contains(timeBadge)) {
                                badgeList.add(timeBadge);
                            }

                            if(!badgeList.isEmpty()) {
                                Map<String, Object> dataToAdd = new HashMap<>();
                                dataToAdd.put("Badges", badgeList);
                                docRef.set(dataToAdd, SetOptions.merge())
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                System.out.println("Badges updated successfully");
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                System.out.println("Error updating badges: " + e.getMessage());
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
                System.out.println(e.getMessage());
            }
        });
    }



    public void retrieveUserBadges(String UserID, BadgesCallback callback) {
        DocumentReference docRef = db.collection("Users")
                .document(UserID)
                .collection("Badges")
                .document("January 2025");

        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                List<String> badgesList = (List<String>) documentSnapshot.get("Badges");
                callback.onCallback(badgesList);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e("Failure to retrieve user badges list", e.getMessage());
            }
        });
    }

    private String getRunningDistanceBadgeMessage(double distance, String activityType) {
        if(distance == 5000) {
            return "Completed a 5KM " + activityType + " Activity";
        } else if(distance == 10000) {
            return "Completed a 10KM " + activityType + " Activity";
        } else if(distance == 21100) {
            return "Completed a Half Marathon " + activityType + " Activity";
        } else if(distance == 42200) {
            return "Completed a Marathon " + activityType + " Activity";
        }
        return "Completed a " + distance + "KM " + activityType + " Activity";
    }

    public interface BadgesCallback {
        void onCallback(List<String> badges);
    }
}
