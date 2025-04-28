package com.example.fittrack;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.anychart.AnyChart;
import com.anychart.AnyChartView;
import com.anychart.chart.common.dataentry.DataEntry;
import com.anychart.chart.common.dataentry.ValueDataEntry;
import com.anychart.charts.Cartesian;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class IndividualStatisticsFragment extends Fragment {
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private AnyChartView distanceChart, activityFrequencyChart;
    private TextView valueActivityFrequency, valueDistance, value1k, value5k, value10k;
    private AutoCompleteTextView activitySelector;
    private RadioGroup timeRangeSelector;
    private String UserID;

    public IndividualStatisticsFragment(String UserID) {
        this.UserID = UserID;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_individual_stats, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

//        distanceChart = view.findViewById(R.id.distanceChart);
//        activityFrequencyChart = view.findViewById(R.id.frequencyChart);

        activitySelector = view.findViewById(R.id.activitySelector);
        timeRangeSelector = view.findViewById(R.id.timeRangeSelector);

        valueActivityFrequency = view.findViewById(R.id.valueActivityFrequency);
        valueDistance = view.findViewById(R.id.valueDistance);
        value1k = view.findViewById(R.id.value1k);
        value5k = view.findViewById(R.id.value5k);
        value10k = view.findViewById(R.id.value10k);

        String[] activities = {"Running", "Cycling", "Walking"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_dropdown_item_1line,
                activities
        );
        activitySelector.setAdapter(adapter);

        activitySelector.setText("Running", false);
        activitySelector.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                activitySelector.showDropDown();
            }
        });

        activitySelector.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String selectedItem = adapterView.getItemAtPosition(i).toString();
                activitySelector.setText(selectedItem, false);
                retrieveStats();
            }
        });

        timeRangeSelector.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                retrieveStats();
            }
        });

        retrieveStats();
    }

    private void retrieveStats() {
        String selectedActivity = activitySelector.getText().toString();
        int checkedId = timeRangeSelector.getCheckedRadioButtonId();

        String timeRange = checkedId == R.id.radioThisWeek ? "lastWeek" : "lastMonth";
        String documentName = selectedActivity + "_" + timeRange;

        db.collection("Users")
                .document(UserID)
                .collection("Statistics")
                .document(documentName)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            Long activityFrequency = documentSnapshot.getLong("ActivityFrequency");
                            Double distance = documentSnapshot.getDouble("Distance");
                            Long fastest1k = documentSnapshot.getLong("1K");
                            Long fastest5k = documentSnapshot.getLong("5K");
                            Long fastest10k = documentSnapshot.getLong("10K");

                            valueActivityFrequency.setText(activityFrequency != null && activityFrequency != -1 ? String.valueOf(activityFrequency) : "--");
                            valueDistance.setText(distance != null && distance != -1 ? String.format("%.2f km", distance / 1000) : "--");
                            value1k.setText(fastest1k != null && fastest1k != -1 ? formatTime(fastest1k) : "--");
                            value5k.setText(fastest5k != null && fastest5k != -1 ? formatTime(fastest5k) : "--");
                            value10k.setText(fastest10k != null && fastest10k != -1 ? formatTime(fastest10k) : "--");
                        }
//                        retrieveActivities(selectedActivity);
                    }
                });
    }

//    private void retrieveActivities(String activityType) {
//        int checkedId = timeRangeSelector.getCheckedRadioButtonId();
//        boolean isThisWeek = (checkedId == R.id.radioThisWeek);
//
//        java.util.Calendar calendar = java.util.Calendar.getInstance();
//        java.util.Calendar now = java.util.Calendar.getInstance();
//
//        if (isThisWeek) {
//            calendar.add(java.util.Calendar.DAY_OF_YEAR, -7);
//        } else {
//            calendar.add(java.util.Calendar.MONTH, -1);
//        }
//
//        Timestamp startDate = new Timestamp(calendar.getTime());
//        Timestamp endDate = new Timestamp(now.getTime());
//
//        db.collection("Activities")
//                .whereEqualTo("UserID", UserID)
//                .whereEqualTo("type", activityType)
//                .whereGreaterThanOrEqualTo("date", startDate)
//                .whereLessThanOrEqualTo("date", endDate)
//                .get()
//                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
//                    @Override
//                    public void onSuccess(QuerySnapshot querySnapshot) {
//                        Map<String, Integer> dateCountMap = new TreeMap<>();
//                        Map<String, Double> dateDistanceMap = new TreeMap<>();
//
//                        for (DocumentSnapshot document : querySnapshot.getDocuments()) {
//                            Timestamp timestamp = document.getTimestamp("date");
//                            String distanceStr = document.getString("distance");
//                            Double distance = null;
//
//                            if (distanceStr != null) {
//                                try {
//                                    distance = Double.parseDouble(distanceStr);
//                                } catch (NumberFormatException e) {
//                                    distance = 0.0; // fallback if somehow badly formatted
//                                }
//                            }
//
//                            if (timestamp != null && distance != null) {
//                                Date date = timestamp.toDate();
//                                SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd/MM/yyyy");
//                                String formattedDate = sdf.format(date);
//
//                                // Update frequency
//                                int count = dateCountMap.containsKey(formattedDate) ? dateCountMap.get(formattedDate) : 0;
//                                dateCountMap.put(formattedDate, count + 1);
//
//                                // Update distance (sum distance per day)
//                                double currentDistance = dateDistanceMap.containsKey(formattedDate) ? dateDistanceMap.get(formattedDate) : 0;
//                                dateDistanceMap.put(formattedDate, currentDistance + distance);
//                            }
//                        }
//
//
//                        List<DataEntry> frequencyDataEntries = new ArrayList<>();
//                        for (Map.Entry<String, Integer> entry : dateCountMap.entrySet()) {
//                            frequencyDataEntries.add(new ValueDataEntry(entry.getKey(), entry.getValue()));
//                        }
//
//                        Cartesian frequencyChart = AnyChart.line();
//                        frequencyChart.title("Activity Frequency");
//                        frequencyChart.xAxis(0).title("Date");
//                        frequencyChart.yAxis(0).title("Activity Count");
//                        frequencyChart.data(frequencyDataEntries);
//                        activityFrequencyChart.setChart(frequencyChart);
//
//
//                        List<DataEntry> distanceDataEntries = new ArrayList<>();
//                        for (Map.Entry<String, Double> entry : dateDistanceMap.entrySet()) {
//                            double distanceKm = entry.getValue() / 1000.0;
//                            double roundedDistanceKm = Math.round(distanceKm * 100.0) / 100.0; // round to 2 decimal places
//                            distanceDataEntries.add(new ValueDataEntry(entry.getKey(), roundedDistanceKm));
//                        }
//
//                        Cartesian distanceChartGraph = AnyChart.line();
//                        distanceChartGraph.title("Distance per Day");
//                        distanceChartGraph.xAxis(0).title("Date");
//                        distanceChartGraph.yAxis(0).title("Distance (KM)");
//                        distanceChartGraph.data(distanceDataEntries);
//                        distanceChart.setChart(distanceChartGraph);
//                    }
//                });
//    }

    private String formatTime(long millis) {
        if (millis < 0) return "--";

        long seconds = millis / 1000;
        long hours = seconds / 3600;
        long minutes = (seconds % 3600) / 60;
        long remainingSeconds = seconds % 60;

        if (hours > 0) {
            return String.format("%d:%02d:%02d", hours, minutes, remainingSeconds);
        } else {
            return String.format("%02d:%02d", minutes, remainingSeconds);
        }
    }
}