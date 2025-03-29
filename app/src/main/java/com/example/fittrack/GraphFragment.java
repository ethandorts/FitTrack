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

        if(selectedMetric.equals("Most Distance")) {
            loadDistanceData();
        }
    }

    private void loadDistanceData() {
        groupsUtil.retrieveUsersinGroup(GroupID, new GroupsDatabaseUtil.UsersinGroupsCallback() {
            @Override
            public void onCallback(ArrayList<String> runners) {
                distanceTotals = new ArrayList<>();
                int runnersSize = runners.size();
                int[] totalCallbacks = {0};

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

    private void loadColumnChart(List<DataEntry> distanceTotals) {
        View view = getView();

        AnyChartView anyChartView = view.findViewById(R.id.any_chart_barchart);
        anyChartView.setProgressBar(view.findViewById(R.id.progressBarChart));
        Cartesian cartesian = AnyChart.column();

        Column column = cartesian.column(distanceTotals);

        column.tooltip()
                .titleFormat("{%X}")
                .position(Position.CENTER_BOTTOM)
                .anchor(Anchor.CENTER_BOTTOM)
                .offsetX(0d)
                .offsetY(0d)
                .format("{%Value}{groupsSeparator: } metres");

        cartesian.animation(true);
        cartesian.title("Total Distance Ran by Users");

        cartesian.yScale().minimum(0d);
        cartesian.yScale().ticks().interval(10000);

        cartesian.yAxis(0).labels().format("{%Value}{groupsSeparator: }");

        cartesian.tooltip().positionMode(TooltipPositionMode.POINT);
        cartesian.interactivity().hoverMode(HoverMode.BY_X);

        cartesian.xAxis(0).labels()
                .rotation(0)
                .wordWrap(String.valueOf(true))
                .enabled(true);

        cartesian.margin(0d, 0d, 0d, 0d);
        cartesian.xAxis(0).title("Runners");
        cartesian.yAxis(0).title("Distance");

        cartesian.barGroupsPadding(0.1d);

        anyChartView.setChart(cartesian);
    }
}
