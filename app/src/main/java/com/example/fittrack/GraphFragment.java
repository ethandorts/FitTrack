package com.example.fittrack;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.anychart.AnyChart;
import com.anychart.AnyChartView;
import com.anychart.chart.common.dataentry.DataEntry;
import com.anychart.chart.common.dataentry.ValueDataEntry;
import com.anychart.charts.Cartesian;
import com.anychart.core.cartesian.series.Column;
import com.anychart.enums.Anchor;
import com.anychart.enums.HoverMode;
import com.anychart.enums.Position;
import com.anychart.enums.TooltipPositionMode;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class GraphFragment extends Fragment {
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private GroupsDatabaseUtil groupsUtil = new GroupsDatabaseUtil(db);
    private FirebaseDatabaseHelper userUtil = new FirebaseDatabaseHelper(db);
    private GamificationUtil gamUtil = new GamificationUtil();
    private List<DataEntry> distanceTotals;
    private String selectedMetric;
    private boolean isWeek = true;
    private String GroupID;
    private String ActivityType;

    public GraphFragment(String selectedMetric, boolean isWeek, String GroupID, String ActivityType) {
        this.selectedMetric = selectedMetric;
        this.isWeek = isWeek;
        this.GroupID = GroupID;
        this.ActivityType = ActivityType;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_graph, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if(GroupID == null) {
            System.out.println("GroupID IS NULL");
        } else {
            System.out.println("GroupID is filled" + GroupID);
        }

        if(selectedMetric.equals("Distance")) {
            loadDistanceData();
        } else if (selectedMetric.equals("Activity Frequency")) {
            loadActivityFrequencyData();
        } else if (selectedMetric.equals("1 KM")) {
            loadFastestData(1000);
        } else if (selectedMetric.equals("5 KM")) {
            loadFastestData(5000);
        } else if (selectedMetric.equals("10 KM")) {
            loadFastestData(10000);
        }
    }

    private void loadActivityFrequencyData() {
        groupsUtil.retrieveUsersinGroup(GroupID, new GroupsDatabaseUtil.UsersinGroupsCallback() {
            @Override
            public void onCallback(ArrayList<String> runners) {
                distanceTotals = new ArrayList<>();
                int runnersSize = runners.size();
                int[] totalCallbacks = {0};

                for (String runner : runners) {
                    userUtil.retrieveUserName(runner, new FirebaseDatabaseHelper.FirestoreUserNameCallback() {
                        @Override
                        public void onCallback(String FullName, long weight, long height, long activityFrequency, long dailyCalorieGoal) {
                            if(isWeek) {
                                System.out.println("Name is: " + runner);
                                gamUtil.calculateUserActivitiesPerWeek(runner, ActivityType, new GamificationUtil.ActivityFrequencyCallback() {
                                    @Override
                                    public void onCallback(int activityNumber) {
                                        System.out.println("Activity No Per Week: " + activityNumber);
                                        distanceTotals.add(new ValueDataEntry(FullName, activityNumber));
                                        totalCallbacks[0]++;

                                        if (totalCallbacks[0] == runnersSize) {
                                            loadColumnChart(distanceTotals);
                                        }
                                    }
                                });
                            } else {
                                gamUtil.calculateUserActivitiesPerMonth(runner, ActivityType, new GamificationUtil.ActivityFrequencyCallback() {
                                    @Override
                                    public void onCallback(int activityNumber) {
                                        distanceTotals.add(new ValueDataEntry(FullName, activityNumber));
                                        totalCallbacks[0]++;
                                        System.out.println("Activity No Per Month: " + activityNumber);
                                        if (totalCallbacks[0] == runnersSize) {
                                            loadColumnChart(distanceTotals);
                                        }
                                    }
                                });
                            }
                        }
                    });
                }
            }
        });
    }

    private void loadDistanceData() {
        groupsUtil.retrieveUsersinGroup(GroupID, new GroupsDatabaseUtil.UsersinGroupsCallback() {
            @Override
            public void onCallback(ArrayList<String> runners) {
                distanceTotals = new ArrayList<>();
                int runnersSize = runners.size();
                int [] totalCallbacks = {0};

                for(String runner : runners) {
                    userUtil.retrieveUserName(runner, new FirebaseDatabaseHelper.FirestoreUserNameCallback() {
                        @Override
                        public void onCallback(String FullName, long weight, long height, long activityFrequency, long dailyCalorieGoal) {
                            if(isWeek) {
                                gamUtil.calculateUserDistancePerWeek(runner, ActivityType,  new GamificationUtil.UserDistancePerMonthCallback() {
                                    @Override
                                    public void onCallback(double distancePerMonth) {
                                        System.out.println("DistancePerWeek: " + distancePerMonth);
                                        distanceTotals.add(new ValueDataEntry(FullName, distancePerMonth));
                                        totalCallbacks[0]++;

                                        if (totalCallbacks[0] == runnersSize) {
                                            loadColumnChart(distanceTotals);
                                        }
                                    }
                                });
                            } else {
                                gamUtil.calculateUserDistancePerMonth(runner, ActivityType, new GamificationUtil.UserDistancePerMonthCallback() {
                                    @Override
                                    public void onCallback(double distancePerMonth) {
                                        System.out.println("DistancePerMonth: " + distancePerMonth);
                                        distanceTotals.add(new ValueDataEntry(FullName, distancePerMonth));
                                        totalCallbacks[0]++;

                                        if (totalCallbacks[0] == runnersSize) {
                                            loadColumnChart(distanceTotals);
                                        }
                                    }
                                });
                            }
                        }
                    });
                }
            }
        });
    }

    public void loadFastestData(double distance) {
        List<DataEntry> fastestData = new ArrayList<>();
        groupsUtil.retrieveUsersinGroup(GroupID, new GroupsDatabaseUtil.UsersinGroupsCallback() {
            @Override
            public void onCallback(ArrayList<String> runners) {
                gamUtil.collectDistanceTime(runners, ActivityType, distance, isWeek, new GamificationUtil.FastestTimeCallback() {
                    @Override
                    public void onCallback(ArrayList<LeaderboardModel> timeData) {
                        System.out.println("Time Data: " + timeData.size());

                        if (timeData.isEmpty()) {
                            loadColumnChart(fastestData);
                            return;
                        }

                        int total = timeData.size();
                        int[] resolved = {0};

                        for (LeaderboardModel model : timeData) {
                            userUtil.retrieveUserName(model.getUsername(), new FirebaseDatabaseHelper.FirestoreUserNameCallback() {
                                @Override
                                public void onCallback(String fullName, long weight, long height, long activityFrequency, long dailyCalorieGoal) {
                                    fastestData.add(new ValueDataEntry(fullName, model.getDistance()));
                                    resolved[0]++;

                                    if (resolved[0] == total) {
                                        loadColumnChart(fastestData);
                                    }
                                }
                            });
                        }
                    }
                });
            }
        });
    }


    private void loadColumnChart(List<DataEntry> distanceTotals) {
        View view = getView();
        if (view == null) return;

        boolean isStatTimeBased = selectedMetric.equals("1 KM") || selectedMetric.equals("5 KM") || selectedMetric.equals("10 KM");

        Collections.sort(distanceTotals, new Comparator<DataEntry>() {
            @Override
            public int compare(DataEntry value1, DataEntry value2) {
                double firstValue = parseDoubleSafe(value1.getValue("value"));
                double secondValue = parseDoubleSafe(value2.getValue("value"));
                return isStatTimeBased ? Double.compare(firstValue, secondValue) : Double.compare(secondValue, firstValue);
            }
        });

        if (distanceTotals.size() > 5) {
            distanceTotals = new ArrayList<>(distanceTotals.subList(0, 5));
        }

        AnyChartView anyChartView = view.findViewById(R.id.any_chart_barchart);
        anyChartView.setProgressBar(view.findViewById(R.id.progressBarChart));
        Cartesian cartesian = AnyChart.column();

        Column column = cartesian.column(distanceTotals);

        String chartTitle;
        String yAxisTitle;
        String tooltipFormat;
        boolean isTimeBased = selectedMetric.equals("1 KM") || selectedMetric.equals("5 KM") || selectedMetric.equals("10 KM");

        if (selectedMetric.equals("Activity Frequency")) {
            chartTitle = "Activity Frequency by Users";
            yAxisTitle = "Activities";
            tooltipFormat = "{%Value} activities";
        } else if (isTimeBased) {
            chartTitle = "Fastest Times by Users";
            yAxisTitle = "Time (mm:ss)";
            tooltipFormat = "";
        } else {
            chartTitle = "Total Distance Ran by Users";
            yAxisTitle = "Distance (km)";
            tooltipFormat = "{%Value}{groupsSeparator: } km";
        }

        // Tooltip formatting
        if (isTimeBased) {
            column.tooltip()
                    .titleFormat("{%X}")
                    .position(Position.CENTER_BOTTOM)
                    .anchor(Anchor.CENTER_BOTTOM)
                    .offsetX(0d)
                    .offsetY(0d)
                    .format("function() { " +
                            "var sec = this.value;" +
                            "var h = Math.floor(sec / 3600);" +
                            "var m = Math.floor((sec % 3600) / 60);" +
                            "var s = sec % 60;" +
                            "var timeStr = (h > 0 ? (h < 10 ? '0' + h : h) + ':' : '') + " +
                            "              (m < 10 ? '0' + m : m) + ':' + " +
                            "              (s < 10 ? '0' + s : s);" +
                            "return timeStr;" +
                            "}");
        } else if (selectedMetric.equals("Activity Frequency")) {
            column.tooltip()
                    .titleFormat("{%X}")
                    .position(Position.CENTER_BOTTOM)
                    .anchor(Anchor.CENTER_BOTTOM)
                    .offsetX(0d)
                    .offsetY(0d)
                    .format("{%Value} activities");
        } else {
            column.tooltip()
                    .titleFormat("{%X}")
                    .position(Position.CENTER_BOTTOM)
                    .anchor(Anchor.CENTER_BOTTOM)
                    .offsetX(0d)
                    .offsetY(0d)
                    .format("function() { return (this.value / 1000).toFixed(2) + ' km'; }");
        }

        // Y-axis formatting
        if (isTimeBased) {
            cartesian.yAxis(0).labels().format("function() { " +
                    "var sec = this.value;" +
                    "var h = Math.floor(sec / 3600);" +
                    "var m = Math.floor((sec % 3600) / 60);" +
                    "var s = sec % 60;" +
                    "var timeStr = (h > 0 ? (h < 10 ? '0' + h : h) + ':' : '') + " +
                    "              (m < 10 ? '0' + m : m) + ':' + " +
                    "              (s < 10 ? '0' + s : s);" +
                    "return timeStr;" +
                    "}");
        } else if (selectedMetric.equals("Activity Frequency")) {
            cartesian.yAxis(0).labels().format("{%Value}");
        } else {
            cartesian.yAxis(0).labels().format("function() { return (this.value / 1000).toFixed(2) + ' km'; }");
        }

        // General chart styling
        cartesian.animation(true);
        cartesian.title(chartTitle);
        cartesian.yScale().minimum(0d);
        cartesian.tooltip().positionMode(TooltipPositionMode.POINT);
        cartesian.interactivity().hoverMode(HoverMode.BY_X);

        cartesian.xAxis(0).labels()
                .rotation(0)
                .wordWrap("true")
                .enabled(true);

        cartesian.margin(0d, 0d, 0d, 0d);
        cartesian.xAxis(0).title("Users");
        cartesian.yAxis(0).title(yAxisTitle);
        cartesian.barGroupsPadding(0.1d);

        anyChartView.setChart(cartesian);
    }

    private double parseDoubleSafe(Object value) {
        try {
            if (value instanceof Number) {
                return ((Number) value).doubleValue();
            } else if (value instanceof String) {
                return Double.parseDouble((String) value);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }


}
