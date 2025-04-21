package com.example.fittrack;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
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
    private ProgressBar aiNutritionProgress;
    private String lastMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_additional_nutrtional_information);

        Intent intent = getIntent();
        String selectedDate = intent.getStringExtra("selectedDate");

        RecyclerView recyclerView = findViewById(R.id.recyclerView2);
        txtAdvice = findViewById(R.id.txtAINutrientAdvice);
        aiNutritionProgress = findViewById(R.id.progressBarNP);
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
            public void onCallback(String FullName, long weight, long height, long activityFrequency, long dailyCalorieGoal, String level, String fitnessGoal) {
                foodUtil.getTotalCalories(selectedDate, new FoodDatabaseUtil.DayCaloriesCallback() {
                    @Override
                    public void onCallback(double caloriesConsumed) {
                        foodUtil.retrieveCaloriesBurnedToday(selectedDate, new FoodDatabaseUtil.DayCaloriesBurnedCallback() {
                            @Override
                            public void onCallback(long caloriesBurned) {
                                foodUtil.retrieveTodaysMealData(selectedDate, new FoodDatabaseUtil.TodayFoodsCallback() {
                                    @Override
                                    public void onCallback(ArrayList<FoodModel> todaysFood) {
                                        GetNutritionalAdvice((int) dailyCalorieGoal, (int) caloriesConsumed, (int) caloriesBurned, todaysFood);
                                    }
                                });
                            }
                        });
                    }
                });
            }
        });
    }

    public void GetNutritionalAdvice(int dailyCaloricGoal, int dailyConsumption, int caloriesBurned, ArrayList<FoodModel> foods) {
        aiNutritionProgress.setVisibility(View.VISIBLE);
        txtAdvice.setText("");
        JSONObject body = new JSONObject();
        JSONArray messagesArray = new JSONArray();

        String question = "You are an AI Fitness Nutrition Coaching Assistant." +
                " My daily caloric goal is " + dailyCaloricGoal + " kcal. I have consumed " + dailyConsumption + " kcal so far and burned " + caloriesBurned + " kcal through exercise." +
                " The foods I have eaten today are listed below:\n\n" +
                displayFoods(foods) + "\n\n" +
                "Based on this, please:\n" +
                "1. Calculate and state how many calories I have remaining to reach my goal.\n" +
                "2. Identify which meals I have already logged today (breakfast, lunch, dinner, snacks).\n" +
                "3. Suggest meals only for the remaining ones in the day. For example, if I've already logged breakfast and lunch, only suggest dinner and snacks.\n" +
                "4. For each remaining meal, suggest 2-3 food items with their calorie count and explain briefly what nutrients they provide or how they help balance my intake.\n\n" +
                "Format your response like this:\n" +
                "---------------------------\n" +
                "Calorie Summary:\n" +
                "- Goal: X kcal\n" +
                "- Consumed: Y kcal\n" +
                "- Burned: Z kcal\n" +
                "- Remaining: N kcal\n\n" +
                "Meal Plan Suggestions:\n\n" +
                "Lunch:\n" +
                "- Grilled chicken breast – 300 kcal: High in protein to support muscle maintenance.\n" +
                "- Quinoa – 220 kcal: Provides complex carbs and fiber.\n\n" +
                "Dinner:\n" +
                "- Steamed salmon – 350 kcal: Rich in omega-3s and protein.\n" +
                "- Brown rice – 200 kcal: Slow-digesting carbs for sustained energy.\n" +
                "- Steamed broccoli – 50 kcal: High in fiber and antioxidants.\n" +
                "---------------------------\n" +
                "Only suggest realistic, common foods that align with a healthy fitness-based diet.";


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
                            aiNutritionProgress.setVisibility(View.GONE);
                            txtAdvice.setText("");
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

    private String displayFoods(ArrayList<FoodModel> foods) {
        if (foods == null || foods.isEmpty()) return "No foods logged yet today.";

        StringBuilder formatted = new StringBuilder();
        for (FoodModel food : foods) {
            formatted.append("- ").append(food.getMealType()).append(": ")
                    .append(food.getFoodName()).append(" (")
                    .append(food.getCalories()).append(" calories)\n");
        }
        return formatted.toString();
    }
}