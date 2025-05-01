package com.example.fittrack;

import static com.example.fittrack.ConversionUtil.cleanAIResponse;
import static com.example.fittrack.ConversionUtil.longToTimeConversion;

import android.app.people.ConversationStatus;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.anychart.APIlib;
import com.anychart.AnyChart;
import com.anychart.AnyChartView;
import com.anychart.chart.common.dataentry.DataEntry;
import com.anychart.chart.common.dataentry.ValueDataEntry;
import com.anychart.charts.Cartesian;
import com.anychart.core.axes.Linear;
import com.anychart.core.cartesian.series.Line;
import com.anychart.enums.Anchor;
import com.anychart.enums.TooltipPositionMode;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GoalProgressReportActivity extends AppCompatActivity {
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private GoalsUtil goalsUtil = new GoalsUtil(db);
    private GamificationUtil gamUtil = new GamificationUtil();
    private FirebaseDatabaseHelper userUtil = new FirebaseDatabaseHelper(db);
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private String UserID = mAuth.getUid();
    private TextView txtGoalDescription, txtTarget, txtProgress, txtDeadline, txtGoalType, txtActivityType;
    private TextView txtTotalProgress, txtBestPace, txtDaysRemaining, txtProgressNumber, txtAIAdvice;
    private ActivitiesRecyclerViewAdapter activitiesAdapter;
    private RecyclerView recyclerGoalEfforts;
    private ProgressBar aiProgressBar;
    private Cartesian cartesian;
    private AnyChartView progressLine;
    private String lastMessage;
    private ArrayList<ActivityModel> activitiesList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_goal_progress_report);

        txtGoalType = findViewById(R.id.txtGoalType);
        txtGoalDescription = findViewById(R.id.txtGoalDescription);
        txtTarget = findViewById(R.id.txtTarget);
        txtActivityType = findViewById(R.id.txtActivityTypeGoal);
        txtProgress = findViewById(R.id.txtProgress);
        txtDeadline = findViewById(R.id.txtDeadline);
        txtTotalProgress = findViewById(R.id.txtTotalProgress);
        txtBestPace = findViewById(R.id.txtAveragePace);
        txtDaysRemaining = findViewById(R.id.txtDaysRemaining);
        txtProgressNumber = findViewById(R.id.txtProgressPercentage);
        txtAIAdvice = findViewById(R.id.txtAIAdvice);

        recyclerGoalEfforts = findViewById(R.id.recyclerGoalEfforts);
        aiProgressBar = findViewById(R.id.aiGoalAdvice);

        progressLine = findViewById(R.id.progressLineChart);
        APIlib.getInstance().setActiveAnyChartView(progressLine);

        cartesian = AnyChart.line();
        cartesian.animation(true);
        cartesian.title("Distance Progress");
        cartesian.yAxis(0).title("Distance (km)");
        cartesian.xAxis(0).title("Date");

        Linear yAxis = cartesian.yAxis(0);
        cartesian.yScale().minimum(0);

        cartesian.tooltip()
                .positionMode(TooltipPositionMode.POINT)
                .anchor(Anchor.RIGHT_TOP)
                .offsetX(5d)
                .offsetY(5d);

        cartesian.xAxis(0).labels().padding(5d, 5d, 5d, 5d);

        Intent intent = getIntent();
        String GoalID = intent.getStringExtra("GoalID");
        String ActivityType = intent.getStringExtra("activityType");

        goalsUtil.retrieveGoalSpecificDescription(UserID, GoalID, new GoalsUtil.SpecificGoalCallback() {
            @Override
            public void onCallback(double targetDistance, int targetTime, String status, String goalType, int currentProgress, Timestamp startDate, Timestamp endDate, String description, String activityType) {
                System.out.println(goalType + " goal");
                if(goalType.equals("Distance")) {
                    txtGoalType.setText("🛣️ Distance Goal");
                } else if (goalType.equals("Time")) {
                    txtGoalType.setText("⏰ Time Goal");
                }
                txtActivityType.setText("\uD83D\uDCAA Activity Type: " + ActivityType);
                txtProgress.setText("\uD83D\uDCC8 Progress: " + status);
                txtGoalDescription.setText(description);
                txtTarget.setText(String.format("\uD83C\uDFAF Target: %.2f KM", targetDistance / 1000));
                txtDeadline.setText("⏳ Deadline: " + ConversionUtil.AltTimestamptoString(endDate));

                if(goalType.equals("Time")) {
                    retrieveTimeGoal(targetDistance, targetTime, activityType, startDate, endDate);
                } else if (goalType.equals("Distance")) {
                    retrieveDistanceGoal(activityType, startDate, endDate, targetDistance);
                }

                gamUtil.getActivityProgressData(UserID, activityType, startDate, endDate, new GamificationUtil.ProgressCallback() {
                    @Override
                    public void onCallback(ArrayList<ActivityModel> activities) {
                        if(goalType.equals("Distance")) {
                            showDistanceProgress(activities, targetDistance);
                        }

                        Timestamp now = Timestamp.now();
                        long millisNow = now.toDate().getTime();
                        long millisEnd = endDate.toDate().getTime();
                        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                        String formattedDate = sdf.format(new Date(millisEnd));
                        long diffInMillis = millisEnd - millisNow;
                        long daysRemaining = Math.max(diffInMillis / (1000 * 60 * 60 * 24), 0);
                        txtDaysRemaining.setText("Days Remaining: " + daysRemaining + " Days");


                        userUtil.retrieveFitnessLevel(UserID, new FirebaseDatabaseHelper.ChatUserCallback() {
                            @Override
                            public void onCallback(String fitnessLevel) {
                                GetFitnessAdvice(fitnessLevel, description, formattedDate, activities);
                            }
                        });

                        if (activities.isEmpty()) {
                            txtTotalProgress.setText("No qualifying activities yet.");
                            txtBestPace.setVisibility(View.GONE);
                            txtProgressNumber.setVisibility(View.GONE);
                        }
                    }
                });
            }
        });
    }

    private void retrieveTimeGoal(double targetDistance, int targetTime, String activityType, Timestamp startDate, Timestamp endDate) {
        Query timeQuery = db.collection("Activities")
                .whereEqualTo("UserID", UserID)
                .whereEqualTo("type", activityType)
                .whereGreaterThanOrEqualTo("date", startDate)
                .whereLessThanOrEqualTo("date", endDate)
                .orderBy("date", Query.Direction.DESCENDING);

        timeQuery.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot querySnapshot) {
                ArrayList<ActivityModel> qualifyingActivities = new ArrayList<>();
                List<ActivityTimeRecord> allQualifyingTimes = new ArrayList<>();
                double requiredDistanceMeters = targetDistance;

                for (DocumentSnapshot document : querySnapshot) {
                    try {
                        String distance = (String) document.get("distance");
                        double distanceValue = Double.parseDouble(distance);

                        if (distanceValue >= requiredDistanceMeters) {
                            String activityID = (String) document.get("ActivityID");
                            String type = (String) document.get("type");
                            Timestamp date = document.getTimestamp("date");
                            String pace = (String) document.get("pace");
                            List<Long> splits = (List<Long>) document.get("splits");
                            List<Object> activityCoordinates = (List<Object>) document.get("activityCoordinates");
                            double time = document.getDouble("time") != null ? document.getDouble("time") : 0.0;

                            ActivityModel model = new ActivityModel(type, null, date, distance, time, pace, UserID, null, activityCoordinates, activityID, splits);
                            qualifyingActivities.add(model);

                            if (splits != null) {
                                int requiredSplits = (int) (targetDistance / 1000);
                                boolean isExactDistance = Math.abs(distanceValue - requiredDistanceMeters) < 1;

                                if (isExactDistance && splits.size() == requiredSplits) {
                                    long totalTime = 0;
                                    for (Long split : splits) {
                                        totalTime += split;
                                    }
                                    String label = ConversionUtil.dateFormatter(date);
                                    allQualifyingTimes.add(new ActivityTimeRecord(totalTime, label, date));
                                } else {
                                    int completeSplitsAvailable = (int) (distanceValue / 1000);
                                    int segmentsToCheck = Math.min(completeSplitsAvailable - requiredSplits + 1,
                                            splits.size() - requiredSplits + 1);

                                    for (int i = 0; i < segmentsToCheck; i++) {
                                        long segmentTime = 0;
                                        boolean validSegment = true;
                                        for (int j = 0; j < requiredSplits; j++) {
                                            if (i + j >= completeSplitsAvailable) {
                                                validSegment = false;
                                                break;
                                            }
                                            segmentTime += splits.get(i + j);
                                        }

                                        if (validSegment) {
                                            String segmentLabel = ConversionUtil.dateFormatter(date);
                                            allQualifyingTimes.add(new ActivityTimeRecord(
                                                    segmentTime,
                                                    segmentLabel,
                                                    date
                                            ));
                                        }
                                    }
                                }
                            }
                        }
                    } catch (Exception e) {
                        System.out.println("Error parsing document: " + e.getMessage());
                    }
                }

                Collections.sort(allQualifyingTimes, new Comparator<ActivityTimeRecord>() {
                    @Override
                    public int compare(ActivityTimeRecord a, ActivityTimeRecord b) {
                        return Long.compare(a.getTime(), b.getTime());
                    }
                });

                List<ActivityTimeRecord> top5Times = allQualifyingTimes.subList(
                        0, Math.min(5, allQualifyingTimes.size()));

                if (!top5Times.isEmpty()) {
                    ActivityTimeRecord bestTimeRecord = top5Times.get(0);
                    txtTotalProgress.setText("Best Time: " + longToTimeConversion(bestTimeRecord.getTime()) + " on " + bestTimeRecord.getLabel());

                    long timeDifference = bestTimeRecord.getTime() - (targetTime * 1000L);
                    String differenceLabel = timeDifference <= 0 ?
                            "Faster by " + longToTimeConversion(Math.abs(timeDifference)) :
                            "Slower by " + longToTimeConversion(timeDifference);
                    txtBestPace.setText(differenceLabel);

                    double progress = ((double)(targetTime * 1000L) / bestTimeRecord.getTime()) * 100;
                    txtProgressNumber.setText("Progress: " + (int)Math.min(progress, 100) + "%");
                } else {
                    txtTotalProgress.setText("No qualifying activities yet.");
                    txtBestPace.setVisibility(View.GONE);
                    txtProgressNumber.setVisibility(View.GONE);
                }
                ManualActivityRecyclerAdapter adapter = new ManualActivityRecyclerAdapter(
                        GoalProgressReportActivity.this, qualifyingActivities);
                recyclerGoalEfforts.setAdapter(adapter);
                recyclerGoalEfforts.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                recyclerGoalEfforts.addItemDecoration(new DividerItemDecoration(
                        getApplicationContext(), DividerItemDecoration.HORIZONTAL));

                createTimeChart(top5Times);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        });
    }

    private void createTimeChart(List<ActivityTimeRecord> top5Times) {
        cartesian = AnyChart.column();
        progressLine.setChart(cartesian);
        cartesian.animation(true);
        cartesian.title("Top 5 Quickest Times");
        cartesian.yAxis(0).title("Time (mm:ss)");
        cartesian.xAxis(0).title("Date");

        List<DataEntry> data = new ArrayList<>();
        for (ActivityTimeRecord record : top5Times) {
            int timeInSeconds = (int) (record.getTime() / 1000);
            data.add(new ValueDataEntry(record.getLabel(), timeInSeconds));
        }

        cartesian.data(data);
        cartesian.tooltip()
                .format("function() { var m = Math.floor(this.value / 60); " + "var s = ('0' + (this.value % 60)).slice(-2); " + "return 'Time: ' + m + ':' + s; }");

        cartesian.yAxis(0).labels()
                .format("function() { return Math.floor(this.value / 60) + ':' + " + "('0' + (this.value % 60)).slice(-2); }");

        cartesian.yScale().minimum(0);
        cartesian.legend().enabled(false);
        progressLine.setChart(cartesian);
    }

    private void retrieveDistanceGoal(String activityType, Timestamp startDate, Timestamp endDate, double targetDistance) {
        Query query = db.collection("Activities")
                .whereEqualTo("UserID", UserID)
                .whereEqualTo("type", activityType)
                .whereGreaterThanOrEqualTo("date", startDate)
                .whereLessThanOrEqualTo("date", endDate)
                .orderBy("date", Query.Direction.DESCENDING);

        FirestoreRecyclerOptions<ActivityModel> options = new FirestoreRecyclerOptions.Builder<ActivityModel>()
                .setQuery(query, ActivityModel.class)
                .build();

        activitiesAdapter = new ActivitiesRecyclerViewAdapter(options, GoalProgressReportActivity.this);
        recyclerGoalEfforts.setAdapter(activitiesAdapter);

        LinearLayoutManager layout = new LinearLayoutManager(getApplicationContext());
        recyclerGoalEfforts.setLayoutManager(layout);
        recyclerGoalEfforts.addItemDecoration(new DividerItemDecoration(getApplicationContext(), DividerItemDecoration.HORIZONTAL));
        activitiesAdapter.startListening();
    }

    private void showDistanceProgress(ArrayList<ActivityModel> activities, double targetDistance) {
        double totalDistance = 0;
        for(ActivityModel activity : activities) {
            totalDistance = totalDistance + Double.parseDouble(activity.getDistance());
        }
        txtTotalProgress.setText("Total Progress: " + String.format("%.2f", totalDistance / 1000) + " / " + String.format("%.2f", targetDistance / 1000) + " KM");
        txtBestPace.setVisibility(View.GONE);
        double totalDistanceKm = totalDistance / 1000;
        double targetDistanceKm = targetDistance / 1000;

        double percentage = (totalDistanceKm / targetDistanceKm) * 100;
        int progressPercentage = (int) Math.min(percentage, 100);
        txtProgressNumber.setText("Progress: " + progressPercentage + "%");

        createDistanceChart(activities, targetDistance);
    }

    private void createDistanceChart(ArrayList<ActivityModel> activities, double targetDistance) {
        List<DataEntry> data = new ArrayList<>();
        List<String> xLabels = new ArrayList<>();
        double cumulativeDistance = 0;
        double maxDistanceReached = 0;

        Collections.sort(activities, Comparator.comparing(ActivityModel::getDate));

        for (ActivityModel activity : activities) {
            double distance = Double.parseDouble(activity.getDistance());
            cumulativeDistance += distance;

            String date = ConversionUtil.dateFormatter(activity.getDate());
            xLabels.add(date);
            data.add(new ValueDataEntry(date, cumulativeDistance / 1000));

            if (cumulativeDistance > maxDistanceReached) {
                maxDistanceReached = cumulativeDistance;
            }
        }

        cartesian.line(data).name("Distance");
        double goalKm = targetDistance / 1000;
        double yAxisMax = Math.max(goalKm, maxDistanceReached / 1000);
        cartesian.yScale().maximum(yAxisMax);
        cartesian.legend().enabled(true);
        progressLine.setChart(cartesian);
    }

    public void GetFitnessAdvice(String fitnessLevel, String goalDescription, String endDate, ArrayList<ActivityModel> activitiesInfo) {
        aiProgressBar.setVisibility(View.VISIBLE);
        txtAIAdvice.setText(" ");
        JSONObject body = new JSONObject();
        JSONArray messagesArray = new JSONArray();

        StringBuilder activitiesDetails = new StringBuilder();
        for(ActivityModel activity : activitiesInfo) {
            activitiesDetails.append("Activity: ").append(activity.getType())
                    .append(", Distance: ").append(activity.getDistance()).append("metres")
                    .append(", Duration: ").append(ConversionUtil.convertSecondsToTime((int) activity.getTime())).append("seconds")
                    .append(", Date: ").append(activity.getDate())
                    .append(", Splits: ").append(activity.getSplits()).append("in milliseconds")
                    .append(", Average Pace: ").append(activity.getPace()).append(" /km")
                    .append("\n");
        }

        String question = "You are an expert AI Fitness Coach. Based on the following detailed activity history:\n\n" +
                activitiesDetails.toString() +
                "\n\nMy current fitness level is: " + fitnessLevel + "." +
                "\nMy fitness goal is: " + goalDescription + ", which I aim to achieve by: " + endDate + "." +
                "My total progress is " + txtTotalProgress.getText().toString() + " and the progress number is " + txtProgressNumber.getText().toString() +
                "\n\nPlease perform the following tasks:\n" +
                "1. Analyze each of my completed activities individually, identifying strengths and weaknesses for each activity. Whenever you analyse put the seconds into minutes and seconds.\n" +
                "2. Summarize overall trends and patterns in my training (e.g., consistency, pacing, volume).\n" +
                "3. Identify specific areas I need to improve based on my goal and my current performance.\n" +
                "4. Recommend targeted training sessions I should focus on (e.g., endurance runs, speed intervals, recovery days).\n" +
                "5. Provide a detailed, realistic weekly training plan for me, from today until my goal deadline, based on my history and fitness level.\n" +
                "6. Ensure advice is specific, actionable, and personalized based on my previous performance data.\n\n" +
                "Be highly detailed, structured, critical but encouraging, and prioritize practical, realistic improvements." +
                " In your response, can you remove all unnecessary characters like ### and *";

        try {
            JSONObject aiDescription = new JSONObject();
            aiDescription.put("role", "system");
            aiDescription.put("content", question);
            messagesArray.put(aiDescription);
            if (lastMessage != null && !lastMessage.isEmpty()) {
                JSONObject previousMessage = new JSONObject();
                previousMessage.put("role", "assistant");
                previousMessage.put("content", lastMessage);
                messagesArray.put(previousMessage);
            }
            JSONObject userMessage = new JSONObject();
            userMessage.put("role", "user");
            userMessage.put("content", question);
            messagesArray.put(userMessage);

            body.put("model", "gpt-3.5-turbo");
            body.put("messages", messagesArray);

        } catch (JSONException e) {
            System.out.println("Error creating request body: " + e);
        }

        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST,
                "https://api.openai.com/v1/chat/completions",
                body,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        try {
                            aiProgressBar.setVisibility(View.GONE);
                            JSONArray choicesArray = jsonObject.getJSONArray("choices");
                            JSONObject choiceObject = choicesArray.getJSONObject(0);
                            JSONObject messageObject = choiceObject.getJSONObject("message");

                            String message = messageObject.getString("content");
                            txtAIAdvice.setText(cleanAIResponse(message));
                            System.out.println(message);
                            lastMessage = message;
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        System.out.println("AI failure: " + error + " " + error.getMessage());
                    }
                }
        ) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headersMap = new HashMap<>();
                headersMap.put("Content-Type", "application/json");
                headersMap.put("Authorization", "Bearer ");
                return headersMap;
            }

            @Override
            public RetryPolicy getRetryPolicy() {
                return new DefaultRetryPolicy(60000, 3, 1);
            }
        };
        requestQueue.add(request);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (progressLine != null) {
            progressLine.invalidate();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (activitiesAdapter != null) {
            activitiesAdapter.stopListening();
        }
    }
}