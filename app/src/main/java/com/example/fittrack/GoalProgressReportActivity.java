package com.example.fittrack;

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
                    Query timeQuery = db.collection("Activities")
                            .whereEqualTo("UserID", UserID)
                            .whereEqualTo("type", activityType)
                            .whereGreaterThanOrEqualTo("date", startDate)
                            .whereLessThanOrEqualTo("date", endDate)
                            .orderBy("date", Query.Direction.DESCENDING);

                    timeQuery.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot querySnapshot) {
                            ArrayList<ActivityModel> activitiesList = new ArrayList<>();
                            System.out.println("Queries: " + querySnapshot.size());
                            for(DocumentSnapshot doc : querySnapshot) {
                                if(Double.parseDouble((String) doc.get("distance")) > targetDistance) {
                                    String activityID = (String) doc.get("ActivityID");
                                    String type = (String) doc.get("type");
                                    Timestamp date = (Timestamp) doc.get("date");
                                    String pace = (String) doc.get("pace");
                                    String distance = (String) doc.get("distance");
                                    List<Long> splits = (List<Long>) doc.get("splits");
                                    List<Object> activityCoordinates = (List<Object>) doc.get("activityCoordinates");
                                    double time = doc.getDouble("time") != null ? doc.getDouble("time") : 0.0;

                                    ActivityModel model = new ActivityModel(type, null, date, String.valueOf(distance), time, pace, UserID, null, activityCoordinates, activityID, splits);
                                    activitiesList.add(model);
                                }
                            }
                            ManualActivityRecyclerAdapter adapter = new ManualActivityRecyclerAdapter(GoalProgressReportActivity.this, activitiesList);
                            recyclerGoalEfforts.setAdapter(adapter);
                            recyclerGoalEfforts.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                            recyclerGoalEfforts.addItemDecoration(new DividerItemDecoration(getApplicationContext(), DividerItemDecoration.HORIZONTAL));
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            System.out.println("Error: " + e.getMessage());
                        }
                    });
                } else if (goalType.equals("Distance")) {
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

                gamUtil.getActivityProgressData(UserID, activityType, startDate, endDate, new GamificationUtil.ProgressCallback() {
                    @Override
                    public void onCallback(ArrayList<ActivityModel> activities) {
                        double totalDistance = 0;

                        if(goalType.equals("Distance")) {
                            for(ActivityModel activity : activities) {
                                totalDistance = totalDistance + Double.parseDouble(activity.getDistance());
                            }
                            txtTotalProgress.setText("Total Progress: " + String.format("%.2f", totalDistance / 1000) + " / " + String.format("%.2f", targetDistance / 1000) + " KM");
                            txtBestPace.setVisibility(View.GONE);
                            double totalDistanceKm = totalDistance / 1000;
                            double targetDistanceKm = targetDistance / 1000;

                            double rawPercentage = (totalDistanceKm / targetDistanceKm) * 100;
                            int progressPercentage = (int) Math.min(rawPercentage, 100);
                            txtProgressNumber.setText("Progress: " + progressPercentage + "%");
                        } else if(goalType.equals("Time")) {
                            long bestTime = Long.MAX_VALUE;
                            boolean qualifyActivities = false;
                            for(ActivityModel activity : activities) {
                                if (Double.parseDouble(activity.getDistance()) >= targetDistance) {
                                    qualifyActivities = true;
                                    List<Long> splits = activity.getSplits();
                                    long splitTime = 0;
                                    for (Long split : splits) {
                                        splitTime = splitTime + split;
                                    }
                                    if (splitTime < bestTime) {
                                        bestTime = splitTime;
                                    }
                                }
                            }
                            String formattedBestTime = longToTimeConversion(bestTime);
                            txtTotalProgress.setText("Best Time: " + formattedBestTime);

                            if (qualifyActivities == false) {
                                txtTotalProgress.setText("No qualifying activities yet.");
                                txtBestPace.setVisibility(View.GONE);
                                txtProgressNumber.setVisibility(View.GONE);
                            } else {
                                txtBestPace.setVisibility(View.VISIBLE);
                                txtProgressNumber.setVisibility(View.VISIBLE);
                            }

                            long target = targetTime * 1000L;
                            long timeDifference = bestTime - target;
                            String label;

                            if (timeDifference <= 0) {
                                label = "Faster by " + longToTimeConversion(Math.abs(timeDifference));
                            } else {
                                label = "Slower by " + longToTimeConversion(timeDifference);
                            }
                            txtBestPace.setText(label);

                            double doubleTarget = Double.parseDouble(String.valueOf(target));
                            double doubleBest = Double.parseDouble(String.valueOf(bestTime));
                            double progress = doubleTarget / doubleBest * 100;
                            String formattedProgress = String.valueOf((int) progress);
                            if (progress > 100) {
                                progress = 100;
                            }
                            txtProgressNumber.setText("Progress: " + formattedProgress + "%");
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

                        System.out.println(activities.toString());
                        if (activities.isEmpty()) {
                            txtTotalProgress.setText("No qualifying activities yet.");
                            txtBestPace.setVisibility(View.GONE);
                            txtProgressNumber.setVisibility(View.GONE);
                            return;
                        }

                        System.out.println( "Activities :" + activities.size());

                        if(goalType.equals("Distance")) {
                            List<DataEntry> data = new ArrayList<>();
                            List<String> xLabels = new ArrayList<>();
                            double cummulativeDistance = 0;
                            double maxDistanceReached = 0;

                            Collections.sort(activities, Comparator.comparing(ActivityModel::getDate));

                            for (ActivityModel activity : activities) {
                                double distance = Double.parseDouble(activity.getDistance());
                                cummulativeDistance += distance;

                                String date = ConversionUtil.dateFormatter(activity.getDate());
                                xLabels.add(date);
                                data.add(new ValueDataEntry(date, cummulativeDistance / 1000));
                                System.out.println(cummulativeDistance + "TotalDistance ");

                                if (cummulativeDistance > maxDistanceReached) {
                                    maxDistanceReached = cummulativeDistance;
                                }
                            }
                            cartesian.data(data);
                            double goalKm = targetDistance / 1000;
                            double yAxisMax = Math.max(goalKm, maxDistanceReached / 1000);
                            cartesian.yScale().maximum(yAxisMax);
                            cartesian.legend().enabled(true);
                            progressLine.setChart(cartesian);
                        } else if (goalType.equals("Time")) {
                            cartesian = AnyChart.column();
                            progressLine.setChart(cartesian);
                            cartesian.animation(true);
                            cartesian.title("Top 5 Quickest Times");
                            cartesian.yAxis(0).title("Time (mm:ss)");
                            cartesian.xAxis(0).title("Date");

                            List<ActivityModel> qualifyingActivities = new ArrayList<>();
                            for (ActivityModel activity : activities) {
                                if (Double.parseDouble(activity.getDistance()) >= targetDistance) {
                                    qualifyingActivities.add(activity);
                                }
                            }

                            qualifyingActivities.sort((a, b) -> {
                                long timeA = a.getSplits().stream().mapToLong(Long::longValue).sum();
                                long timeB = b.getSplits().stream().mapToLong(Long::longValue).sum();
                                return Long.compare(timeA, timeB);
                            });

                            List<ActivityModel> top5 = qualifyingActivities.subList(0, Math.min(5, qualifyingActivities.size()));

                            List<DataEntry> data = new ArrayList<>();
                            for (ActivityModel activity : top5) {
                                long splitTime = activity.getSplits().stream().mapToLong(Long::longValue).sum();
                                int timeInSeconds = (int) (splitTime / 1000);
                                String date = ConversionUtil.dateFormatter(activity.getDate());
                                data.add(new ValueDataEntry(date, timeInSeconds));
                            }

                            cartesian.data(data);

                            cartesian.tooltip()
                                    .format("function() { var m = Math.floor(this.value / 60); var s = ('0' + (this.value % 60)).slice(-2); return 'Time: ' + m + ':' + s; }");

                            cartesian.yAxis(0).labels()
                                    .format("function() { return Math.floor(this.value / 60) + ':' + ('0' + (this.value % 60)).slice(-2); }");

                            cartesian.yScale().minimum(0);
                            cartesian.legend().enabled(false);

                            progressLine.setChart(cartesian);
                        }
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
                    }
                });
            }
        });
    }

    public void GetFitnessAdvice(String fitnessLevel, String goalDescription, String endDate,  ArrayList<ActivityModel> activitiesInfo) {
        aiProgressBar.setVisibility(View.VISIBLE);
        txtAIAdvice.setText(" ");
        JSONObject body = new JSONObject();
        JSONArray messagesArray = new JSONArray();

                String question = "You are an AI Fitness Coach. Based on the following information I have provided of activities I have completed: \n" +
                activitiesInfo.toString() +
                "My fitness level type is: " + fitnessLevel +
                ". Please suggest ways in which I can achieve my fitness goal which is " +
                goalDescription + ", before the date of " + endDate +
                ".";


//        String question = "You are an AI Fitness Coach. Based on the following information I have provided of activities I have completed: \n" +
//                activitiesInfo.toString() +
//                "My fitness level type is: " + fitnessLevel +
//                ". Please suggest ways in which such as a training plan or technique to achieve my fitness goal which is " +
//                goalDescription + ", before the date of " + endDate +
//                ". Please provide me a plan from today to the date provided here: " + endDate;


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
