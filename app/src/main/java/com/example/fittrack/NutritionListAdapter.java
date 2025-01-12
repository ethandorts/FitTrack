package com.example.fittrack;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

import java.util.ArrayList;

public class NutritionListAdapter extends FirestoreRecyclerAdapter<FoodModel, NutritionListAdapter.NutritionItemHolder> {
    private RecyclerViewInterface recyclerViewInterface;

    private Context context;
    private ArrayList<FoodModel> foodList = new ArrayList<>();

    public NutritionListAdapter(@NonNull FirestoreRecyclerOptions<FoodModel> options, Context context) {
        super(options);
        this.context = context;
    }

    @NonNull
    @Override
    public NutritionItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.food_item_row, parent, false);
        return new NutritionListAdapter.NutritionItemHolder(view, recyclerViewInterface);
    }

    @Override
    public void onBindViewHolder(@NonNull NutritionItemHolder holder, int position, @NonNull FoodModel food) {
        holder.foodName.setText(food.getFoodName());
        holder.calories.setText(String.valueOf(food.getCalories() + " Calories"));
    }

    @Override
    public int getItemCount() {
        return super.getItemCount();
    }

    public static class NutritionItemHolder extends RecyclerView.ViewHolder {
        TextView foodName, calories;
        public NutritionItemHolder(@NonNull View itemView, RecyclerViewInterface recyclerViewInterface) {
            super(itemView);
            foodName = itemView.findViewById(R.id.txtLeaderboardStatName);
            calories = itemView.findViewById(R.id.txtDistanceStat);

        }
    }
}
