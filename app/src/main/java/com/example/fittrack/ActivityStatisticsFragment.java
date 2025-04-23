package com.example.fittrack;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
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

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ActivityStatisticsFragment extends Fragment {
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseDatabaseHelper activityUtil = new FirebaseDatabaseHelper(db);
    private TableLayout statsTable;
    private String ActivityID;
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
                } else if (splits != null && !splits.isEmpty()) {
                    minSplit = splits.get(0);
                } else {
                    minSplit = 0L;
                }

                String bestPace = ConversionUtil.longToTimeConversion(minSplit);
                List<Double> elevationStats = (data.get("Elevation") != null) ? (List<Double>) data.get("Elevation") : new ArrayList<>();
                double gain = elevationStats.isEmpty() ? 0.0 : elevationCalculator.calculateElevationGain(elevationStats);
                double loss = elevationStats.isEmpty() ? 0.0 : elevationCalculator.calculateElevationLoss(elevationStats);
                double maxElevation = elevationStats.isEmpty() ? 0.0 : elevationCalculator.getMaxElevation(elevationStats);
                double minElevation = elevationStats.isEmpty() ? 0.0 : elevationCalculator.getMinElevation(elevationStats);

                addTableSection(statsTable, "Pace");
                addRowStatsTable("Average Pace", pace + " /KM");
                if(minSplit != 0L) {
                    addRowStatsTable("Best Pace", bestPace.split("\\.")[0] + " /KM");
                }

                addTableSection(statsTable, "Speed");
                addRowStatsTable("Average Speed", String.format("%.2f mph", ActivityStatsConversionUtil.calculateAverageSpeed(distance, time)));
                addRowStatsTable("Average Moving Speed", String.format("%.2f mph", ActivityStatsConversionUtil.calculateAverageSpeed(distance, time)));

                if (splits != null && splits.size() > 1) {
                    addRowStatsTable("Maximum Speed", String.format("%.2f mph", ConversionUtil.convertLongToMPH(minSplit)));
                }

                addTableSection(statsTable, "Time");
                addRowStatsTable("Total Time", ConversionUtil.convertSecondsToTime(time));

                addTableSection(statsTable, "Calories");
                addRowStatsTable("Calories Burned", String.valueOf((long) data.get("caloriesBurned")));

                if (!elevationStats.isEmpty()) {
                    addTableSection(statsTable, "Elevation");
                    addRowStatsTable("Elevation Gain", String.format("%.2f metres", gain));
                    addRowStatsTable("Elevation Loss", String.format("%.2f metres", loss));
                    addRowStatsTable("Maximum Elevation", String.format("%.2f metres", maxElevation));
                    addRowStatsTable("Minimum Elevation", String.format("%.2f metres", minElevation));
                }

                // addTableSection(statsTable, "Heart Rate");
                // addRowStatsTable("Average Heart Rate", "— BPM");
                // addRowStatsTable("Max Heart Rate", "— BPM");
            }
        });
    }

    private void addTableSection(TableLayout statTable, String sectionTitle) {
        TableRow titleRow = new TableRow(requireContext());

        TextView txtSectionTitle = new TextView(requireContext());
        txtSectionTitle.setText(sectionTitle);
        txtSectionTitle.setBackgroundResource(R.drawable.section_header);
        txtSectionTitle.setTextColor(getResources().getColor(R.color.white));
        txtSectionTitle.setTypeface(null, Typeface.BOLD);
        txtSectionTitle.setPadding(24, 24, 24, 24);
        txtSectionTitle.setTextSize(18);

        TableRow.LayoutParams params = new TableRow.LayoutParams();
        params.span = 2;
        txtSectionTitle.setLayoutParams(params);
        titleRow.setLayoutParams(new TableRow.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        titleRow.addView(txtSectionTitle);
        statTable.addView(titleRow);
    }

    private void addRowStatsTable(String label, String value) {
        TableRow row = new TableRow(requireContext());


        int rowIndex = statsTable.getChildCount();
        if (rowIndex % 2 == 0) {
            row.setBackgroundColor(getResources().getColor(R.color.row_background_light));
        } else {
            row.setBackgroundColor(getResources().getColor(R.color.row_background_alt));
        }

        TextView txtLabel = new TextView(requireContext());
        txtLabel.setText(label);
        txtLabel.setPadding(24, 20, 8, 20);
        txtLabel.setTextSize(16);
        txtLabel.setTypeface(null, Typeface.BOLD);
        txtLabel.setTextColor(Color.DKGRAY);
        row.addView(txtLabel);

        TextView txtValue = new TextView(requireContext());
        txtValue.setText(value);
        txtValue.setPadding(8, 20, 24, 20);
        txtValue.setTextSize(16);
        txtValue.setTextColor(Color.BLACK);
        txtValue.setGravity(Gravity.END);
        row.addView(txtValue);

        statsTable.addView(row);
    }
}
