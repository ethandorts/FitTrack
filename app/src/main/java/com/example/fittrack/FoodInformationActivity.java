package com.example.fittrack;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.text.InputFilter;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.firestore.FirebaseFirestore;

public class FoodInformationActivity extends AppCompatActivity {
    private TextView txtNutritionalFacts, txtFoodName, txtCalories, editServingQuantity;
    private EditText editServingSize;
    private Spinner editMeal;
    private Button btnLogFood;
    private ImageButton btnIncrease, btnDecrease;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FoodDatabaseUtil foodUtil = new FoodDatabaseUtil(db);
    private int servings = 1;
    private int maxServings = 20;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_information);

        txtFoodName = findViewById(R.id.txtIndividualFood);
        txtCalories = findViewById(R.id.txtTotalCalories);
        txtNutritionalFacts = findViewById(R.id.txtNutritionalFacts);
        editServingSize = findViewById(R.id.editServingSize);
        editServingQuantity = findViewById(R.id.editNoServings);
        editMeal = findViewById(R.id.editMealType);
        btnLogFood = findViewById(R.id.btnLogFood);
        btnIncrease = findViewById(R.id.btnIncreaseServing);
        btnDecrease = findViewById(R.id.btnDecreaseServing);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter
                .createFromResource(this, R.array.meal_types, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(androidx.appcompat.R.layout.support_simple_spinner_dropdown_item);
        editMeal.setAdapter(adapter);

        editServingQuantity.setText(String.valueOf(servings));

        Intent intent = getIntent();
        String foodName = intent.getStringExtra("FoodName");
        double calories = intent.getDoubleExtra("Calories", 0.0);
        String mealType = intent.getStringExtra("MealType");
        String selectedDate = intent.getStringExtra("selectedDate");
        double fat = intent.getDoubleExtra("Fat", 0.0);
        double saturated_fat = intent.getDoubleExtra("Saturated Fat", 0.0);
        double protein = intent.getDoubleExtra("Protein", 0.0);
        double sodium = intent.getDoubleExtra("Sodium", 0.0);
        double potassium = intent.getDoubleExtra("Potassium", 0.0);
        double carbs = intent.getDoubleExtra("Carbs", 0.0);
        double fiber = intent.getDoubleExtra("Fiber", 0.0);
        double sugar = intent.getDoubleExtra("Sugar", 0.0);

        System.out.println("Received Sugar: " + sugar);

        txtFoodName.setText(foodName);
        txtCalories.setText(calories + " calories");

        txtNutritionalFacts.setText(Html.fromHtml(
                "<b>Fat:</b> " + fat + " g<br>" +
                        "<b>Saturated Fat:</b> " + saturated_fat + " g<br>" +
                        "<b>Protein:</b> " + protein + " g<br>" +
                        "<b>Sodium:</b> " + sodium + " mg<br>" +
                        "<b>Potassium:</b> " + potassium + " mg<br>" +
                        "<b>Carbohydrates:</b> " + carbs + " g<br>" +
                        "<b>Fiber:</b> " + fiber + " g<br>" +
                        "<b>Sugar:</b> " + sugar + " g"
        ));

        btnIncrease.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (servings < maxServings) {
                    servings++;
                    editServingQuantity.setText(String.valueOf(servings));
                } else {
                    Toast.makeText(FoodInformationActivity.this, "Maximum of 20 servings allowed", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnDecrease.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (servings > 1) {
                    servings--;
                    editServingQuantity.setText(String.valueOf(servings));
                }
            }
        });

        btnLogFood.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validateCalorieInputs()) {
                    double servingSize = Double.parseDouble(editServingSize.getText().toString());
                    double servingQuantity = Double.parseDouble(editServingQuantity.getText().toString());
                    double totalGrams = servingSize * servingQuantity;
                    double multiplier = totalGrams / 100.0;
                    System.out.println("Multiplier value: " + multiplier);

                    FoodModel food = new FoodModel(
                            foodName,
                            calories * multiplier,
                            editMeal.getSelectedItem().toString(),
                            totalGrams,
                            servingQuantity,
                            fat,
                            saturated_fat,
                            protein,
                            sodium,
                            potassium,
                            carbs,
                            fiber,
                            sugar,
                            false
                    );

                    foodUtil.saveFood(food, selectedDate);
                    editServingSize.setText("");
                    editServingQuantity.setText("");

                    Intent intent = new Intent(FoodInformationActivity.this, NutritionTracking.class);
                    startActivity(intent);
                    finish();
                }
            }
        });
    }

    private boolean validateCalorieInputs() {
        String servingSizeStr = editServingSize.getText().toString().trim();
        String servingQuantityStr = editServingQuantity.getText().toString().trim();

        if (servingSizeStr.isEmpty()) {
            editServingSize.setError("Serving size is required!");
            return false;
        }
        if (servingQuantityStr.isEmpty()) {
            editServingQuantity.setError("Serving quantity is required!");
            return false;
        }

        try {
            double servingSize = Double.parseDouble(servingSizeStr);
            double servingQuantity = Double.parseDouble(servingQuantityStr);

            if (servingSize <= 0) {
                editServingSize.setError("Serving size must be greater than 0!");
                return false;
            }
            if (servingQuantity <= 0) {
                editServingQuantity.setError("Serving quantity must be greater than 0!");
                return false;
            }
        } catch (NumberFormatException e) {
            editServingSize.setError("Invalid number format!");
            editServingQuantity.setError("Invalid number format!");
            return false;
        }
        return true;
    }
}