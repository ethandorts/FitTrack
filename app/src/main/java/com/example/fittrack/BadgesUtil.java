package com.example.fittrack;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Query;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CountDownLatch;

public class BadgesUtil {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private String UserID = FirebaseAuth.getInstance().getUid();

    public List<String> collectDistanceBadges(List<String> activityTypes) {
        List<String> earned = new ArrayList<>();
        CountDownLatch latch = new CountDownLatch(activityTypes.size());

        Calendar start = Calendar.getInstance();
        start.set(Calendar.DAY_OF_MONTH, 1);

        Calendar end = Calendar.getInstance();
        end.set(Calendar.DAY_OF_MONTH, end.getActualMaximum(Calendar.DAY_OF_MONTH));

        Timestamp startTs = new Timestamp(start.getTime());
        Timestamp endTs = new Timestamp(end.getTime());

        for (String type : activityTypes) {
            db.collection("Activities")
                    .whereEqualTo("UserID", UserID)
                    .whereEqualTo("type", type)
                    .whereGreaterThanOrEqualTo("date", startTs)
                    .whereLessThanOrEqualTo("date", endTs)
                    .get()
                    .addOnSuccessListener(new com.google.android.gms.tasks.OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot querySnapshot) {
                            double total = 0.0;
                            for (QueryDocumentSnapshot doc : querySnapshot) {
                                try {
                                    total += Double.parseDouble(String.valueOf(doc.get("distance")));
                                } catch (Exception ignored) {}
                            }

                            int badgeTarget = 25000;
                            int earnedBadges = (int) (total / badgeTarget);

                            String month = new SimpleDateFormat("MMMM yyyy", Locale.getDefault()).format(start.getTime());
                            for (int i = 1; i <= earnedBadges; i++) {
                                earned.add("Ran " + (i * 25) + " KM in " + month);
                            }

                            latch.countDown();
                        }
                    })
                    .addOnFailureListener(new com.google.android.gms.tasks.OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            latch.countDown();
                        }
                    });
        }

        try {
            latch.await();
        } catch (InterruptedException ignored) {}

        return earned;
    }

    public List<String> collectFirstDistanceBadges(String activityType) {
        List<String> earned = new ArrayList<>();
        double[] thresholds = {1000, 5000, 21100, 42200};

        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_MONTH, 1);

        String month = new SimpleDateFormat("MMMM yyyy", Locale.getDefault()).format(cal.getTime());

        CountDownLatch latch = new CountDownLatch(1);

        db.collection("Activities")
                .whereEqualTo("UserID", UserID)
                .whereEqualTo("type", activityType)
                .whereGreaterThanOrEqualTo("date", new Timestamp(cal.getTime()))
                .get()
                .addOnSuccessListener(new com.google.android.gms.tasks.OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot snapshot) {
                        for (QueryDocumentSnapshot doc : snapshot) {
                            try {
                                double dist = Double.parseDouble(String.valueOf(doc.get("distance")));
                                for (double threshold : thresholds) {
                                    if (dist >= threshold) {
                                        earned.add(getBadgeText(threshold, activityType, month));
                                    }
                                }
                            } catch (Exception ignored) {}
                        }
                        latch.countDown();
                    }
                })
                .addOnFailureListener(new com.google.android.gms.tasks.OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        latch.countDown();
                    }
                });

        try {
            latch.await();
        } catch (InterruptedException ignored) {}

        return earned;
    }

    public List<String> collectTimeBadges() {
        List<String> earned = new ArrayList<>();

        Calendar start = Calendar.getInstance();
        start.set(Calendar.DAY_OF_MONTH, 1);
        Timestamp startTs = new Timestamp(start.getTime());

        Calendar end = Calendar.getInstance();
        end.set(Calendar.DAY_OF_MONTH, end.getActualMaximum(Calendar.DAY_OF_MONTH));
        Timestamp endTs = new Timestamp(end.getTime());

        CountDownLatch latch = new CountDownLatch(1);

        db.collection("Activities")
                .whereEqualTo("UserID", UserID)
                .whereGreaterThanOrEqualTo("date", startTs)
                .whereLessThanOrEqualTo("date", endTs)
                .get()
                .addOnSuccessListener(new com.google.android.gms.tasks.OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot snapshot) {
                        String month = new SimpleDateFormat("MMMM yyyy", Locale.getDefault()).format(start.getTime());
                        for (QueryDocumentSnapshot doc : snapshot) {
                            try {
                                long time = Long.parseLong(String.valueOf(doc.get("time")));
                                if (time >= 1800) earned.add("Completed a Fitness Activity of over 30 minutes in " + month);
                                if (time >= 2700) earned.add("Completed a Fitness Activity of over 45 minutes in " + month);
                                if (time >= 3600) earned.add("Completed a Fitness Activity of over 1 hour in " + month);
                                if (time >= 7200) earned.add("Completed a Fitness Activity of over 2 hours in " + month);
                            } catch (Exception ignored) {}
                        }
                        latch.countDown();
                    }
                })
                .addOnFailureListener(new com.google.android.gms.tasks.OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        latch.countDown();
                    }
                });

        try {
            latch.await();
        } catch (InterruptedException ignored) {}

        return earned;
    }

    public String getFirstActivityBadge() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_MONTH, 1);

        Timestamp start = new Timestamp(cal.getTime());

        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
        Timestamp end = new Timestamp(cal.getTime());

        CountDownLatch latch = new CountDownLatch(1);
        final boolean[] hasActivity = {false};

        db.collection("Activities")
                .whereEqualTo("UserID", UserID)
                .whereGreaterThanOrEqualTo("date", start)
                .whereLessThanOrEqualTo("date", end)
                .get()
                .addOnSuccessListener(new com.google.android.gms.tasks.OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot snapshot) {
                        hasActivity[0] = !snapshot.isEmpty();
                        latch.countDown();
                    }
                })
                .addOnFailureListener(new com.google.android.gms.tasks.OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        latch.countDown();
                    }
                });

        try {
            latch.await();
        } catch (InterruptedException ignored) {}

        if (hasActivity[0]) {
            return "Completed First Fitness Activity for " + new SimpleDateFormat("MMMM yyyy", Locale.getDefault()).format(new Date());
        } else {
            return null;
        }
    }

    public void retrieveUserBadges(String UserID, BadgesCallback callback) {
        String month = new SimpleDateFormat("MMMM yyyy").format(Calendar.getInstance().getTime());

        db.collection("Users")
                .document(UserID)
                .collection("Badges")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot querySnapshot) {
                        List<String> allBadges = new ArrayList<>();
                        for(DocumentSnapshot document : querySnapshot.getDocuments()) {
                            List<String> badges = (List<String>) document.get("Badges");
                            if (badges != null) {
                                allBadges.addAll(badges);
                            }
                        }

                        callback.onCallback(allBadges);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("Badges Retrieval Error: ", e.getMessage());
                    }
                });
    }

    private String getBadgeText(double dist, String type, String month) {
        if (dist == 5000) return "Completed a 5KM " + type + " Activity in " + month;
        if (dist == 10000) return "Completed a 10KM " + type + " Activity in " + month;
        if (dist == 21100) return "Completed a Half Marathon " + type + " Activity in " + month;
        if (dist == 42200) return "Completed a Marathon " + type + " Activity in " + month;
        return "Completed a " + (int)(dist / 1000) + "KM " + type + " Activity in " + month;
    }

    public interface BadgesCallback {
        void onCallback(List<String> badges);
    }
}
