package com.example.fittrack;

import android.content.Context;
import android.content.Intent;
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

public class NutritionSearchRecyclerViewAdapter extends RecyclerView.Adapter<NutritionSearchRecyclerViewAdapter.NutritionSearchViewHolder> {
    private Context context;
    private ArrayList<SearchFoodModel> searchFoodList = new ArrayList<>();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FoodDatabaseUtil foodUtil = new FoodDatabaseUtil(db);

    public NutritionSearchRecyclerViewAdapter(Context context, ArrayList<SearchFoodModel> searchFoodList) {
        this.context = context;
        this.searchFoodList = searchFoodList;
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
        holder.calorieCount.setText(String.valueOf(model.getCalories() + " calories"));
        holder.furtherDetails.setText(String.valueOf(model.getCarbs()));
        holder.btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FoodModel food = new FoodModel(
                        ConversionUtil.capitaliseFoodName(model.getFoodName()),
                        model.getCalories(),
                        model.getMealType(),
                        1,
                        model.getFat(),
                        model.getSaturated_fat(),
                        model.getProtein(),
                        model.getSodium(),
                        model.getPotassium(),
                        model.getCarbs(),
                        model.getFiber(),
                        model.getSugar()
                );
                System.out.println(model.getMealType());
                foodUtil.saveFood(food);
                Toast.makeText(view.getContext(), "Logged food successfully", Toast.LENGTH_SHORT).show();
            }
        });
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, FoodInformationActivity.class);
                intent.putExtra("FoodName", String.valueOf(model.getFoodName()));
                intent.putExtra("Calories", String.valueOf(model.getCalories()));
                intent.putExtra("MealType", String.valueOf(model.getMealType()));
                intent.putExtra("Fat", String.valueOf(model.getFat()));
                intent.putExtra("Saturated Fat", String.valueOf(model.getSaturated_fat()));
                intent.putExtra("Protein", String.valueOf(model.getProtein()));
                intent.putExtra("Sodium", String.valueOf(model.getSodium()));
                intent.putExtra("Potassium", String.valueOf(model.getPotassium()));
                intent.putExtra("Carbs", String.valueOf(model.getCarbs()));
                intent.putExtra("Fiber", String.valueOf(model.getFiber()));
                intent.putExtra("Sugar", String.valueOf(model.getSugar()));
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