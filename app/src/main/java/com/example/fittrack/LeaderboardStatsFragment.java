package com.example.fittrack;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

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
    private FirebaseDatabaseHelper userUtil = new FirebaseDatabaseHelper(db);
    private GroupsDatabaseUtil groupsUtil = new GroupsDatabaseUtil(db);
    private String selectedMetric;
    private boolean isWeek;
    private String GroupID;
    private String ActivityType;
    private TableLayout table;
    private TextView txtValue, leaderboardTitle;

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
        table = view.findViewById(R.id.leaderboardTable);
        leaderboardTitle = view.findViewById(R.id.leaderboardTitle);
        txtValue = view.findViewById(R.id.ValueType);

        if (selectedMetric.equals("Distance")) {
            getLeaderboardDistanceData();
        } else if (selectedMetric.equals("1 KM")) {
            getFastestData(1000);
        } else if (selectedMetric.equals("5 KM")) {
            getFastestData(5000);
        } else if (selectedMetric.equals("10 KM")) {
            getFastestData(10000);
        } else if (selectedMetric.equals("Activity Frequency")) {
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
                    Task<DocumentSnapshot> t = db.collection("Users")
                            .document(runner)
                            .collection("Statistics")
                            .document(isWeek ? ActivityType + "_lastWeek" : ActivityType + "_lastMonth")
                            .get();
                    tasks.add(t);
                }

                Tasks.whenAllSuccess(tasks).addOnSuccessListener(new OnSuccessListener<List<Object>>() {
                    @Override
                    public void onSuccess(List<Object> documents) {
                        ArrayList<LeaderboardModel> stats = new ArrayList<>();
                        for (int i = 0; i < documents.size(); i++) {
                            DocumentSnapshot snapshot = (DocumentSnapshot) documents.get(i);
                            Double distance = snapshot.getDouble("Distance");
                            if (distance == null) distance = 0.0;
                            stats.add(new LeaderboardModel(userIds.get(i), distance / 1000, null));
                        }
                        System.out.println(stats.size() + " Stats Size");
                        updateLeaderboard(stats, "Distance");
                    }
                }).addOnFailureListener(new OnFailureListener() {
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
                    Task<DocumentSnapshot> t = db.collection("Users")
                            .document(runner)
                            .collection("Statistics")
                            .document(isWeek ? ActivityType + "_lastWeek" : ActivityType + "_lastMonth")
                            .get();
                    tasks.add(t);
                }

                Tasks.whenAllSuccess(tasks).addOnSuccessListener(new OnSuccessListener<List<Object>>() {
                    @Override
                    public void onSuccess(List<Object> documents) {
                        ArrayList<LeaderboardModel> stats = new ArrayList<>();
                        for (int i = 0; i < documents.size(); i++) {
                            DocumentSnapshot snapshot = (DocumentSnapshot) documents.get(i);
                            Double activityFrequency = snapshot.getDouble("ActivityFrequency");
                            if (activityFrequency == null) activityFrequency = 0.0;
                            stats.add(new LeaderboardModel(userIds.get(i), activityFrequency, null));
                            System.out.println(stats.size());
                            System.out.println(stats.get(i).getUsername());
                            updateLeaderboard(stats, "ActivityFrequency");
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
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
                    Task<DocumentSnapshot> t = db.collection("Users")
                            .document(runner)
                            .collection("Statistics")
                            .document(isWeek ? ActivityType + "_lastWeek" : ActivityType + "_lastMonth")
                            .get();
                    tasks.add(t);
                }

                Tasks.whenAllSuccess(tasks).addOnSuccessListener(new OnSuccessListener<List<Object>>() {
                    @Override
                    public void onSuccess(List<Object> docs) {
                        ArrayList<LeaderboardModel> stats = new ArrayList<>();
                        for (int i = 0; i < docs.size(); i++) {
                            DocumentSnapshot snapshot = (DocumentSnapshot) docs.get(i);
                            double fastest = 0;
                            if (distance == 1000) {
                                fastest = parseSafeTime(snapshot.get("1K"));
                            } else if (distance == 5000) {
                                fastest = parseSafeTime(snapshot.get("5K"));
                            } else if (distance == 10000) {
                                fastest = parseSafeTime(snapshot.get("10K"));
                            }
                            stats.add(new LeaderboardModel(userIds.get(i), fastest, null));
                            System.out.println(stats.get(i).getDistance());
                            updateLeaderboard(stats, "Time");
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("Leaderboard", "Error loading stats", e);
                    }
                });
            }
        });
    }

    private double parseSafeTime(Object raw) {
        if (raw instanceof Number) {
            return ((Number) raw).doubleValue();
        } else if (raw instanceof String) {
            try {
                return Double.parseDouble((String) raw);
            } catch (NumberFormatException e) {
                return 0.0;
            }
        } else {
            return 0.0;
        }
    }

//    private void fetchUsernamesAndUpdate(final ArrayList<LeaderboardModel> stats, final String selectedMetric) {
//        if (selectedMetric.equals("Time")) {
//            Collections.sort(stats, new Comparator<LeaderboardModel>() {
//                @Override
//                public int compare(LeaderboardModel a, LeaderboardModel b) {
//                    return Double.compare(a.getDistance(), b.getDistance());
//                }
//            });
//        } else {
//            Collections.sort(stats, new Comparator<LeaderboardModel>() {
//                @Override
//                public int compare(LeaderboardModel a, LeaderboardModel b) {
//                    return Double.compare(b.getDistance(), a.getDistance());
//                }
//            });
//        }
//
//        updateLeaderboard(stats, selectedMetric);
//    }

    private void updateLeaderboard(ArrayList<LeaderboardModel> leaderboardStats, String selectedMetric) {
        table.removeViews(1, Math.max(0, table.getChildCount() - 1));

        // Update leaderboard title and column header
        if (leaderboardTitle != null) {
            switch (selectedMetric) {
                case "Time":
                    leaderboardTitle.setText("🏆 Fastest Time Leaderboard");
                    txtValue.setText("Time");
                    Collections.sort(leaderboardStats, Comparator.comparingDouble(LeaderboardModel::getDistance));
                    break;
                case "ActivityFrequency":
                    leaderboardTitle.setText("🏆 Frequency Leaderboard");
                    txtValue.setText("Frequency");
                    Collections.sort(leaderboardStats, (a, b) -> Double.compare(b.getDistance(), a.getDistance()));
                    break;
                case "Distance":
                    leaderboardTitle.setText("🏆 Distance Leaderboard");
                    txtValue.setText("Distance (KM)");
                    Collections.sort(leaderboardStats, (a, b) -> Double.compare(b.getDistance(), a.getDistance()));
                    break;
                default:
                    leaderboardTitle.setText("🏆 " + selectedMetric + " Leaderboard");
                    txtValue.setText(selectedMetric);
                    Collections.sort(leaderboardStats, (a, b) -> Double.compare(b.getDistance(), a.getDistance()));
            }
        }

        int rank = 1;
        for (LeaderboardModel stats : leaderboardStats) {
            if (stats.getDistance() == -1.0) continue;

            TableRow row = new TableRow(getContext());

            TextView rankView = new TextView(getContext());
            TextView nameView = new TextView(getContext());
            TextView valueView = new TextView(getContext());

            rankView.setText(String.valueOf(rank++));
            rankView.setGravity(Gravity.CENTER);
            rankView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

            userUtil.retrieveChatName(stats.getUsername(), new FirebaseDatabaseHelper.ChatUserCallback() {
                @Override
                public void onCallback(String chatName) {
                    nameView.setText(chatName != null ? chatName : stats.getUsername());
                    nameView.setGravity(Gravity.CENTER);
                    nameView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                }
            });


            String value;
            if (selectedMetric.equals("Time")) {
                value = longToTimeConversion((long) stats.getDistance());
            } else if (selectedMetric.equals("Activity Frequency")) {
                value = String.format("%.0f", stats.getDistance());
            } else {
                value = String.format("%.2f", stats.getDistance());
            }

            valueView.setText(value);
            valueView.setGravity(Gravity.CENTER);
            valueView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

            float textSize = 18f;
            int padding = 12;

            rankView.setTextSize(textSize);
            nameView.setTextSize(textSize);
            valueView.setTextSize(textSize);

            rankView.setPadding(padding, padding, padding, padding);
            nameView.setPadding(padding, padding, padding, padding);
            valueView.setPadding(padding, padding, padding, padding);

            row.addView(rankView);
            row.addView(nameView);
            row.addView(valueView);

            table.addView(row);
        }
    }

    private String longToTimeConversion(long longValue) {
        long milliseconds = longValue % 1000;
        longValue = longValue / 1000;
        long hours = longValue / 3600;
        long minutes = (longValue % 3600) / 60;
        long seconds = longValue % 60;

        if (hours > 0) {
            return String.format("%02d:%02d:%02d", hours, minutes, seconds);
        } else {
            return String.format("%02d:%02d", minutes, seconds);
        }
    }


}
