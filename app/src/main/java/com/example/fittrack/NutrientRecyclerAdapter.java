package com.example.fittrack;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class NutrientRecyclerAdapter extends RecyclerView.Adapter<NutrientRecyclerAdapter.NutrientViewHolder>{
    private Context context;
    private ArrayList<NutrientModel> nutrientList = new ArrayList<>();

    public NutrientRecyclerAdapter(Context context, ArrayList<NutrientModel> nutrientList) {
        this.context = context;
        this.nutrientList = nutrientList;
    }

    @NonNull
    @Override
    public NutrientViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.nutrient_row, parent, false);
        return new NutrientRecyclerAdapter.NutrientViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NutrientViewHolder holder, int position) {
        NutrientModel nutrient = nutrientList.get(position);
        holder.txtNutrientName.setText(nutrient.getNutrientName());
        holder.txtNutrientValue.setText(String.format("%.2f grams", nutrient.getNutrientValue()));
    }

    @Override
    public int getItemCount() {
        return nutrientList.size();
    }

    public static class NutrientViewHolder extends RecyclerView.ViewHolder {
        TextView txtNutrientName, txtNutrientValue;

        public NutrientViewHolder(@NonNull View itemView) {
            super(itemView);
            txtNutrientName = itemView.findViewById(R.id.txtNutrientNameLabel);
            txtNutrientValue = itemView.findViewById(R.id.txtNutrientValueLabel);
        }
    }
}
