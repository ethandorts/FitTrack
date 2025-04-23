package com.example.fittrack;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.Group;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class LeaderboardStatsFragment extends Fragment {
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private String UserID = mAuth.getUid();
    private RecyclerView leaderboardRecyclerView;
    private GamificationUtil gamificationUtil = new GamificationUtil();
    private FirebaseDatabaseHelper userUtil = new FirebaseDatabaseHelper(db);
    private GroupsDatabaseUtil groupsUtil = new GroupsDatabaseUtil(db);
    private String selectedMetric;
    private boolean isWeek;
    private String GroupID;
    private String ActivityType;

    public LeaderboardStatsFragment(String selectedMetric, boolean isWeek, String GroupID, String ActivityType) {
        this.selectedMetric = selectedMetric;
        this.isWeek = isWeek;
        this.GroupID = GroupID;
        this.ActivityType = ActivityType;
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_leaderboard_stats, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        leaderboardRecyclerView = view.findViewById(R.id.recyclerLeaderboardStats);

        if(selectedMetric.equals("Distance")) {
            getLeaderboardDistanceData();
        } else if (selectedMetric.equals("1 KM")) {
            getFastestData(1000);
        } else if(selectedMetric.equals("5 KM")) {
            getFastestData(5000);
        } else if(selectedMetric.equals("10 KM")) {
            getFastestData(10000);
        } else if(selectedMetric.equals("Activity Frequency")) {
            getLeaderboardActivityFrequencyData();
        }
    }

    private void getLeaderboardDistanceData() {
        groupsUtil.retrieveUsersinGroup(GroupID, new GroupsDatabaseUtil.UsersinGroupsCallback() {
            @Override
            public void onCallback(ArrayList<String> runners) {
                List<String> userIds = new ArrayList<>(runners);
                List<Task<DocumentSnapshot>> tasks = new ArrayList<>();

                for (String runner : userIds) {
                    Task<DocumentSnapshot> t =
                            db.collection("Users")
                            .document(runner)
                            .collection("Statistics")
                            .document(isWeek ? "lastWeek" : "lastMonth")
                            .get();
                    tasks.add(t);
                }

                Tasks.whenAllSuccess(tasks)
                        .addOnSuccessListener(new OnSuccessListener<List<Object>>() {
                            @Override
                            public void onSuccess(List<Object> docs) {
                                ArrayList<LeaderboardModel> stats = new ArrayList<>();
                                for (int i = 0; i < docs.size(); i++) {
                                    DocumentSnapshot snap = (DocumentSnapshot) docs.get(i);
                                    Double distance = snap.getDouble("Distance");
                                    if (distance == null) distance = 0.0;
                                    stats.add(new LeaderboardModel(userIds.get(i), distance / 1000, null));
                                }

                                Collections.sort(stats, new Comparator<LeaderboardModel>() {
                                    @Override
                                    public int compare(LeaderboardModel a, LeaderboardModel b) {
                                        return Double.compare(b.getDistance(), a.getDistance());
                                    }
                                });
                                updateLeaderboard(stats, "Distance");
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.e("Leaderboard", "Error loading stats", e);
                            }
                        });
            }
        });
    }

    private void getLeaderboardActivityFrequencyData() {
        groupsUtil.retrieveUsersinGroup(GroupID, new GroupsDatabaseUtil.UsersinGroupsCallback() {
            @Override
            public void onCallback(ArrayList<String> runners) {
                List<String> userIds = new ArrayList<>(runners);
                List<Task<DocumentSnapshot>> tasks = new ArrayList<>();

                for (String runner : userIds) {
                    Task<DocumentSnapshot> t =
                            db.collection("Users")
                                    .document(runner)
                                    .collection("Statistics")
                                    .document(isWeek ? "lastWeek" : "lastMonth")
                                    .get();
                    tasks.add(t);
                }

                Tasks.whenAllSuccess(tasks)
                        .addOnSuccessListener(new OnSuccessListener<List<Object>>() {
                            @Override
                            public void onSuccess(List<Object> docs) {
                                ArrayList<LeaderboardModel> stats = new ArrayList<>();
                                for (int i = 0; i < docs.size(); i++) {
                                    DocumentSnapshot snap = (DocumentSnapshot) docs.get(i);
                                    Double activityFrequency = snap.getDouble("ActivityFrequency");
                                    if (activityFrequency == null) activityFrequency = 0.0;
                                    stats.add(new LeaderboardModel(userIds.get(i), activityFrequency, null));
                                }

                                Collections.sort(stats, new Comparator<LeaderboardModel>() {
                                    @Override
                                    public int compare(LeaderboardModel a, LeaderboardModel b) {
                                        return Double.compare(b.getDistance(), a.getDistance());
                                    }
                                });
                                updateLeaderboard(stats, "ActivityFrequency");
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.e("Leaderboard", "Error loading stats", e);
                            }
                        });
            }
        });
    }

    public void getFastestData(double distance) {
        groupsUtil.retrieveUsersinGroup(GroupID, new GroupsDatabaseUtil.UsersinGroupsCallback() {
            @Override
            public void onCallback(ArrayList<String> runners) {
                List<String> userIds = new ArrayList<>(runners);
                List<Task<DocumentSnapshot>> tasks = new ArrayList<>();

                for (String runner : userIds) {
                    Task<DocumentSnapshot> t =
                            db.collection("Users")
                                    .document(runner)
                                    .collection("Statistics")
                                    .document(isWeek ? "lastWeek" : "lastMonth")
                                    .get();
                    tasks.add(t);
                }

                Tasks.whenAllSuccess(tasks)
                        .addOnSuccessListener(new OnSuccessListener<List<Object>>() {
                            @Override
                            public void onSuccess(List<Object> docs) {
                                ArrayList<LeaderboardModel> stats = new ArrayList<>();

                                for (int i = 0; i < docs.size(); i++) {
                                    DocumentSnapshot snap = (DocumentSnapshot) docs.get(i);
                                    System.out.println(snap.getId());

                                    double fastest = 0;

                                    if (distance == 1000) {
                                        Object raw = snap.get("1K");
                                        fastest = parseSafeTime(raw, "1K", snap.getId());
                                    } else if (distance == 5000) {
                                        Object raw = snap.get("5K");
                                        fastest = parseSafeTime(raw, "5K", snap.getId());
                                    } else if (distance == 10000) {
                                        Object raw = snap.get("10K");
                                        fastest = parseSafeTime(raw, "10K", snap.getId());
                                    }

                                    if (fastest == -1.0) continue;
                                    stats.add(new LeaderboardModel(userIds.get(i), fastest, null));
                                }

                                Collections.sort(stats, new Comparator<LeaderboardModel>() {
                                    @Override
                                    public int compare(LeaderboardModel a, LeaderboardModel b) {
                                        return Double.compare(a.getDistance(), b.getDistance());
                                    }
                                });

                                updateLeaderboard(stats, "Time");
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.e("Leaderboard", "Error loading stats", e);
                            }
                        });
            }
        });
    }

    private double parseSafeTime(Object raw, String field, String docId) {
        if (raw instanceof Number) {
            return ((Number) raw).doubleValue();
        } else if (raw instanceof String) {
            try {
                return Double.parseDouble((String) raw);
            } catch (NumberFormatException e) {
                Log.w("Leaderboard", "Invalid string for " + field + " in " + docId + ": " + raw);
            }
        } else {
            Log.w("Leaderboard", "Missing or invalid " + field + " in " + docId);
        }
        return 0.0;
    }


    private void updateLeaderboard(ArrayList<LeaderboardModel> leaderboardStats, String selectedMetric) {
        LeaderboardRecyclerAdapter adapter = new LeaderboardRecyclerAdapter(getContext(), leaderboardStats, selectedMetric);
        leaderboardRecyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        leaderboardRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    }
}
