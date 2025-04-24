package com.example.fittrack;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.firestore.WriteBatch;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class UpdateStats extends Worker {
    private static final String TAG = "UpdateStats";
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final String userId = FirebaseAuth.getInstance().getUid();
    private final GamificationUtil gamUtil = new GamificationUtil();

    public UpdateStats(@NonNull Context context, @NonNull WorkerParameters params) {
        super(context, params);
    }

    @NonNull
    @Override
    public Result doWork() {
        List<String> activityTypes = Arrays.asList("Running", "Cycling", "Walking");
        WriteBatch batch = db.batch();
        CountDownLatch finalLatch = new CountDownLatch(activityTypes.size());

        for (String type : activityTypes) {
            updateStatsForType(type, batch, finalLatch);
        }

        try {
            if (!finalLatch.await(3, TimeUnit.MINUTES)) {
                Log.e(TAG, "Timed out waiting for all type-specific stats");
                return Result.retry();
            }
        } catch (InterruptedException e) {
            Log.e(TAG, "Interrupted while waiting for all type stats", e);
            return Result.retry();
        }

        final AtomicBoolean writeSuccess = new AtomicBoolean(true);
        final CountDownLatch writeLatch = new CountDownLatch(1);

        batch.commit()
                .addOnSuccessListener(unused -> writeLatch.countDown())
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Batch write failed", e);
                    writeSuccess.set(false);
                    writeLatch.countDown();
                });

        try {
            if (!writeLatch.await(1, TimeUnit.MINUTES) || !writeSuccess.get()) {
                return Result.retry();
            }
        } catch (InterruptedException e) {
            return Result.retry();
        }

        return Result.success();
    }

    private void updateStatsForType(String type, WriteBatch batch, CountDownLatch finalLatch) {
        AtomicReference<Double> weeklyDistance = new AtomicReference<>(0.0);
        AtomicReference<Double> monthlyDistance = new AtomicReference<>(0.0);
        AtomicInteger weeklyFreq = new AtomicInteger(0);
        AtomicInteger monthlyFreq = new AtomicInteger(0);
        AtomicInteger weeklyFast1k = new AtomicInteger(-1);
        AtomicInteger weeklyFast5k = new AtomicInteger(-1);
        AtomicInteger weeklyFast10k = new AtomicInteger(-1);
        AtomicInteger monthlyFast1k = new AtomicInteger(-1);
        AtomicInteger monthlyFast5k = new AtomicInteger(-1);
        AtomicInteger monthlyFast10k = new AtomicInteger(-1);

        CountDownLatch latch = new CountDownLatch(10);

        fetchFastestSplit(type, 1000, true, new FastestHandler(weeklyFast1k, latch));
        fetchFastestSplit(type, 5000, true, new FastestHandler(weeklyFast5k, latch));
        fetchFastestSplit(type, 10000, true, new FastestHandler(weeklyFast10k, latch));
        fetchFastestSplit(type, 1000, false, new FastestHandler(monthlyFast1k, latch));
        fetchFastestSplit(type, 5000, false, new FastestHandler(monthlyFast5k, latch));
        fetchFastestSplit(type, 10000, false, new FastestHandler(monthlyFast10k, latch));

        gamUtil.calculateUserDistancePerWeek(userId, type, distance -> {
            weeklyDistance.set(distance);
            latch.countDown();
        });
        gamUtil.calculateUserDistancePerMonth(userId, type, distance -> {
            monthlyDistance.set(distance);
            latch.countDown();
        });
        gamUtil.calculateUserActivitiesPerWeek(userId, type, count -> {
            weeklyFreq.set(count);
            latch.countDown();
        });
        gamUtil.calculateUserActivitiesPerMonth(userId, type, count -> {
            monthlyFreq.set(count);
            latch.countDown();
        });

        new Thread(() -> {
            try {
                if (!latch.await(2, TimeUnit.MINUTES)) {
                    Log.e(TAG, "Timeout for type: " + type);
                    finalLatch.countDown();
                    return;
                }

                Map<String, Object> weekStats = new HashMap<>();
                weekStats.put("Distance", weeklyDistance.get());
                weekStats.put("ActivityFrequency", weeklyFreq.get());
                weekStats.put("1K", weeklyFast1k.get());
                weekStats.put("5K", weeklyFast5k.get());
                weekStats.put("10K", weeklyFast10k.get());

                Map<String, Object> monthStats = new HashMap<>();
                monthStats.put("Distance", monthlyDistance.get());
                monthStats.put("ActivityFrequency", monthlyFreq.get());
                monthStats.put("1K", monthlyFast1k.get());
                monthStats.put("5K", monthlyFast5k.get());
                monthStats.put("10K", monthlyFast10k.get());

                DocumentReference weekRef = db.collection("Users").document(userId)
                        .collection("Statistics").document(type + "_lastWeek");
                DocumentReference monthRef = db.collection("Users").document(userId)
                        .collection("Statistics").document(type + "_lastMonth");

                batch.set(weekRef, weekStats, SetOptions.merge());
                batch.set(monthRef, monthStats, SetOptions.merge());

            } catch (InterruptedException e) {
                Log.e(TAG, "Interrupted inside updateStatsForType: " + type, e);
            } finally {
                finalLatch.countDown();
            }
        }).start();
    }

    private void fetchFastestSplit(String activityType, double distanceMeters, boolean isWeek, GamificationUtil.FastestTimeCallback callbackWrapper) {
        ArrayList<String> single = new ArrayList<>(1);
        single.add(userId);

        gamUtil.collectDistanceTime(
                single,
                activityType,
                distanceMeters,
                isWeek,
                callbackWrapper
        );
    }

    private class FastestHandler implements GamificationUtil.FastestTimeCallback {
        private final AtomicInteger target;
        private final CountDownLatch latch;

        FastestHandler(AtomicInteger target, CountDownLatch latch) {
            this.target = target;
            this.latch = latch;
        }

        @Override
        public void onCallback(ArrayList<LeaderboardModel> timeData) {
            if (timeData != null && !timeData.isEmpty()) {
                target.set((int) timeData.get(0).getDistance());
            } else {
                target.set(-1);
            }
            latch.countDown();
        }
    }
}
