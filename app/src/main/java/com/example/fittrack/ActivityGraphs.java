package com.example.fittrack;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.anychart.APIlib;
import com.anychart.AnyChart;
import com.anychart.AnyChartView;
import com.anychart.chart.common.dataentry.DataEntry;
import com.anychart.chart.common.dataentry.ValueDataEntry;
import com.anychart.charts.Cartesian;
import com.anychart.core.axes.Linear;
import com.anychart.enums.Anchor;
import com.anychart.enums.TooltipPositionMode;

import java.util.ArrayList;
import java.util.List;

public class ActivityGraphs extends Fragment {
    private String ActivityID;
    private ProgressBar progressBar;
    private GamificationUtil gamificationUtil = new GamificationUtil();
    public ActivityGraphs(String ActivityID) {
        this.ActivityID = ActivityID;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_activity_graphs, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        AnyChartView paceGraph = view.findViewById(R.id.paceLineGraph);
        progressBar = view.findViewById(R.id.graphProgressBar);

        if (paceGraph == null) {
            return;
        }

        gamificationUtil.retrieveActivitySplits(ActivityID, new GamificationUtil.ActivitySplitsCallback() {
            @Override
            public void onCallback(List<String> splits) {
                AnyChartView paceGraph = view.findViewById(R.id.paceLineGraph);
                AnyChartView paceBarChart = view.findViewById(R.id.paceBarGraph);

                if (getActivity() == null || getContext() == null) {
                    return;
                }

                if (!splits.isEmpty()) {
//                    splits.remove(splits.size() - 1);
                } else {
                    view.findViewById(R.id.txtPaceGraphs).setVisibility(View.GONE);
                    view.findViewById(R.id.paceLineGraph).setVisibility(View.GONE);
                    view.findViewById(R.id.paceBarGraph).setVisibility(View.GONE);
                    view.findViewById(R.id.textView).setVisibility(View.GONE);
                    view.findViewById(R.id.elevationChart).setVisibility(View.GONE);
                    view.findViewById(R.id.txtNoGraphs).setVisibility(View.VISIBLE);
                }

                APIlib.getInstance().setActiveAnyChartView(paceGraph);

                Cartesian cartesian = AnyChart.line();
                cartesian.animation(true);
                cartesian.title("Lap Times");
                cartesian.yAxis(0).title("Time (MM:SS)");
                cartesian.xAxis(0).title("Lap Number");

                cartesian.tooltip()
                        .format("function() {" +
                                "var minutes = Math.floor(this.value / 60);" +
                                "var seconds = Math.floor(this.value % 60);" +
                                "return minutes + ':' + (seconds < 10 ? '0' : '') + seconds;" +
                                "}")
                        .positionMode(TooltipPositionMode.POINT)
                        .anchor(Anchor.RIGHT_TOP)
                        .offsetX(5d)
                        .offsetY(5d);

                List<DataEntry> lapTimeData = new ArrayList<>();
                for(int i = 0; i < splits.size(); i++) {
                    lapTimeData.add(new ValueDataEntry("Lap " + (i + 1), convertTimeToSeconds(splits.get(i))));
                }

                cartesian.data(lapTimeData);
                cartesian.xAxis(0).labels().padding(5d, 5d, 5d, 5d);


                Linear yAxis = cartesian.yAxis(0);
                yAxis.labels().format("function() { " +
                        "var minutes = Math.floor(this.value / 60); " +
                        "var seconds = this.value % 60; " +
                        "return minutes + ':' + (seconds < 10 ? '0' : '') + seconds; " +
                        "}");

                paceGraph.setChart(cartesian);

                APIlib.getInstance().setActiveAnyChartView(paceBarChart);
                Cartesian paceBar = AnyChart.column();
                paceBar.animation(true);
                paceBar.title("Average Pace Per Lap");
                paceBar.yAxis(0).title("Time (HH:MM)");
                paceBar.xAxis(0).title("Lap Number");

                paceBar.tooltip()
                        .format("function() {" +
                                "var minutes = Math.floor(this.value / 60);" +
                                "var seconds = Math.floor(this.value % 60);" +
                                "return minutes + ':' + (seconds < 10 ? '0' : '') + seconds;" +
                                "}")
                        .positionMode(TooltipPositionMode.POINT)
                        .anchor(Anchor.RIGHT_TOP)
                        .offsetX(5d)
                        .offsetY(5d);

                List<DataEntry> paceBarData = new ArrayList<>();
                for(int i =0; i <splits.size(); i++) {
                    paceBarData.add(new ValueDataEntry("Lap " + (i + 1), convertTimeToSeconds(splits.get(i))));
                }

                paceBar.data(paceBarData);
                paceBar.xAxis(0).labels().padding(5d, 5d, 5d, 5d);

                Linear PaceyAxis = paceBar.yAxis(0);
                PaceyAxis.labels().format("function() { " +
                        "var minutes = Math.floor(this.value / 60); " +
                        "var seconds = this.value % 60; " +
                        "return minutes + ':' + (seconds < 10 ? '0' : '') + seconds; " +
                        "}");
                paceBarChart.setChart(paceBar);
                progressBar.setVisibility(View.GONE);
            }
        });

        gamificationUtil.retrieveElevationStats(ActivityID, new GamificationUtil.ActivityElevationCallback() {
            @Override
            public void onCallback(List<Double> elevationStats, double time) {
                System.out.println(elevationStats);
                AnyChartView elevationLineChart = view.findViewById(R.id.elevationChart);

                APIlib.getInstance().setActiveAnyChartView(elevationLineChart);
                Cartesian elevationLine = AnyChart.line();
                elevationLine.animation(true);
                elevationLine.title("Elevation Gain Across Activity");
                elevationLine.yAxis(0).title("Elevation Gain (m)");
                elevationLine.xAxis(0).title("Activity Time");

                elevationLine.tooltip()
                        .positionMode(TooltipPositionMode.POINT)
                        .anchor(Anchor.RIGHT_TOP)
                        .offsetX(5d)
                        .offsetY(5d);

                int dataSize = elevationStats.size();
                double interval = time / dataSize;
                double currentTime = 0;

                List<DataEntry> elevationData = new ArrayList<>();
                for(int i =0; i < elevationStats.size(); i++) {
                    String timeLabel = formatTime(currentTime);
                    elevationData.add(new ValueDataEntry(timeLabel, elevationStats.get(i)));
                    currentTime += interval;
                }

                elevationLine.data(elevationData);
                elevationLine.xAxis(0).labels().padding(5d, 5d, 5d, 5d);
                elevationLine.tooltip(false);
                elevationLineChart.setChart(elevationLine);
            }
        });
    }

    private String formatTime(double seconds) {
        int minutes = (int) (seconds / 60);
        int secs = (int) (seconds % 60);
        return String.format("%02d:%02d", minutes, secs);
    }

    private int convertTimeToSeconds(String time) {
        String[] parts = time.split(":");
        int minutes = Integer.parseInt(parts[0]);
        int seconds = Integer.parseInt(parts[1]);
        return minutes * 60 + seconds;
    }

    private String convertSecondsToTime(int inputSeconds) {
        int minutes = inputSeconds / 60;
        int seconds = inputSeconds % 60;
        return minutes + ":" + (seconds < 10 ? "0" : "") + seconds;
    }
}
