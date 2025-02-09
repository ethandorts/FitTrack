package com.example.fittrack;

import android.content.Context;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AIGeneratorUtil {
    Context context;

    public AIGeneratorUtil(Context context) {
        this.context = context;
    }

    public void generateTrainingPlan(long weight, long height, List<String> goals) {
        AskFitTrackCoachingAssistant("Create a training schedule for the next week. " +
                "Here is my information, I weigh (retrieve information) kg and I am (retrieve information) cm tall. " +
                "My goal is (retrieve goals) to run a 120km this month. Other Considerations is " +
                "(user types in a specific condition e.g. sore leg, sore ankle).  \n" +
                "\n" +
                "Display the training schedule in this format, for example:\n" +
                "\n" +
                "Date: 24/10/2024\n" +
                "Activity Type: Running\n" +
                "Activity Title: 5KM Run\n" +
                "Details: Tempo Run\n");
    }

    public void generateDailyNutritionPlan() {
        AskFitTrackCoachingAssistant("Create me a nutrition plan for tomorrow. I have a goal to eat 2,500 calories a day. I weigh 64kg and I am 179cm tall. Provide me with a nutritional plan.\n" +
                "\n" +
                "Provide the Nutritional Plan in this format, for example:\n" +
                "\n" +
                "Breakfast \n" +
                "Food: Rice Crispies, Toast \n" +
                "\n" +
                "Lunch\n" +
                "Food: Ham Sandwich \n" +
                "\n" +
                "Dinner\n" +
                "Food: Fish, Chips\n");
    }

    public void generateNutritionalAdvice() {
        AskFitTrackCoachingAssistant("My calorie goal for today is 3000 calories. My current number of calories consumed is 2300. " +
                "The current time is 11:00am. Can you specifically make the food consumed add up to the difference between calorie goal and the calories consumed." +
                "Can you suggest foods for me to eat so I can reach my calorie goal by the end of the day?" +
                "Here is an example of a response: " +
                "Suggested Food: (Suggested Food Name Here + total grams of food) " +
                "Number of Calories: (Number of calories of food suggested) " +
                "(Provide a reason for selection)");
    }

    private void AskFitTrackCoachingAssistant(String question) {
        JSONObject body = new JSONObject();
        JSONArray messagesArray = new JSONArray();
        try {
            body.put("model", "gpt-3.5-turbo");
            JSONObject messages = new JSONObject();
            messages.put("role", "user");
            messages.put("content", question);
            messagesArray.put(messages);
            body.put("messages", messagesArray);
        } catch(JSONException e) {
            System.out.println("Error creating request body: " + e);
        }

        RequestQueue requestQueue = Volley.newRequestQueue(context);
        //String api_key;
        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST,
                "https://api.openai.com/v1/chat/completions",
                body,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        try {
                            System.out.println(jsonObject);
                            JSONArray array = jsonObject.getJSONArray("choices");
                            JSONObject object = (JSONObject) array.get(0);
                            JSONObject nextObject = (JSONObject) object.get("message");
                            String message = nextObject.getString("content");

                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        System.out.println("AI failure: " + volleyError + " " + volleyError.getMessage() + " ");
                    }
                }
        ) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headersMap = new HashMap<>();
                headersMap.put("Content-Type", "application/json");

                return headersMap;
            }

            @Override
            public RetryPolicy getRetryPolicy() {
                return new DefaultRetryPolicy(60000, 3, 1);
            }
        };
        requestQueue.add(request);
    }
}
