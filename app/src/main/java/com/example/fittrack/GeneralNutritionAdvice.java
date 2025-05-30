package com.example.fittrack;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GeneralNutritionAdvice extends Fragment {
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private String UserID = mAuth.getUid();
    private TextView txtResponse;
    private ImageButton btnSendRequest;
    private EditText editMessage;
    private String lastMessage;
    private GoalsUtil goalsUtil = new GoalsUtil(db);
    private String goalsList = " ";
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        txtResponse = view.findViewById(R.id.txtAIResponseNA);
        btnSendRequest = view.findViewById(R.id.btnSendRequestNA);
        editMessage = view.findViewById(R.id.editModifyNA);

        editMessage.setClickable(false);

        goalsUtil.retrieveUserGoals(UserID, new GoalsUtil.GoalsCallback() {
            @Override
            public void onCallback(List<String> goals) {
                for(String goal : goals) {
                    goalsList = goalsList + goal + ". ";
                }

                btnSendRequest.setClickable(true);

                btnSendRequest.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String question = editMessage.getText().toString();
                        AskFitTrackCoachingAssistant("Here is my information, I weigh" + getUserWeight() + "kg" +
                                " and I am " + getUserHeight() + "cm tall. " + "I have a daily calorie goal of " + getDailyCalorieGoal() + ". " +
                                "Use this background information that may you help you make an informed decision on how to best answer this question: " +
                                question);
                        editMessage.setText("");
                    }
                });
            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_general_nutrition_advice, container, false);
    }

    public void AskFitTrackCoachingAssistant(String question) {
        JSONObject body = new JSONObject();
        JSONArray messagesArray = new JSONArray();

        try {
            JSONObject aiDescription = new JSONObject();
            aiDescription.put("role", "system");
            aiDescription.put("content", "You are an AI Fitness Coaching Assistant that specialises in giving personalised nutrition advice.");
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

    private long getDailyCalorieGoal() {
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("UserPI", Context.MODE_PRIVATE);
        return sharedPreferences.getLong("DailyCalorieGoal", 0);
    }
}
