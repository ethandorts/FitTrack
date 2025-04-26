package com.example.fittrack;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.auth.User;

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
                final List<String> userIds = new ArrayList<String>(runners);
                List<Task<DocumentSnapshot>> statTasks = new ArrayList<>();

                for (String uid : userIds) {
                    DocumentReference docRef = db
                            .collection("Users")
                            .document(uid)
                            .collection("Statistics")
                            .document(isWeek ? ActivityType + "_lastWeek" : ActivityType + "_lastMonth");
                    statTasks.add(docRef.get());
                }

                Tasks.whenAllSuccess(statTasks)
                        .addOnSuccessListener(new OnSuccessListener<List<Object>>() {
                            @Override
                            public void onSuccess(List<Object> docs) {
                                List<Task<DataEntry>> chartTasks = new ArrayList<>();

                                for (int i = 0; i < docs.size(); i++) {
                                    final String uid = userIds.get(i);
                                    DocumentSnapshot snap = (DocumentSnapshot) docs.get(i);

                                    Double freq = snap.getDouble("ActivityFrequency");
                                    if (freq == null) freq = 0.0;
                                    final double finalFreq = freq;

                                    TaskCompletionSource<DataEntry> tcs = new TaskCompletionSource<>();
                                    chartTasks.add(tcs.getTask());

                                    userUtil.retrieveChatName(uid, new FirebaseDatabaseHelper.ChatUserCallback() {
                                        @Override
                                        public void onCallback(String chatName) {
                                            tcs.setResult(new ValueDataEntry(chatName, finalFreq));
                                        }
                                    });
                                }

                                Tasks.whenAllSuccess(chartTasks)
                                        .addOnSuccessListener(new OnSuccessListener<List<Object>>() {
                                            @Override
                                            public void onSuccess(List<Object> entries) {
                                                List<DataEntry> dataEntries = (List<DataEntry>)(List<?>) entries;
                                                System.out.println(dataEntries +  " - Data Entries");
                                                loadColumnChart(dataEntries);
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Log.e("GraphFragment", "Error loading AF chart names", e);
                                            }
                                        });
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.e("GraphFragment", "Error loading AF stats", e);
                            }
                        });
            }
        });
    }



    private void loadDistanceData() {
        System.out.println("Loading distance for GroupID=" + GroupID + ", isWeek=" + isWeek);

        groupsUtil.retrieveUsersinGroup(GroupID, new GroupsDatabaseUtil.UsersinGroupsCallback() {
            @Override
            public void onCallback(ArrayList<String> runners) {
                List<String> userIds = new ArrayList<String>(runners);

                List<Task<DocumentSnapshot>> statTasks = new ArrayList<Task<DocumentSnapshot>>();
                for (String uid : userIds) {
                    DocumentReference docRef = db
                            .collection("Users")
                            .document(uid)
                            .collection("Statistics")
                            .document(isWeek ? ActivityType + "_lastWeek" : ActivityType + "_lastMonth");
                    statTasks.add(docRef.get());
                }

                Tasks.whenAllSuccess(statTasks)
                        .addOnSuccessListener(new OnSuccessListener<List<Object>>() {
                            @Override
                            public void onSuccess(List<Object> statsResults) {
                                List<Task<DataEntry>> chartTasks = new ArrayList<Task<DataEntry>>();

                                for (int i = 0; i < statsResults.size(); i++) {
                                    DocumentSnapshot snap = (DocumentSnapshot) statsResults.get(i);
                                    final double dist = snap.getDouble("Distance") != null
                                            ? snap.getDouble("Distance")
                                            : 0.0;


                                    TaskCompletionSource<DataEntry> tcs = new TaskCompletionSource<>();
                                    chartTasks.add(tcs.getTask());

                                    userUtil.retrieveChatName(
                                            userIds.get(i),
                                            new FirebaseDatabaseHelper.ChatUserCallback() {
                                                @Override
                                                public void onCallback(String chatName) {
                                                    tcs.setResult(new ValueDataEntry(chatName, dist));
                                                }
                                            }
                                    );
                                }

                                Tasks.whenAllSuccess(chartTasks)
                                        .addOnSuccessListener(new OnSuccessListener<List<Object>>() {
                                            @Override
                                            public void onSuccess(List<Object> chartResults) {
                                                List<DataEntry> entries = (List<DataEntry>)(List<?>) chartResults;
                                                loadColumnChart(entries);
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Log.e("GraphFragment", "Error loading distance chart names", e);
                                            }
                                        });
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.e("GraphFragment", "Error loading distance stats", e);
                            }
                        });
            }
        });
    }

    private void loadFastestData(final double distance) {
        if (GroupID == null) {
            Log.e("GraphFragment", "Cannot load fastest data: GroupID is null");
            return;
        }

        Log.d("GraphFragment", "Loading fastest data for distance: " + distance + ", GroupID=" + GroupID);

        groupsUtil.retrieveUsersinGroup(GroupID, new GroupsDatabaseUtil.UsersinGroupsCallback() {
            @Override
            public void onCallback(ArrayList<String> runners) {
                if (runners == null || runners.isEmpty()) {
                    Log.e("GraphFragment", "No runners found in group");
                    return;
                }

                Log.d("GraphFragment", "Found " + runners.size() + " runners");

                final List<String> userIds = new ArrayList<>(runners);
                List<Task<DocumentSnapshot>> statTasks = new ArrayList<>();

                for (String uid : userIds) {
                    DocumentReference docRef = db
                            .collection("Users")
                            .document(uid)
                            .collection("Statistics")
                            .document(isWeek ? ActivityType + "_lastWeek" : ActivityType + "_lastMonth");
                    statTasks.add(docRef.get());
                }

                Tasks.whenAllSuccess(statTasks)
                        .addOnSuccessListener(new OnSuccessListener<List<Object>>() {
                            @Override
                            public void onSuccess(List<Object> statsResults) {
                                Log.d("GraphFragment", "Successfully fetched stats for " + statsResults.size() + " users");
                                List<Task<DataEntry>> chartTasks = new ArrayList<>();

                                for (int i = 0; i < statsResults.size(); i++) {
                                    DocumentSnapshot snap = (DocumentSnapshot) statsResults.get(i);
                                    final String uid = userIds.get(i);

                                    String fieldKey;
                                    if (distance == 1000.0) {
                                        fieldKey = "1K";
                                    } else if (distance == 5000.0) {
                                        fieldKey = "5K";
                                    } else {
                                        fieldKey = "10K";
                                    }

                                    Double fastest = null;
                                    try {
                                        Object raw = snap.get(fieldKey);
                                        if (raw != null) {
                                            if (raw instanceof Number) {
                                                fastest = ((Number) raw).doubleValue();
                                            } else if (raw instanceof String) {
                                                fastest = parseTimeStringToSeconds((String) raw);
                                            }
                                        }
                                    } catch (Exception e) {
                                        Log.w("GraphFragment", "Error parsing time for field: " + fieldKey, e);
                                    }

                                    if (fastest == null) {
                                        Log.d("GraphFragment", "No valid time for userId=" + uid);
                                        fastest = Double.MAX_VALUE;
                                    } else {
                                        Log.d("GraphFragment", "UserId=" + uid + ", fastestTime=" + fastest + " seconds");
                                    }

                                    final double finalFastest = fastest;
                                    TaskCompletionSource<DataEntry> tcs = new TaskCompletionSource<>();
                                    chartTasks.add(tcs.getTask());

                                    userUtil.retrieveChatName(uid, new FirebaseDatabaseHelper.ChatUserCallback() {
                                        @Override
                                        public void onCallback(String chatName) {
                                            Log.d("GraphFragment", "Mapping chat name: " + chatName + " -> " + finalFastest);
                                            tcs.setResult(new ValueDataEntry(chatName, finalFastest));
                                        }
                                    });
                                }

                                Tasks.whenAllSuccess(chartTasks)
                                        .addOnSuccessListener(new OnSuccessListener<List<Object>>() {
                                            @Override
                                            public void onSuccess(List<Object> chartResults) {
                                                List<DataEntry> entries = new ArrayList<>();
                                                for (Object obj : chartResults) {
                                                    if (obj instanceof DataEntry) {
                                                        DataEntry entry = (DataEntry) obj;
                                                        double value = parseDoubleSafe(entry.getValue("value"));
                                                        if (value < Double.MAX_VALUE) {
                                                            entries.add(entry);
                                                            Log.d("GraphFragment", "Added chart entry: " + entry.getValue("x") + " -> " + value);
                                                        } else {
                                                            Log.d("GraphFragment", "Skipped chart entry with invalid value");
                                                        }
                                                    }
                                                }
                                                Log.d("GraphFragment", "Total valid chart entries: " + entries.size());
                                                loadColumnChart(entries);
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Log.e("GraphFragment", "Error resolving fastest chart names", e);
                                            }
                                        });
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.e("GraphFragment", "Error loading fastest stat docs", e);
                            }
                        });
            }
        });
    }

    private void loadColumnChart(List<DataEntry> distanceTotals) {
        View view = getView();
        if (view == null) return;

        boolean isStatTimeBased = selectedMetric.equals("1 KM") || selectedMetric.equals("5 KM") || selectedMetric.equals("10 KM");

        List<DataEntry> validEntries = new ArrayList<>();
        for (DataEntry entry : distanceTotals) {
            double value = parseDoubleSafe(entry.getValue("value"));
            if (value > 0 && value != Double.MAX_VALUE) {
                if (isStatTimeBased) {
                    value = value / 1000.0;
                }
                validEntries.add(new ValueDataEntry(entry.getValue("x").toString(), value));
            }
        }

        if (validEntries.isEmpty()) {
            Log.d("GraphFragment", "No valid entries to display on chart.");
            return;
        }

        Collections.sort(validEntries, new Comparator<DataEntry>() {
            @Override
            public int compare(DataEntry value1, DataEntry value2) {
                double firstValue = parseDoubleSafe(value1.getValue("value"));
                double secondValue = parseDoubleSafe(value2.getValue("value"));
                return isStatTimeBased ? Double.compare(firstValue, secondValue) : Double.compare(secondValue, firstValue);
            }
        });

        if (validEntries.size() > 5) {
            validEntries = new ArrayList<>(validEntries.subList(0, 5));
        }

        AnyChartView anyChartView = (AnyChartView) view.findViewById(R.id.any_chart_barchart);
        anyChartView.setProgressBar(view.findViewById(R.id.progressBarChart));

        Cartesian cartesian = AnyChart.column();
        Column column = cartesian.column(validEntries);

        String chartTitle;
        String yAxisTitle;

        if (selectedMetric.equals("Activity Frequency")) {
            chartTitle = "Activity Frequency by Users";
            yAxisTitle = "Activities";
            column.tooltip()
                    .titleFormat("{%X}")
                    .position(Position.CENTER_BOTTOM)
                    .anchor(Anchor.CENTER_BOTTOM)
                    .offsetX(0d)
                    .offsetY(0d)
                    .format("{%Value} activities");
        } else if (isStatTimeBased) {
            chartTitle = "Fastest Times by Users";
            yAxisTitle = "Time (mm:ss)";
            column.tooltip()
                    .titleFormat("{%X}")
                    .position(Position.CENTER_BOTTOM)
                    .anchor(Anchor.CENTER_BOTTOM)
                    .offsetX(0d)
                    .offsetY(0d)
                    .format("function() { " +
                            "var totalSeconds = this.value;" +
                            "var minutes = Math.floor(totalSeconds / 60);" +
                            "var seconds = Math.floor(totalSeconds % 60);" +
                            "return (minutes < 10 ? '0' + minutes : minutes) + ':' + (seconds < 10 ? '0' + seconds : seconds);" +
                            "}");
        } else {
            chartTitle = "Total Distance Ran by Users";
            yAxisTitle = "Distance (KM)";
            column.tooltip()
                    .titleFormat("{%X}")
                    .position(Position.CENTER_BOTTOM)
                    .anchor(Anchor.CENTER_BOTTOM)
                    .offsetX(0d)
                    .offsetY(0d)
                    .format("function() { return (this.value / 1000).toFixed(2) + ' km'; }");
        }

        if (isStatTimeBased) {
            cartesian.yAxis(0).labels().format("function() { " +
                    "var totalSeconds = this.value;" +
                    "var minutes = Math.floor(totalSeconds / 60);" +
                    "var seconds = Math.floor(totalSeconds % 60);" +
                    "return (minutes < 10 ? '0' + minutes : minutes) + ':' + (seconds < 10 ? '0' + seconds : seconds);" +
                    "}");
        } else if (selectedMetric.equals("Activity Frequency")) {
            cartesian.yAxis(0).labels().format("{%Value}");
        } else {
            cartesian.yAxis(0).labels().format("function() { return (this.value / 1000).toFixed(2) + ' km'; }");
        }

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

    private double parseTimeStringToSeconds(String timeString) {
        try {
            String[] parts = timeString.split(":");
            if (parts.length == 2) {
                int minutes = Integer.parseInt(parts[0]);
                int seconds = Integer.parseInt(parts[1]);
                return minutes * 60 + seconds;
            } else if (parts.length == 3) {
                int hours = Integer.parseInt(parts[0]);
                int minutes = Integer.parseInt(parts[1]);
                int seconds = Integer.parseInt(parts[2]);
                return hours * 3600 + minutes * 60 + seconds;
            }
        } catch (NumberFormatException e) {
            Log.w("GraphFragment", "Invalid time format: " + timeString, e);
        }
        return Double.MAX_VALUE;
    }
}
