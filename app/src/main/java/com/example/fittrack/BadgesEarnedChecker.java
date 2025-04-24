package com.example.fittrack;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.firestore.DocumentSnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.HashMap;
import java.util.Map;

public class BadgesEarnedChecker extends Worker {

    private BadgesUtil badgesUtil = new BadgesUtil();

    public BadgesEarnedChecker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        Log.d("Badge Checker", "Running Badge Check");

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String userId = FirebaseAuth.getInstance().getUid();

        Calendar calendar = Calendar.getInstance();
        String monthId = new SimpleDateFormat("MMMM yyyy", Locale.getDefault()).format(calendar.getTime());
        DocumentReference badgeDocRef = db.collection("Users").document(userId).collection("Badges").document(monthId);

        List<String> activityTypes = new ArrayList<>(Arrays.asList("Running", "Walking", "Cycling"));
        List<String> existingBadges = new ArrayList<>();
        Set<String> combinedBadges = new HashSet<>();

        try {
            DocumentSnapshot badgeDoc = com.google.android.gms.tasks.Tasks.await(badgeDocRef.get());
            if (badgeDoc.exists() && badgeDoc.contains("Badges")) {
                existingBadges = (List<String>) badgeDoc.get("Badges");
                if (existingBadges == null) {
                    existingBadges = new ArrayList<>();
                }
            }

            combinedBadges.addAll(existingBadges);
            combinedBadges.addAll(badgesUtil.collectDistanceBadges(activityTypes));

            for (String type : activityTypes) {
                combinedBadges.addAll(badgesUtil.collectFirstDistanceBadges(type));
            }

            combinedBadges.addAll(badgesUtil.collectTimeBadges());

            String firstActivityBadge = badgesUtil.getFirstActivityBadge();
            if (firstActivityBadge != null) {
                combinedBadges.add(firstActivityBadge);
            }

            Set<String> newlyEarned = new HashSet<>(combinedBadges);
            newlyEarned.removeAll(existingBadges);
            System.out.println(newlyEarned.size());

            if (!combinedBadges.equals(new HashSet<String>(existingBadges))) {
                Map<String, Object> update = new HashMap<>();
                update.put("Badges", new ArrayList<>(combinedBadges));

                badgeDocRef.set(update, SetOptions.merge());

                NotificationUtil.createBadgeAchievedNotificationChannel(getApplicationContext());

                for (String badge : newlyEarned) {
                    NotificationUtil.showBadgeAchievedNotification(getApplicationContext(), badge);
                }

                Log.d("Badge Checker", "Badges updated.");
            }

            Log.d("Badges Process Finished", "Badges Process Finished");

            return Result.success();

        } catch (Exception e) {
            Log.e("Badge Checker", "Failed to process badges: " + e.getMessage());
            return Result.failure();
        }
    }
}
