package com.example.fittrack;

import android.app.Activity;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ActivityStatisticsFragment extends Fragment {
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseDatabaseHelper activityUtil = new FirebaseDatabaseHelper(db);
    TableLayout statsTable;
    String ActivityID;
    private ElevationCalculator elevationCalculator = new ElevationCalculator();

    public ActivityStatisticsFragment(String ActivityID) {
        this.ActivityID = ActivityID;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_activity_statistics, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        statsTable = view.findViewById(R.id.activityStatsTable);
        activityUtil.retrieveSpecificActivity(ActivityID, new FirebaseDatabaseHelper.SpecificActivityCallback() {
            @Override
            public void onCallback(Map<String, Object> data) {
                Long minSplit = Long.MAX_VALUE;

                String stringDistance = (String) data.get("distance");
                double distance = Double.parseDouble(stringDistance);
                String pace = (String) data.get("pace");
                int time = (data.get("time") != null) ? ((Number) data.get("time")).intValue() : 0;
                List<Long> splits = (List<Long>) data.get("splits");
                if (splits != null && splits.size() > 1) {
                    for (int i = 0; i < splits.size() - 1; i++) {
                        if (splits.get(i) < minSplit) {
                            minSplit = splits.get(i);
                        }
                    }
                } else {
                    minSplit = splits.get(0);
                }
                String bestPace = ConversionUtil.longToTimeConversion(minSplit);

                List<Double> elevationStats = (data.get("Elevation") != null) ? (List<Double>) data.get("Elevation") : new ArrayList<>();
                double gain = (elevationStats.isEmpty()) ? 0.0 : elevationCalculator.calculateElevationGain(elevationStats);
                double loss = (elevationStats.isEmpty()) ? 0.0 : elevationCalculator.calculateElevationLoss(elevationStats);
                double maxElevation = (elevationStats.isEmpty()) ? 0.0 : elevationCalculator.getMaxElevation(elevationStats);
                double minElevation = (elevationStats.isEmpty()) ? 0.0 : elevationCalculator.getMinElevation(elevationStats);

                addTableSection(statsTable, "Pace");
                addRowStatsTable(statsTable, "Average Pace", (String) data.get("pace") + " /KM");
                addRowStatsTable(statsTable, "Best Pace", bestPace + " /KM");
                addTableSection(statsTable, "Speed");
                addRowStatsTable(statsTable, "Average Speed", String.valueOf(ActivityStatsConversionUtil.calculateAverageSpeed(distance, (int) time)) + " mph");
                addRowStatsTable(statsTable, "Average Moving Speed", String.valueOf(ActivityStatsConversionUtil.calculateAverageSpeed(distance, (int) time)) + " mph");
                if(splits.size() > 1) {
                    addRowStatsTable(statsTable, "Maximum Speed", String.format("%.2f", ConversionUtil.convertLongToMPH(minSplit)) + " mph");
                }
                addTableSection(statsTable, "Time");
                addRowStatsTable(statsTable, "Total Time", ConversionUtil.convertSecondsToTime(time));
                addTableSection(statsTable, "Calories");
                addRowStatsTable(statsTable, "Calories Burned", String.valueOf((long) data.get("caloriesBurned")));
                if (!elevationStats.isEmpty()) {
                    addTableSection(statsTable, "Elevation");
                    addRowStatsTable(statsTable, "Elevation Gain", String.format("%.2f", gain) + " metres");
                    addRowStatsTable(statsTable, "Elevation Loss", String.format("%.2f", loss) + " metres");
                    addRowStatsTable(statsTable, "Maximum Elevation", String.format("%.2f", maxElevation) + " metres");
                    addRowStatsTable(statsTable, "Minimum Elevation", String.format("%.2f", minElevation) + " metres");
                }
                addTableSection(statsTable, "Heart Rate");
                addRowStatsTable(statsTable, "Average Heart Rate", " - BPM");
                addRowStatsTable(statsTable, "Maximum Heart Rate", "- BPM");
            }
        });
    }

    public void addTableSection(TableLayout statTable, String sectionTitle) {
        TableRow titleRow = new TableRow(requireContext());

        TextView txtSectionTitle = new TextView(requireContext());
        txtSectionTitle.setText(sectionTitle);
        txtSectionTitle.setBackgroundColor(getResources().getColor(R.color.button_blue));
        txtSectionTitle.setTextColor(getResources().getColor(R.color.white));
        txtSectionTitle.setTypeface(null, Typeface.BOLD);
        txtSectionTitle.setPadding(10, 10, 10, 10);
        txtSectionTitle.setTextSize(30);

        TableRow.LayoutParams params = new TableRow.LayoutParams();
        params.span = 2;
        txtSectionTitle.setLayoutParams(params);
        titleRow.setLayoutParams(new TableRow.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        titleRow.addView(txtSectionTitle);
        statTable.addView(titleRow);
    }

    private void addRowStatsTable(TableLayout statsTable, String label, String value) {
        TableRow row = new TableRow(requireContext());

        TextView txtLabel = new TextView(requireContext());
        txtLabel.setText(label);
        txtLabel.setPadding(20, 20, 20, 20);
        txtLabel.setTextSize(16);
        txtLabel.setTypeface(null, Typeface.BOLD);
        row.addView(txtLabel);

        TextView txtValue = new TextView(requireContext());
        txtValue.setText(value);
        txtValue.setPadding(20, 20, 20, 20);
        txtValue.setTextSize(16);
        row.addView(txtValue);

        statsTable.addView(row);
    }
}
