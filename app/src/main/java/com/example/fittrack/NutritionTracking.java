package com.example.fittrack;

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
    private NutritionListAdapter foodAdapter;
    private TextView txtCalorieCount;
    private double dailyCalories = 0;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
    private Date today = new Date();
    private String date = dateFormat.format(today);

    @Override
    protected void onStart() {
        super.onStart();
        foodAdapter.startListening();
        System.out.println("FoodAdapter Listening...");
    }

    @Override
    protected void onStop() {
        super.onStop();
        foodAdapter.stopListening();
        System.out.println("FoodAdapter Listening...");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nutrition_tracking);

        EditText editFood = findViewById(R.id.editFood);
        Button btnAddFood = findViewById(R.id.btnAddFood);
        txtCalorieCount = findViewById(R.id.txtCalorieCount);
        RecyclerView recyclerNutrition = findViewById(R.id.recyclerViewnutrition);

        Query query = db.collection("Users")
                .document(UserID)
                .collection("Nutrition")
                .document(date)
                .collection("Meals");

        FirestoreRecyclerOptions<FoodModel> options =
                new FirestoreRecyclerOptions.Builder<FoodModel>()
                        .setQuery(query, FoodModel.class)
                        .build();

        foodAdapter = new NutritionListAdapter(options, this);
        recyclerNutrition.setAdapter(foodAdapter);
        recyclerNutrition.setLayoutManager(new LinearLayoutManager(this));
        getCalorieCount();

        btnAddFood.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String food = editFood.getText().toString();
                foodRequest(food, new FoodResponse() {
                    @Override
                    public void onFoodResponse(String name, double calories, int quantity, String mealType) {
                        FoodModel food = new FoodModel(name, calories, mealType, quantity);
                        foodDatabaseUtil.saveFood(food);
                        new android.os.Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                getCalorieCount();
                            }
                        }, 2000);
                    }
                });
            }
        });
    }

    private void foodRequest(String food, FoodResponse response) {
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
                            JSONObject foodObject = itemsArray.getJSONObject(0);
                            String name = foodObject.getString("name");
                            double calories = foodObject.getDouble("calories");
                            int quantity = 1;
                            String mealType = "Dinner";
                            response.onFoodResponse(name, calories, quantity, mealType);
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
                //headersMap.put("X-Api-Key", ); // insert API KEY HERE

                return headersMap;
            }
        };
        requestQueue.add(request);
    }
    public interface FoodResponse {
        void onFoodResponse(String name, double calories, int quantity, String mealType);
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
}