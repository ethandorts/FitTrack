package com.example.fittrack;

import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

public class AINutritionInsightsActivity extends AppCompatActivity {
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FoodDatabaseUtil foodUtil = new FoodDatabaseUtil(db);
    private String selectedDate;
    private TextView txtInsights;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ainutrition_insights);

        txtInsights = findViewById(R.id.txtAIInsights);

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
}