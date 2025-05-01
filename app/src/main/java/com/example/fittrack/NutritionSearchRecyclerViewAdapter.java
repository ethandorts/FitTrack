package com.example.fittrack;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Date;

public class NutritionSearchRecyclerViewAdapter extends RecyclerView.Adapter<NutritionSearchRecyclerViewAdapter.NutritionSearchViewHolder> {
    private Context context;
    private ArrayList<SearchFoodModel> searchFoodList = new ArrayList<>();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FoodDatabaseUtil foodUtil = new FoodDatabaseUtil(db);
    private String selectedDate;

    public NutritionSearchRecyclerViewAdapter(Context context, ArrayList<SearchFoodModel> searchFoodList, String date) {
        this.context = context;
        this.searchFoodList = searchFoodList;
        this.selectedDate = date;
    }

    @NonNull
    @Override
    public NutritionSearchViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.nutrition_search_row, parent, false);
        return new NutritionSearchRecyclerViewAdapter.NutritionSearchViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NutritionSearchViewHolder holder, int position) {
        SearchFoodModel model = searchFoodList.get(position);
        holder.foodName.setText(model.getFoodName());
        holder.calorieCount.setText(String.valueOf(model.getCalories() + " kcal"));
        holder.furtherDetails.setText(String.valueOf(model.getCarbs()));
        holder.btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FoodModel food = new FoodModel(
                        ConversionUtil.capitaliseFoodName(model.getFoodName()),
                        model.getCalories(),
                        model.getMealType(),
                        100,
                        1,
                        model.getFat(),
                        model.getSaturated_fat(),
                        model.getProtein(),
                        model.getSodium(),
                        model.getPotassium(),
                        model.getCarbs(),
                        model.getFiber(),
                        model.getSugar(),
                        false
                );
                System.out.println(model.getMealType());
                foodUtil.saveFood(food, selectedDate);
                Toast.makeText(view.getContext(), "Logged food successfully", Toast.LENGTH_SHORT).show();

                new Handler(Looper.getMainLooper()).postDelayed(() -> {
                    Intent intent = new Intent(view.getContext(), NutritionTrackingOverview.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    view.getContext().startActivity(intent);
                }, 2000);
            }
        });
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("Passing: " + model.getMealType());
                System.out.println("Passing: " + model.getCalories());
                Intent intent = new Intent(context, FoodInformationActivity.class);
                intent.putExtra("FoodName", model.getFoodName());
                intent.putExtra("Calories", model.getCalories());
                intent.putExtra("MealType", model.getMealType());
                intent.putExtra("Fat", model.getFat());
                intent.putExtra("Saturated Fat", model.getSaturated_fat());
                intent.putExtra("Protein", model.getProtein());
                intent.putExtra("Sodium", model.getSodium());
                intent.putExtra("Potassium", model.getPotassium());
                intent.putExtra("Carbs", model.getCarbs());
                intent.putExtra("Fiber", model.getFiber());
                intent.putExtra("Sugar", model.getSugar());
                intent.putExtra("selectedDate", selectedDate);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return searchFoodList.size();
    }

    public static class NutritionSearchViewHolder extends RecyclerView.ViewHolder {
        private TextView foodName, calorieCount, furtherDetails;
        private ImageButton btnAdd;

        public NutritionSearchViewHolder(@NonNull View itemView) {
            super(itemView);
            foodName = itemView.findViewById(R.id.txtNutritionSearchName);
            calorieCount = itemView.findViewById(R.id.txtSearchCalorieCount);
            furtherDetails = itemView.findViewById(R.id.txtFurtherDetails);
            btnAdd = itemView.findViewById(R.id.btnAddFoodSearch);
        }
    }
}
