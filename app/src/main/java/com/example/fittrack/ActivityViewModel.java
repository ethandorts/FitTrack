package com.example.fittrack;

import android.content.Context;
import android.view.View;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class ActivityViewModel extends ViewModel {
    private MutableLiveData<ArrayList<ActivityModel>> UserActivities = new MutableLiveData<>();
    private MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private MutableLiveData<Boolean> isEndofArray = new MutableLiveData<>(false);
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private String UserID = mAuth.getUid();
    private String GroupID;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseDatabaseHelper DatabaseUtil = new FirebaseDatabaseHelper(db);
    private GroupsDatabaseUtil GroupsUtil = new GroupsDatabaseUtil(db);
    private DocumentSnapshot lastVisible = null;

    public ActivityViewModel() {
        loadUserActivities();
    }

    public MutableLiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public MutableLiveData<Boolean> getIsEndofArray() {
        return isEndofArray;
    }

    public MutableLiveData<ArrayList<ActivityModel>> getUserActivities() {
        return UserActivities;
    }

    public void loadUserActivities() {
        if(Boolean.TRUE.equals(isLoading.getValue()) || Boolean.TRUE.equals(isEndofArray.getValue())) {
            return;
        }

        isLoading.setValue(true);
        DatabaseUtil.retrieveUserActivities(UserID, new FirebaseDatabaseHelper.FirestoreActivitiesCallback() {
            @Override
            public void onCallback(List<Map<String, Object>> data, DocumentSnapshot lastItemVisible) {
                if (data == null || data.isEmpty()) {
                    isEndofArray.setValue(true);
                    isLoading.setValue(false);
                    return;
                }
                HashSet<ActivityModel> singleActivities = new HashSet<>(UserActivities.getValue() != null ? UserActivities.getValue() : new ArrayList<>());
                for (Map<String, Object> activity : data) {
                    ActivityModel activityInfo = new ActivityModel(
                            String.valueOf(activity.get("type")),
                            String.valueOf(activity.get("typeImage")),
                            (Timestamp) activity.get("date"),
                            String.valueOf(activity.get("distance")),
                            formatRunTime(Double.parseDouble(String.valueOf(activity.get("time")))),
                            String.valueOf(activity.get("pace")),
                            "",
                            String.valueOf(activity.get("UserImage")),
                            (List<Object>) activity.get("activityCoordinates")
                    );
                    singleActivities.add(activityInfo);
                }
                UserActivities.setValue(new ArrayList<>(singleActivities));
                lastVisible = lastItemVisible;
                isLoading.setValue(false);
            }
        }, lastVisible);
    }

    public void loadGroupActivities() {
        if (Boolean.TRUE.equals(isLoading.getValue()) || Boolean.TRUE.equals(isEndofArray.getValue())) {
            return;
        }

        isLoading.setValue(true);

        GroupsUtil.retrieveUsersinGroup("Sg8JLYf9lpE1akjQRHBv", new GroupsDatabaseUtil.UsersinGroupsCallback() {
            @Override
            public void onCallback(ArrayList<String> runners) {
                AtomicInteger count = new AtomicInteger(0);
                System.out.println(runners);
                HashSet<ActivityModel> singleActivities = new HashSet<>(UserActivities.getValue() != null ? UserActivities.getValue() : new ArrayList<>());
                for(String runner : runners) {
                    System.out.println("Runner: " + runner);
                    DatabaseUtil.retrieveUserActivities(runner, new FirebaseDatabaseHelper.FirestoreActivitiesCallback() {
                        @Override
                        public void onCallback(List<Map<String, Object>> data, DocumentSnapshot lastVisible) {
                            if(data != null) {
                                for (Map<String, Object> activity : data) {
                                    System.out.println("Map: " + activity);
//                                    ActivityModel activityInfo = new ActivityModel(
//                                            String.valueOf(activity.get("type")),
//                                            String.valueOf(activity.get("typeImage")),
//                                            (Timestamp) activity.get("date"),
//                                            String.valueOf(activity.get("distance")),
//                                            formatRunTime(Double.parseDouble(String.valueOf(activity.get("time")))),
//                                            String.valueOf(activity.get("pace")),
//                                            "",
//                                            String.valueOf(activity.get("UserImage")),
//                                            (List<Object>) activity.get("activityCoordinates")
//                                    );
                                    //singleActivities.add(activityInfo);
                                }
                            }
                            System.out.println("Group Activities: " + singleActivities.size());
//                            if(count.incrementAndGet() == runners.size()) {
//                                UserActivities.setValue(new ArrayList<>(singleActivities));
//                                isLoading.setValue(false);
//                            }
                        }
                    }, lastVisible);
                }
            }
        });
    }

    private String formatRunTime(double timePassed) {
        int hours = (int) (timePassed / 3600);
        int minutes = (int) ((timePassed % 3600) / 60);
        int seconds = (int) (timePassed % 60);
        String formattedRunTime = String.format("%02d:%02d:%02d", hours, minutes, seconds);

        return formattedRunTime;
    }
}
