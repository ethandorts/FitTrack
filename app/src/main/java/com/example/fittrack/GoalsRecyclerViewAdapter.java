package com.example.fittrack;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.Timestamp;

import java.text.SimpleDateFormat;
import java.util.Date;

public class GoalsRecyclerViewAdapter extends FirestoreRecyclerAdapter<GoalModel, GoalsRecyclerViewAdapter.GoalsViewHolder> {
    private Context context;

    public GoalsRecyclerViewAdapter(Context context, @NonNull FirestoreRecyclerOptions<GoalModel> options) {
        super(options);
        this.context = context;
    }

    @Override
    protected void onBindViewHolder(@NonNull GoalsViewHolder holder, int i, @NonNull GoalModel model) {
        holder.txtGoalType.setText(model.getGoalType() + " Goal");
        holder.txtGoalDescription.setText(model.getGoalDescription());
        holder.txtDeadline.setText("Completion Target Date: " + ConversionUtil.dateFormatter(model.getEndDate()));
        holder.txtProgress.setText(model.getStatus());
        if(model.getStatus().equals("Completed")) {
            holder.txtProgress.setTextColor(Color.RED);
        }
    }

    @NonNull
    @Override
    public GoalsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.goal_row, parent, false);
        return new GoalsRecyclerViewAdapter.GoalsViewHolder(view);
    }

    public class GoalsViewHolder extends RecyclerView.ViewHolder {
        TextView txtGoalType, txtGoalDescription, txtProgress, txtDeadline;

        public GoalsViewHolder(@NonNull View itemView) {
            super(itemView);
            txtGoalType = itemView.findViewById(R.id.goal_title);
            txtGoalDescription = itemView.findViewById(R.id.goal_description);
            txtProgress = itemView.findViewById(R.id.goal_status);
            txtDeadline = itemView.findViewById(R.id.goal_deadline);
        }
    }
}