package com.example.fittrack;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AdditionalNutrtionalInformationActivity extends AppCompatActivity {
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private String UserID = mAuth.getUid();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FoodDatabaseUtil foodUtil = new FoodDatabaseUtil(db);
    private FirebaseDatabaseHelper userUtil = new FirebaseDatabaseHelper(db);
    private TextView txtAdvice;
    private String lastMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_additional_nutrtional_information);

        Intent intent = getIntent();
        String selectedDate = intent.getStringExtra("selectedDate");

        RecyclerView recyclerView = findViewById(R.id.recyclerView2);
        txtAdvice = findViewById(R.id.txtAINutrientAdvice);
        ArrayList<NutrientModel> nutrientList = new ArrayList<>();
        foodUtil.getTodayNutrient(selectedDate, new FoodDatabaseUtil.NutrientCallback() {
            @Override
            public void onCallback(double fatAmount, double saturatedFatAmount, double proteinAmount, double sodiumAmount, double potassiumAmount, double carbsAmount, double fiberAmount, double sugarAmount) {
                nutrientList.add(new NutrientModel(" Fat", fatAmount));
                nutrientList.add(new NutrientModel(" Saturated Fat", saturatedFatAmount));
                nutrientList.add(new NutrientModel(" Protein", proteinAmount));
                nutrientList.add(new NutrientModel(" Potassium", potassiumAmount));
                nutrientList.add(new NutrientModel(" Carbohydrates", carbsAmount));
                nutrientList.add(new NutrientModel(" Fiber", fiberAmount));
                nutrientList.add(new NutrientModel(" Sugar", sugarAmount));

                NutrientRecyclerAdapter adapter = new NutrientRecyclerAdapter(getApplicationContext(), nutrientList);
                recyclerView.setAdapter(adapter);
                recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
            }
        });

        userUtil.retrieveUserName(UserID, new FirebaseDatabaseHelper.FirestoreUserNameCallback() {
            @Override
            public void onCallback(String FullName, long weight, long height, long activityFrequency, long dailyCalorieGoal) {
                foodUtil.getTotalCalories(selectedDate, new FoodDatabaseUtil.DayCaloriesCallback() {
                    @Override
                    public void onCallback(double caloriesConsumed) {
                        foodUtil.retrieveCaloriesBurnedToday(selectedDate, new FoodDatabaseUtil.DayCaloriesBurnedCallback() {
                            @Override
                            public void onCallback(long caloriesBurned) {
                                GetNutritionalAdvice("", 0, 0, 0, new ArrayList<>());
                            }
                        });
                    }
                });
            }
        });
    }

    public void GetNutritionalAdvice(String question, int dailyCaloricGoal, int dailyConsumption, int caloriesBurned, ArrayList<String> foods) {
        JSONObject body = new JSONObject();
        JSONArray messagesArray = new JSONArray();

        try {
            JSONObject aiDescription = new JSONObject();
            aiDescription.put("role", "system");
            aiDescription.put("content", "You are an AI Fitness Nutrition Coaching Assistant. My daily caloric goal is " + dailyCaloricGoal +
                    " and currently I have eaten " + dailyConsumption + ". I have burned " + caloriesBurned + ". Here is what I have already eaten today: " + "\n" + foods + ". " +
                    "Can you advise me on what I should eat to meet my calorie goals.");
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
                            JSONArray choicesArray = jsonObject.getJSONArray("choices");
                            JSONObject choiceObject = choicesArray.getJSONObject(0);
                            JSONObject messageObject = choiceObject.getJSONObject("message");

                            String message = messageObject.getString("content");
                            txtAdvice.setText(message);
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



}