package com.example.fittrack;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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

        gamificationUtil.retrieveActivitySplits(ActivityID, new GamificationUtil.ActivitySplitsCallback() {
            @Override
            public void onCallback(List<String> splits) {
                AnyChartView paceGraph = view.findViewById(R.id.mealGraph);

                APIlib.getInstance().setActiveAnyChartView(paceGraph);

                Cartesian cartesian = AnyChart.line();
                cartesian.animation(true);
                cartesian.title("Lap Times");
                cartesian.yAxis(0).title("Time (MM:SS)");
                cartesian.xAxis(0).title("Lap Number");

                cartesian.tooltip()
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
                cartesian.tooltip(false);


                Linear yAxis = cartesian.yAxis(0);
                yAxis.labels().format("function() { " +
                        "var minutes = Math.floor(this.value / 60); " +
                        "var seconds = this.value % 60; " +
                        "return minutes + ':' + (seconds < 10 ? '0' : '') + seconds; " +
                        "}");

                paceGraph.setChart(cartesian);
            }
        });
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
