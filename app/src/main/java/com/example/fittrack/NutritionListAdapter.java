package com.example.fittrack;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class NutritionListAdapter extends FirestoreRecyclerAdapter<FoodModel, NutritionListAdapter.NutritionItemHolder> {
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private RecyclerViewInterface recyclerViewInterface;
    private FoodDatabaseUtil FoodUtil = new FoodDatabaseUtil(db);
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
        String DocumentID = getSnapshots().getSnapshot(position).getId();

        System.out.println(food.getCalories());
        holder.foodName.setText(food.getFoodName());
        holder.calories.setText(String.format("%.2f", food.getCalories()) + " Calories");
        holder.moreDetails.setText(Html.fromHtml(
                "<b>Serving Size:</b> " + String.format("%.2f", food.getServingSize()) + " g<br>" +
                        "<b>Fat:</b> " + String.format("%.2f", food.getFat()) + " g<br>" +
                        "<b>Saturated Fat:</b> " + String.format("%.2f", food.getSaturated_fat()) + " g<br>" +
                        "<b>Protein:</b> " + String.format("%.2f", food.getProtein()) + " g<br>" +
                        "<b>Sodium:</b> " + String.format("%.2f", food.getSodium()) + " mg<br>" +
                        "<b>Potassium:</b> " + String.format("%.2f", food.getPotassium()) + " mg<br>" +
                        "<b>Carbohydrates:</b> " + String.format("%.2f", food.getCarbs()) + " g<br>" +
                        "<b>Fiber:</b> " + String.format("%.2f", food.getFiber()) + " g<br>" +
                        "<b>Sugar:</b> " + String.format("%.2f", food.getSugar()) + " g"
        ));

        if(food.isDetailsShown()) {
            holder.moreDetails.setVisibility(View.VISIBLE);
            holder.btnRemoveFood.setVisibility(View.VISIBLE);
        } else {
            holder.moreDetails.setVisibility(View.GONE);
            holder.btnRemoveFood.setVisibility(View.GONE);
        }

        holder.btnRemoveFood.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FoodUtil.deleteFoodItem(DocumentID);
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                food.setDetailsShown(!food.isDetailsShown());
                notifyItemChanged(holder.getAdapterPosition());
            }
        });
    }

    @Override
    public int getItemCount() {
        return super.getItemCount();
    }

    public static class NutritionItemHolder extends RecyclerView.ViewHolder {
        TextView foodName, calories, moreDetails;
        Button btnRemoveFood;
        public NutritionItemHolder(@NonNull View itemView, RecyclerViewInterface recyclerViewInterface) {
            super(itemView);
            foodName = itemView.findViewById(R.id.txtNutrientNameLabel);
            calories = itemView.findViewById(R.id.txtNutrientValueLabel);
            moreDetails = itemView.findViewById(R.id.txtFoodFurtherDetails);
            btnRemoveFood = itemView.findViewById(R.id.btnRemoveFood);
        }
    }
}
