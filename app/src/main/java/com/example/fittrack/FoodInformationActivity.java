package com.example.fittrack;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class FoodInformationActivity extends AppCompatActivity {
    private TextView txtNutritionalFacts, txtFoodName, txtCalories;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_information);

        txtFoodName = findViewById(R.id.txtIndividualFood);
        txtCalories = findViewById(R.id.txtTotalCalories);
        txtNutritionalFacts = findViewById(R.id.txtNutritionalFacts);

        Intent intent = getIntent();
        String foodName = intent.getStringExtra("FoodName");
        String calories = intent.getStringExtra("Calories");
        String mealType = intent.getStringExtra("MealType");
        String fat = intent.getStringExtra("Fat");
        String saturated_fat = intent.getStringExtra("Saturated Fat");
        String protein = intent.getStringExtra("Protein");
        String sodium = intent.getStringExtra("Sodium");
        String potassium = intent.getStringExtra("Potassium");
        String carbs = intent.getStringExtra("Carbs");
        String fiber = intent.getStringExtra("Fiber");
        String sugar = intent.getStringExtra("Sugar");
        System.out.println("Received Sugar: " + sugar);

        txtFoodName.setText(foodName);
        txtCalories.setText(calories + " calories");

        txtNutritionalFacts.setText(" Fat : " + fat + "\n"
        + " Saturated Fat: " + saturated_fat + "\n"
        + " Protein : " + protein + "\n"
        + " Sodium: " + sodium + "\n"
        + " Potassium: " + potassium + "\n"
        + " Carbohydrates: " + carbs + "\n"
        + " Fiber: " + fiber + "\n"
        + " Sugar: " + sugar);

    }
}