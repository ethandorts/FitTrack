package com.example.fittrack;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

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
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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
    private TextView txtGoalDescription, txtTarget, txtProgress, txtDeadline, txtGoalType;
    private TextView txtTotalProgress, txtDaysRemaining, txtProgressNumber, txtAIAdvice;
    private ActivitiesRecyclerViewAdapter activitiesAdapter;
    private RecyclerView recyclerGoalEfforts;
    private ProgressBar aiProgressBar;
    private Cartesian cartesian;
    private AnyChartView progressLine;
    private String lastMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_goal_progress_report);

        txtGoalType = findViewById(R.id.txtGoalType);
        txtGoalDescription = findViewById(R.id.txtGoalDescription);
        txtTarget = findViewById(R.id.txtTarget);
        txtProgress = findViewById(R.id.txtProgress);
        txtDeadline = findViewById(R.id.txtDeadline);
        txtTotalProgress = findViewById(R.id.txtTotalProgress);
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
        cartesian.yScale().minimum(0); // Always start from 0

        cartesian.tooltip()
                .positionMode(TooltipPositionMode.POINT)
                .anchor(Anchor.RIGHT_TOP)
                .offsetX(5d)
                .offsetY(5d);

        cartesian.xAxis(0).labels().padding(5d, 5d, 5d, 5d);
        cartesian.tooltip(false);
        progressLine.setChart(cartesian);

        Intent intent = getIntent();
        String GoalID = intent.getStringExtra("GoalID");

        goalsUtil.retrieveGoalSpecificDescription(UserID, GoalID, new GoalsUtil.SpecificGoalCallback() {
            @Override
            public void onCallback(double targetDistance, int targetTime, String status, String goalType, int currentProgress, Timestamp startDate, Timestamp endDate, String description) {
                txtGoalType.setText("🛣️ Distance Goal");
                txtProgress.setText("\uD83D\uDCC8 Progress: " + status);
                txtGoalDescription.setText(description);
                txtTarget.setText(String.format("\uD83C\uDFAF Target: %.2f KM", targetDistance / 1000));
                txtDeadline.setText("⏳ Deadline: " + ConversionUtil.AltTimestamptoString(endDate));

                Query query = db.collection("Activities")
                        .whereEqualTo("UserID", UserID)
                        .whereEqualTo("type", "Running")
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

                gamUtil.getActivityProgressData(UserID, startDate, endDate, new GamificationUtil.ProgressCallback() {
                    @Override
                    public void onCallback(ArrayList<ActivityModel> activities) {
                        double totalDistance = 0;
                        for(ActivityModel activity : activities) {
                            totalDistance = totalDistance + Double.parseDouble(activity.getDistance());
                        }
                        txtTotalProgress.setText("Total Progress: " + String.format("%.2f", totalDistance / 1000) + " / " + String.format("%.2f", targetDistance / 1000) + " KM");

                        Timestamp now = Timestamp.now();

                        long millisNow = now.toDate().getTime();
                        long millisEnd = endDate.toDate().getTime();

                        long diffInMillis = millisEnd - millisNow;
                        long daysRemaining = Math.max(diffInMillis / (1000 * 60 * 60 * 24), 0);

                        txtDaysRemaining.setText("Days Remaining: " + daysRemaining + " Days");
                        double totalDistanceKm = totalDistance / 1000;
                        double targetDistanceKm = targetDistance / 1000;

                        double rawPercentage = (totalDistanceKm / targetDistanceKm) * 100;
                        int progressPercentage = (int) Math.min(rawPercentage, 100);

                        txtProgressNumber.setText("Progress: " + progressPercentage + "%");

                        userUtil.retrieveFitnessLevel(UserID, new FirebaseDatabaseHelper.ChatUserCallback() {
                            @Override
                            public void onCallback(String fitnessLevel) {
                                GetFitnessAdvice(fitnessLevel, description, activities);
                            }
                        });

//                        System.out.println(activities.toString());
//                        if (activities.isEmpty()) return;
//
//                        List<DataEntry> data = new ArrayList<>();
//                        List<String> xLabels = new ArrayList<>();
//                        double totalDistance = 0;
//                        double maxDistanceReached = 0;
//
//                        // Sort by date
//                        Collections.sort(activities, Comparator.comparing(ActivityModel::getDate));
//
//                        for (ActivityModel activity : activities) {
//                            double distance = Double.parseDouble(activity.getDistance());
//                            totalDistance += distance;
//
//                            String date = ConversionUtil.dateFormatter(activity.getDate());
//                            xLabels.add(date);
//                            data.add(new ValueDataEntry(date, totalDistance));
//
//                            if (totalDistance > maxDistanceReached) {
//                                maxDistanceReached = totalDistance;
//                            }
//                        }
//
//                        cartesian.data(data);
//
//                        // Dynamically set Y-axis max to max(user progress, goal)
//                        double goalKm = targetDistance / 1000;
//                        double yAxisMax = Math.max(goalKm, maxDistanceReached);
//                        cartesian.yScale().maximum(yAxisMax);
//
//                        // Add red target line at the goal
//                        if (!xLabels.isEmpty()) {
//                            Line targetLine = cartesian.line(new ArrayList<DataEntry>() {{
//                                add(new ValueDataEntry(xLabels.get(0), goalKm));
//                                add(new ValueDataEntry(xLabels.get(xLabels.size() - 1), goalKm));
//                            }});
//                            targetLine.stroke("3 red");
//                            targetLine.tooltip().enabled(false);
//                            targetLine.hovered().markers().enabled(false);
//                            targetLine.name("🎯 Target");
//                        }
//
//                        cartesian.legend().enabled(true);
                    }
                });
            }
        });
    }

    public void GetFitnessAdvice(String fitnessLevel, String goalDescription, ArrayList<ActivityModel> activitiesInfo) {
        aiProgressBar.setVisibility(View.VISIBLE);
        txtAIAdvice.setText(" ");
        JSONObject body = new JSONObject();
        JSONArray messagesArray = new JSONArray();

        String question = "You are an AI Fitness Coach. Based on the following information I have provided of activities I have completed: \n " +
                activitiesInfo.toString() +
                "My fitness level type is: " + fitnessLevel +
                ". Please suggest ways in which such as a training plan or technique to achieve my fitness goal which is " +
                goalDescription + ". " +
                "";


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
                            txtAIAdvice.setText(message);
                            System.out.println(message);
                            lastMessage = message;

//                            editMessage.setVisibility(View.VISIBLE);
//                            btnSendRequest.setVisibility(View.VISIBLE);
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
    protected void onStop() {
        super.onStop();
        if (activitiesAdapter != null) {
            activitiesAdapter.stopListening();
        }
    }

}
