package com.example.fittrack;

import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
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

public class ActivitySplitsFragment extends Fragment {
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseDatabaseHelper DatabaseUtil = new FirebaseDatabaseHelper(db);
    private TextView txtNoSplits;
    private String ActivityID;

    public ActivitySplitsFragment(String activityID) {
        ActivityID = activityID;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_activity_splits, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TableLayout splitsTable = view.findViewById(R.id.splitsTable);
        txtNoSplits = view.findViewById(R.id.txtNoSplitsMessage);
        LinearLayout tableContainer = view.findViewById(R.id.tableContainer);
        TableRow tableHeaders = new TableRow(view.getContext());

        DatabaseUtil.retrieveSpecificActivity(ActivityID, new FirebaseDatabaseHelper.SpecificActivityCallback() {
            @Override
            public void onCallback(Map<String, Object> data) {
                String returnedDistance = (String) data.get("distance");
                double formattedDistance = Double.parseDouble(returnedDistance);
                double overDistance = formattedDistance % 1000;
                List<Long> splits = (List<Long>) data.get("splits");
                System.out.println(splits);
                if(splits != null) {
                    for (int i = 0; i < splits.size(); i++) {
                        if (i == splits.size() - 1) {
                            TableRow row = new TableRow(getContext());
                            addInfo(row, String.valueOf(i + 1));
                            addInfo(row, longToTimeConversion(splits.get(i)));
                            String distance = String.format("%.2f", overDistance / 1000);
                            addInfo(row, distance);
                            double partialDistanceMeters = overDistance;
                            addInfo(row, calculateFinalSplitAveragePace(splits.get(i), partialDistanceMeters));

                            splitsTable.addView(row);
                        } else {
                            TableRow row = new TableRow(getContext());
                            addInfo(row, String.valueOf(i + 1));
                            addInfo(row, longToTimeConversion(splits.get(i)));
                            String distance = String.format("%.2f", (float) (i + 1));
                            addInfo(row, distance);
                            addInfo(row, longToTimeConversionRounded(splits.get(i)));
                            splitsTable.addView(row);
                        }
                    }
                } else {
                    splitsTable.setVisibility(View.GONE);
                    tableHeaders.setVisibility(View.GONE);
                    tableContainer.setVisibility(View.GONE);
                    txtNoSplits.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    private void addInfo(TableRow row, String text) {
        TextView textView = new TextView(getContext());
        textView.setText(text);
        textView.setPadding(16, 16, 30, 16);
        textView.setGravity(Gravity.CENTER);
        textView.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f));
        row.addView(textView);
    }


    private String longToTimeConversion(long longValue) {
        long milliseconds = longValue % 1000;
        longValue = longValue / 1000;
        long hours = longValue / 3600;
        long minutes = (longValue % 3600) / 60;
        long seconds = longValue % 60;

        if (hours > 0) {
            return String.format("%02d:%02d:%02d.%d", hours, minutes, seconds, milliseconds / 100);
        } else {
            return String.format("%02d:%02d.%d", minutes, seconds, milliseconds / 100);
        }
    }

    private String longToTimeConversionRounded(long longValue) {
        longValue = (longValue + 500) / 1000;
        long hours = longValue / 3600;
        long minutes = (longValue % 3600) / 60;
        long seconds = longValue % 60;

        if (hours > 0) {
            return String.format("%02d:%02d:%02d", hours, minutes, seconds);
        } else {
            return String.format("%02d:%02d", minutes, seconds);
        }
    }

    private String calculateFinalSplitAveragePace(long timeMillis, double partialDistanceMeters) {
        if (partialDistanceMeters <= 0) {
            return "-";
        }

        double distanceKm = partialDistanceMeters / 1000.0;
        long paceMillisPerKm = (long) (timeMillis / distanceKm);

        return longToTimeConversionRounded(paceMillisPerKm);
    }

}
