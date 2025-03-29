package com.example.fittrack;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NutritionTracking extends AppCompatActivity {
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FoodDatabaseUtil foodDatabaseUtil = new FoodDatabaseUtil(db);
    private FirebaseUser mAuth = FirebaseAuth.getInstance().getCurrentUser();
    private String UserID = mAuth.getUid();
    private NutritionSearchRecyclerViewAdapter foodAdapter;
    private TextView txtCalorieCount;
    private double dailyCalories = 0;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
    private Date today = new Date();
    private String date = dateFormat.format(today);
    private ArrayList<SearchFoodModel> foodlist = new ArrayList<>();

    @Override
    protected void onStart() {
        super.onStart();
        System.out.println("FoodAdapter Listening...");
    }

    @Override
    protected void onStop() {
        super.onStop();
        System.out.println("FoodAdapter Listening...");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nutrition_tracking);
        Intent intent = getIntent();
        String mealTime = intent.getStringExtra("mealType");

        EditText editFood = findViewById(R.id.editFood);
        Button btnSearchFood = findViewById(R.id.btnSearchFood);
        //txtCalorieCount = findViewById(R.id.txtCalorieCount);
        Button btnReturn = findViewById(R.id.btnNutritionOverview);
        RecyclerView recyclerNutrition = findViewById(R.id.recyclerViewnutrition);

        foodAdapter = new NutritionSearchRecyclerViewAdapter(getApplicationContext(), foodlist);
        recyclerNutrition.setAdapter(foodAdapter);
        recyclerNutrition.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        loadGeneralFoods(mealTime);

//        Query query = db.collection("Users")
//                .document(UserID)
//                .collection("Nutrition")
//                .document(date)
//                .collection("Meals");
//
//        FirestoreRecyclerOptions<FoodModel> options =
//                new FirestoreRecyclerOptions.Builder<FoodModel>()
//                        .setQuery(query, FoodModel.class)
//                        .build();

//        foodAdapter = new NutritionListAdapter(options, this);
//        recyclerNutrition.setAdapter(foodAdapter);
//        recyclerNutrition.setLayoutManager(new LinearLayoutManager(this));
//        getCalorieCount();



        btnSearchFood.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                foodlist.clear();
                String food = editFood.getText().toString();
                String mealType = intent.getStringExtra("mealType");
                foodRequest(food, mealType, new FoodResponse() {
                    @Override
                    public void onFoodResponse(String name, double calories, double fat, double saturated_fat, double protein, double sodium, double potassium, double carbs, double fiber, double sugar, int quantity, String mealTime) {
                        SearchFoodModel model = new SearchFoodModel(ConversionUtil.capitaliseFoodName(name), calories, fat, saturated_fat, protein, sodium, potassium, carbs, fiber, sugar, quantity, mealType);
                        System.out.println(model.getCalories());
                        foodlist.add(model);
                        foodAdapter.notifyDataSetChanged();
                        System.out.println("Count: " + foodAdapter.getItemCount());
//                        FoodModel food = new FoodModel(name, calories, mealType, quantity);
//                        foodDatabaseUtil.saveFood(food);
//                        new android.os.Handler().postDelayed(new Runnable() {
//                            @Override
//                            public void run() {
//                                getCalorieCount();
//                            }
//                        }, 2000);
                    }
                });
            }
        });

        btnReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(NutritionTracking.this, NutritionTrackingOverview.class);
                startActivity(intent);
            }
        });
    }

    private void foodRequest(String food, String mealType, FoodResponse response) {
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET,
                "https://api.calorieninjas.com/v1/nutrition?query=" + food,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        try {
                            JSONArray itemsArray = jsonObject.getJSONArray("items");
                            if(itemsArray.length() != 0) {
                                JSONObject foodObject = itemsArray.getJSONObject(0);
                                String name = foodObject.getString("name");
                                double calories = foodObject.getDouble("calories");
                                double fat = foodObject.getDouble("fat_total_g");
                                double saturated_fat = foodObject.getDouble("fat_saturated_g");
                                double protein = foodObject.getDouble("protein_g");
                                double sodium = foodObject.getDouble("sodium_mg");
                                double potassium = foodObject.getDouble("potassium_mg");
                                double carbs = foodObject.getDouble("carbohydrates_total_g");
                                double fiber = foodObject.getDouble("fiber_g");
                                double sugar = foodObject.getDouble("sugar_g");
                                int quantity = 1;
                                String mealType = "Dinner";
                                response.onFoodResponse(name, calories, fat, saturated_fat, protein, sodium, potassium, carbs, fiber, sugar, quantity, mealType);
                            } else {
                                System.out.println("No food found for this search!");
                            }
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        System.out.println("Error in getting food data from API" + volleyError);
                    }
                }
        ) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headersMap = new HashMap<>();
                headersMap.put("X-Api-Key", "3T9QzDFbiwikUXS4RMQsKg==DeZjUwaU7l1WgRkm"); // insert API KEY HERE

                return headersMap;
            }
        };
        requestQueue.add(request);
    }
    public interface FoodResponse {
        void onFoodResponse(String name, double calories,  double fat, double saturated_fat, double protein, double sodium, double potassium, double carbs, double fiber, double sugar, int quantity, String mealType);
    }

    private void getCalorieCount() {
        db.collection("Users")
                .document(UserID)
                .collection("Nutrition")
                .document(date)
                .collection("Meals").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot querySnapshot) {
                dailyCalories = 0;
                List<DocumentSnapshot> snapshots = querySnapshot.getDocuments();
                for(DocumentSnapshot snapshot : snapshots) {
                    double calories = (double) snapshot.get("calories");
                    dailyCalories += calories;
                    txtCalorieCount.setText("Daily Calorie Count: " + dailyCalories);
                }
            }
        });
    }

    private void loadGeneralFoods(String mealType) {
        foodlist.add(new SearchFoodModel("Chicken", 166.2, 3.5, 1, 31, 72, 226, 0, 0, 0, 1, mealType));
        foodlist.add(new SearchFoodModel("Cereal", 386.0, 6.8, 1.5, 12.2, 495, 489, 71.9, 9.2, 4.3, 1, mealType));
        foodlist.add(new SearchFoodModel("Chips", 540.8, 34.4, 3.4, 6.4, 533, 150, 54.3, 3.1, 0.3, 1, mealType));
        foodlist.add(new SearchFoodModel("Potatoes", 92.9, 0.1, 0.0, 2.5, 10, 70, 21, 2.2, 1.2, 1, mealType));
        foodlist.add(new SearchFoodModel("Apple", 53.0, 0.2, 0.0, 0.3, 1, 11, 14.1, 2.4, 10.3, 1, mealType));
        foodlist.add(new SearchFoodModel("Toast", 298.3, 4.0, 0.8, 9.1, 541, 102, 54.2, 2.9, 6.2, 1, mealType));
        foodlist.add(new SearchFoodModel("Tomatoes", 34.3, 0.2, 0, 0.7, 187, 15, 7.6, 0.5, 4, 1, mealType));
        foodlist.add(new SearchFoodModel("Ham", 167.7, 5.9, 1.5, 9.4, 631, 98, 20.4, 1.9, 2.5,1, mealType));
    }
}