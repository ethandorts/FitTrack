package com.example.fittrack;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

public class AINutritionInsightsFragment extends Fragment {
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FoodDatabaseUtil foodUtil = new FoodDatabaseUtil(db);
    private String selectedDate;
    private TextView txtInsights;

    public AINutritionInsightsFragment(String selectedDate) {
        this.selectedDate = selectedDate;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.ai_nutrition_insights_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        txtInsights = view.findViewById(R.id.txtAIStats);

        foodUtil.getTotalCalories(selectedDate, new FoodDatabaseUtil.DayCaloriesCallback() {
            @Override
            public void onCallback(double calories) {
                foodUtil.getCaloriesForMealType(selectedDate, new FoodDatabaseUtil.CaloriesMealCallback() {
                    @Override
                    public void onCallback(HashMap<String, Double> caloriesMap) {
                        foodUtil.retrieveCaloriesBurnedToday(selectedDate, new FoodDatabaseUtil.DayCaloriesBurnedCallback() {
                            @Override
                            public void onCallback(long calories) {
                                txtInsights.setText(calories + " \n" + caloriesMap.get("Breakfast") + " \n" + caloriesMap.get("Lunch")
                                + " \n" + caloriesMap.get("Dinner") + " \n" + caloriesMap.get("Snacks"));
                            }
                        });
                    }
                });
            }
        });
    }



//    public void GetAINutritionInsights(String question) {
//        JSONObject body = new JSONObject();
//        JSONArray messagesArray = new JSONArray();
//
//        try {
//            JSONObject aiDescription = new JSONObject();
//            aiDescription.put("role", "system");
//            aiDescription.put("content", "You are an AI Fitness Coaching Assistant that specialises in creating personalised fitness training workout schedules.");
//            messagesArray.put(aiDescription);
//            JSONObject userMessage = new JSONObject();
//            userMessage.put("role", "user");
//            userMessage.put("content", question);
//            messagesArray.put(userMessage);
//
//            body.put("model", "gpt-3.5-turbo");
//            body.put("messages", messagesArray);
//
//        } catch (JSONException e) {
//            System.out.println("Error creating request body: " + e);
//        }
//
//        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
//        JsonObjectRequest request = new JsonObjectRequest(
//                Request.Method.POST,
//                "https://api.openai.com/v1/chat/completions",
//                body,
//                new Response.Listener<JSONObject>() {
//                    @Override
//                    public void onResponse(JSONObject jsonObject) {
//                        try {
//                            JSONArray choicesArray = jsonObject.getJSONArray("choices");
//                            JSONObject choiceObject = choicesArray.getJSONObject(0);
//                            JSONObject messageObject = choiceObject.getJSONObject("message");
//
//                            String message = messageObject.getString("content");
//                            txtResponse.setText(message);
//                            System.out.println(message);
//                            lastMessage = message;
//
//                            editMessage.setVisibility(View.VISIBLE);
//                            btnSendRequest.setVisibility(View.VISIBLE);
//                            //btnSaveTrainingSchedule.setVisibility(View.VISIBLE);
//
//                            if(txtResponse.getText().length() > 0) {
//                                btnSaveTrainingSchedule.setVisibility(View.VISIBLE);
//                                btnSaveTrainingSchedule.setOnClickListener(new View.OnClickListener() {
//                                    @Override
//                                    public void onClick(View view) {
//                                        extractAIRoutine(message);
//                                    }
//                                });
//                            }
//
//
//
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                },
//                new Response.ErrorListener() {
//                    @Override
//                    public void onErrorResponse(VolleyError error) {
//                        System.out.println("AI failure: " + error + " " + error.getMessage());
//                    }
//                }
//        ) {
//            @Override
//            public Map<String, String> getHeaders() {
//                Map<String, String> headersMap = new HashMap<>();
//                headersMap.put("Content-Type", "application/json");
//                headersMap.put("Authorization", "Bearer ");
//                return headersMap;
//            }
//
//            @Override
//            public RetryPolicy getRetryPolicy() {
//                return new DefaultRetryPolicy(60000, 3, 1);
//            }
//        };
//        requestQueue.add(request);
//    }
}
