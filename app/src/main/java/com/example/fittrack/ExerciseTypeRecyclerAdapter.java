package com.example.fittrack;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ExerciseTypeRecyclerAdapter extends RecyclerView.Adapter<ExerciseTypeRecyclerAdapter.ExerciseTypeViewHolder> {
    private Context context;
    private ArrayList<ExerciseTypeModel> exerciseTypes = new ArrayList<>();

    public ExerciseTypeRecyclerAdapter(Context context, ArrayList<ExerciseTypeModel> exerciseTypes) {
        this.context = context;
        this.exerciseTypes = exerciseTypes;
    }

    @NonNull
    @Override
    public ExerciseTypeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.exercise_type_row, parent, false);
        return new ExerciseTypeRecyclerAdapter.ExerciseTypeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ExerciseTypeViewHolder holder, int position) {
        ExerciseTypeModel exerciseType = exerciseTypes.get(position);
        holder.txtExerciseType.setText(exerciseType.getExerciseType());
        holder.imgExerciseType.setImageResource(exerciseType.getImgExerciseType());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, MainActivity.class);
                intent.putExtra("Type", exerciseType.getExerciseType());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return exerciseTypes.size();
    }

    public static class ExerciseTypeViewHolder extends RecyclerView.ViewHolder {
        TextView txtExerciseType;
        ImageView imgExerciseType;

        public ExerciseTypeViewHolder(@NonNull View itemView) {
            super(itemView);
            txtExerciseType = itemView.findViewById(R.id.txtBadgeDescription);
            imgExerciseType = itemView.findViewById(R.id.imgBadge);
        }
    }
}
