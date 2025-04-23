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
                            .document(isWeek ? "lastWeek" : "lastMonth");
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

                                    final TaskCompletionSource<DataEntry> tcs = new TaskCompletionSource<>();
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
                            .document(isWeek ? "lastWeek" : "lastMonth");
                    statTasks.add(docRef.get());
                }

                Tasks.whenAllSuccess(statTasks)
                        .addOnSuccessListener(new OnSuccessListener<List<Object>>() {
                            @Override
                            public void onSuccess(List<Object> statsResults) {
                                // 4) Build name+distance Tasks
                                List<Task<DataEntry>> chartTasks = new ArrayList<Task<DataEntry>>();

                                for (int i = 0; i < statsResults.size(); i++) {
                                    DocumentSnapshot snap = (DocumentSnapshot) statsResults.get(i);
                                    final double dist = snap.getDouble("Distance") != null
                                            ? snap.getDouble("Distance")
                                            : 0.0;

                                    // Wrap the name lookup in a TaskCompletionSource
                                    final TaskCompletionSource<DataEntry> tcs = new TaskCompletionSource<>();
                                    chartTasks.add(tcs.getTask());

                                    // Resolve display name, then complete the Task
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

        // 1) Get group members
        groupsUtil.retrieveUsersinGroup(GroupID, new GroupsDatabaseUtil.UsersinGroupsCallback() {
            @Override
            public void onCallback(ArrayList<String> runners) {
                final List<String> userIds = new ArrayList<>(runners);
                List<Task<DocumentSnapshot>> statTasks = new ArrayList<>();

                // 2) Create stat fetch tasks
                for (String uid : userIds) {
                    DocumentReference docRef = db
                            .collection("Users")
                            .document(uid)
                            .collection("Statistics")
                            .document(isWeek ? "lastWeek" : "lastMonth");
                    statTasks.add(docRef.get());
                }

                // 3) Wait for all stats to load
                Tasks.whenAllSuccess(statTasks)
                        .addOnSuccessListener(new OnSuccessListener<List<Object>>() {
                            @Override
                            public void onSuccess(List<Object> statsResults) {
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

                                    Object raw = snap.get(fieldKey);
                                    final double fastest;
                                    if (raw instanceof Number) {
                                        fastest = ((Number) raw).doubleValue();
                                    } else if (raw instanceof String) {
                                        try {
                                            fastest = Double.parseDouble((String) raw);
                                        } catch (NumberFormatException e) {
                                            Log.w("GraphFragment", "Invalid string value for " + fieldKey + ": " + raw);
                                            continue;
                                        }
                                    } else {
                                        Log.w("GraphFragment", "Missing or invalid " + fieldKey + " for user " + uid);
                                        continue;
                                    }

                                    final TaskCompletionSource<DataEntry> tcs = new TaskCompletionSource<>();
                                    chartTasks.add(tcs.getTask());

                                    userUtil.retrieveChatName(uid, new FirebaseDatabaseHelper.ChatUserCallback() {
                                        @Override
                                        public void onCallback(String chatName) {
                                            tcs.setResult(new ValueDataEntry(chatName, fastest));
                                        }
                                    });
                                }

                                // 4) Wait until all chart entry tasks complete
                                Tasks.whenAllSuccess(chartTasks)
                                        .addOnSuccessListener(new OnSuccessListener<List<Object>>() {
                                            @Override
                                            public void onSuccess(List<Object> chartResults) {
                                                List<DataEntry> entries = (List<DataEntry>) (List<?>) chartResults;
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
