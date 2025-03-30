package com.example.fittrack;

import android.os.Bundle;
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

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class LeaderboardStatsFragment extends Fragment {
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
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
                ArrayList<LeaderboardModel> leaderboardStats = new ArrayList<>();
                int runnersSize = runners.size();
                int[] totalCallbacks = {0};

                for (String runner : runners) {
                    System.out.println(runner);
                    if(isWeek) {
                        System.out.println("Name is: " + runner);
                        gamificationUtil.calculateUserDistancePerWeek(runner, ActivityType, new GamificationUtil.UserDistancePerMonthCallback() {
                            @Override
                            public void onCallback(double distancePerWeek) {
                                leaderboardStats.add(new LeaderboardModel(runner, distancePerWeek, null));
                                totalCallbacks[0]++;

                                if (totalCallbacks[0] == runnersSize) {
                                    Collections.sort(leaderboardStats, new Comparator<LeaderboardModel>() {
                                        @Override
                                        public int compare(LeaderboardModel a, LeaderboardModel b) {
                                            System.out.println("A: " + a.getDistance() + " | B: " + b.getDistance());
                                            return Double.compare(b.getDistance(), a.getDistance());
                                        }
                                    });
                                    updateLeaderboard(leaderboardStats);
                                }
                            }
                        });
                    } else {
                        gamificationUtil.calculateUserDistancePerMonth(runner, ActivityType,  new GamificationUtil.UserDistancePerMonthCallback() {
                            @Override
                            public void onCallback(double distancePerMonth) {
                                leaderboardStats.add(new LeaderboardModel(runner, distancePerMonth, null));
                                totalCallbacks[0]++;

                                if (totalCallbacks[0] == runnersSize) {
                                    Collections.sort(leaderboardStats, new Comparator<LeaderboardModel>() {
                                        @Override
                                        public int compare(LeaderboardModel a, LeaderboardModel b) {
                                            System.out.println("A: " + a.getDistance() + " | B: " + b.getDistance());
                                            return Double.compare(b.getDistance(), a.getDistance());
                                        }
                                    });
                                    updateLeaderboard(leaderboardStats);
                                }
                            }
                        });
                    }
                }
            }
        });
    }

    private void getLeaderboardActivityFrequencyData() {
        groupsUtil.retrieveUsersinGroup(GroupID, new GroupsDatabaseUtil.UsersinGroupsCallback() {
            @Override
            public void onCallback(ArrayList<String> runners) {
                ArrayList<LeaderboardModel> leaderboardStats = new ArrayList<>();
                int runnersSize = runners.size();
                int[] totalCallbacks = {0};

                for (String runner : runners) {
                    System.out.println(runner);
                    if(isWeek) {
                        System.out.println("Name is: " + runner);
                        gamificationUtil.calculateUserActivitiesPerWeek(runner, ActivityType, new GamificationUtil.ActivityFrequencyCallback() {
                            @Override
                            public void onCallback(int activityNumber) {
                                System.out.println("Activity No Per Week: " + activityNumber);
                                leaderboardStats.add(new LeaderboardModel(runner, activityNumber, null));
                                totalCallbacks[0]++;

                                if (totalCallbacks[0] == runnersSize) {
                                    Collections.sort(leaderboardStats, new Comparator<LeaderboardModel>() {
                                        @Override
                                        public int compare(LeaderboardModel a, LeaderboardModel b) {
                                            System.out.println("A: " + a.getDistance() + " | B: " + b.getDistance());
                                            return Double.compare(b.getDistance(), a.getDistance());
                                        }
                                    });
                                    updateLeaderboard(leaderboardStats);
                                }
                            }
                        });
                    } else {
                        gamificationUtil.calculateUserActivitiesPerMonth(runner, ActivityType, new GamificationUtil.ActivityFrequencyCallback() {
                            @Override
                            public void onCallback(int activityNumber) {
                                leaderboardStats.add(new LeaderboardModel(runner, activityNumber, null));
                                totalCallbacks[0]++;
                                System.out.println("Activity No Per Month: " + activityNumber);
                                if (totalCallbacks[0] == runnersSize) {
                                    Collections.sort(leaderboardStats, new Comparator<LeaderboardModel>() {
                                        @Override
                                        public int compare(LeaderboardModel a, LeaderboardModel b) {
                                            System.out.println("A: " + a.getDistance() + " | B: " + b.getDistance());
                                            return Double.compare(b.getDistance(), a.getDistance());
                                        }
                                    });
                                    updateLeaderboard(leaderboardStats);
                                }
                            }
                        });
                    }
                }
            }
        });
    }

    public void getFastestData(double distance) {
        groupsUtil.retrieveUsersinGroup(GroupID, new GroupsDatabaseUtil.UsersinGroupsCallback() {
            @Override
            public void onCallback(ArrayList<String> runners) {
                if(isWeek) {
                    gamificationUtil.collectDistanceTime(runners, ActivityType, distance, true, new GamificationUtil.FastestTimeCallback() {
                        @Override
                        public void onCallback(ArrayList<LeaderboardModel> timeData) {
                            System.out.println("Time Data: " + timeData.size());
                            updateLeaderboard(timeData);
                        }
                    });
                } else {
                    gamificationUtil.collectDistanceTime(runners, ActivityType, distance, false, new GamificationUtil.FastestTimeCallback() {
                        @Override
                        public void onCallback(ArrayList<LeaderboardModel> timeData) {
                            System.out.println("Time Data: " + timeData.size());
                            updateLeaderboard(timeData);
                        }
                    });
                }
            }
        });
    }

    private void updateLeaderboard(ArrayList<LeaderboardModel> leaderboardStats) {
        LeaderboardRecyclerAdapter adapter = new LeaderboardRecyclerAdapter(getContext(), leaderboardStats);
        leaderboardRecyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        leaderboardRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    }
}
