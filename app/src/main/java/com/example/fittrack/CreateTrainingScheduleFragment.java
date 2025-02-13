package com.example.fittrack;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CreateTrainingScheduleFragment extends Fragment {
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private String UserID = mAuth.getUid();
    private String lastMessage;
    private TextView txtResponse;
    private ImageButton btnSendRequest;
    private EditText editMessage;
    private GoalsUtil goalsUtil = new GoalsUtil(db);
    private String goalsList = " ";
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        txtResponse = view.findViewById(R.id.txtAIResponse);
        btnSendRequest = view.findViewById(R.id.btnSendRequest);
        editMessage = view.findViewById(R.id.editModify);

        editMessage.setVisibility(View.GONE);
        btnSendRequest.setVisibility(View.GONE);

        goalsUtil.retrieveUserGoals(UserID, new GoalsUtil.GoalsCallback() {
            @Override
            public void onCallback(List<String> goals) {
                for(String goal : goals) {
                    goalsList = goalsList + goal + ". ";
                }

                Date date = new Date();
                SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
                String formattedDate = formatter.format(date);

                System.out.println(getContext() + " Context");

                AskFitTrackCoachingAssistant("Create a training schedule for the next week." +
                        "Today's date is " + formattedDate + ". Here is my information, I weigh " + getUserWeight() +
                        "kg and I am " + getUserHeight() + "cm tall. " +
                        "My goals are " + goalsList + "." + "Other Considerations is none. \n" +
                        "\n" +
                        "I would like to complete " + getActivityFrequency() + " fitness activities per week only. " +
                        "Display the training schedule in this format strictly with no asterixes following words, for example:\n" +
                        "\n" +
                        "Date: 24/10/2024\n" +
                        "Activity Type: Running\n" +
                        "Activity Title: 5KM Run (describe the distance and activity)\n" +
                        "Details: Tempo Run (describe the details of the run)\n");
            }
        });

        if(txtResponse.getText().length() > 0) {
            editMessage.setVisibility(View.VISIBLE);
            btnSendRequest.setVisibility(View.VISIBLE);
        }

        btnSendRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String newQuestion = editMessage.getText().toString();
                AskFitTrackCoachingAssistant("This was your last response: " + lastMessage +
                        ". " + "\n" +
                        "Use this information to respond to the question below. " + "\n" +
                        newQuestion + " \n" +
                        "If a modified training schedule is necessary, display a training schedule in this format, for example:\n" +
                        "\n" +
                        "Date: 24/10/2024\n" +
                        "Activity Type: Running\n" +
                        "Activity Title: 5KM Run\n" +
                        "Details: Tempo Run\n");
                editMessage.setText("");
            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_create_training_schedule, container, false);
    }

    public void AskFitTrackCoachingAssistant(String question) {
        JSONObject body = new JSONObject();
        JSONArray messagesArray = new JSONArray();

        try {
            JSONObject aiDescription = new JSONObject();
            aiDescription.put("role", "system");
            aiDescription.put("content", "You are an AI Fitness Coaching Assistant that specialises in creating personalised fitness training workout schedules.");
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

        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST,
                "https://api.openai.com/v1/chat/completions",
                body,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        try {
                            JSONArray choicesArray = jsonObject.getJSONArray("choices");
                            JSONObject choiceObject = choicesArray.getJSONObject(0);
                            JSONObject messageObject = choiceObject.getJSONObject("message");

                            String message = messageObject.getString("content");
                            txtResponse.setText(message);
                            System.out.println(message);
                            //extractAIRoutine(message);
                            lastMessage = message;

                            editMessage.setVisibility(View.VISIBLE);
                            btnSendRequest.setVisibility(View.VISIBLE);

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


    private long getUserWeight() {
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("UserPI", Context.MODE_PRIVATE);
        return sharedPreferences.getLong("Weight", 0);
    }

    private long getUserHeight() {
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("UserPI", Context.MODE_PRIVATE);
        return sharedPreferences.getLong("Height", 0);
    }

    private long getActivityFrequency() {
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("UserPI", Context.MODE_PRIVATE);
        return sharedPreferences.getLong("ActivityFrequency", 0);
    }

    private void addToCalendar(String activityType, String dateTime, String eventName, String description) {
        Map<String, Object> activityEvent = new HashMap<>();
        activityEvent.put("ActivityType", activityType);
        activityEvent.put("DateTime", dateTime);
        activityEvent.put("EventName", eventName);
        activityEvent.put("Description", description);

        db.collection("Users")
                .document(UserID)
                .collection("Calendar")
                .add(activityEvent)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d("AI added event", "AI events added successfully");
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("AI events added failure", "Failure to add AI Events");
                    }
                });
    }

    private void extractAIRoutine(String response) {
        try {
            String[] futureActivities = response.split("\\n");
            String dateTime = "";
            String eventName = "";
            String description = "";
            String activityType = "";

            DateTimeFormatter inputFormat = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            DateTimeFormatter outputFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd");

            for (String activity : futureActivities) {
                activity = activity.trim();

                if (activity.startsWith("Date:")) {
                    String rawDate = activity.replace("Date:", "").trim();
                    try {
                        dateTime = LocalDate.parse(rawDate, inputFormat).format(outputFormat);
                        System.out.println(dateTime);
                    } catch (Exception dateParseException) {
                        Log.e("Date Parse Error", "Invalid date format: " + rawDate);
                    }
                } else if (activity.startsWith("Activity Type:")) {
                    activityType = activity.replace("Activity Type:", "").trim();
                    System.out.println("Activity Type " + activityType);
                } else if (activity.startsWith("Activity Title:")) {
                    eventName = activity.replace("Activity Title:", "").trim();
                    System.out.println("EventName: " + eventName);
                } else if (activity.startsWith("Details:")) {
                    description = activity.replace("Details:", "").trim();
                    System.out.println("Details " + description);
                }

                if (!dateTime.isEmpty() && !activityType.isEmpty() && !eventName.isEmpty() && !description.isEmpty()) {
                    addToCalendar(activityType, dateTime, eventName, description);
                    Log.d("DB_SAVED", "Saved Event: " + dateTime + ", " + activityType + ", " + eventName);

                    dateTime = "";
                    activityType = "";
                    eventName = "";
                    description = "";
                }
            }

            if (!dateTime.isEmpty() && !activityType.isEmpty() && !eventName.isEmpty() && !description.isEmpty()) {
                addToCalendar(activityType, dateTime, eventName, description);
                Log.d("AI Events Saved", "Saved AI event for " + dateTime);
            } else {
                System.out.println("Events not saved");
            }

        } catch (Exception e) {
            Log.e("Failure to extract AI generated events", "Failed to extract AI generated events: " + e.getMessage());
        }
    }
}
