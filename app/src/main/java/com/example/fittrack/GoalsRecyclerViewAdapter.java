package com.example.fittrack;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

public class GoalsRecyclerViewAdapter extends FirestoreRecyclerAdapter<GoalModel, GoalsRecyclerViewAdapter.GoalsViewHolder> {
    private Context context;

    public GoalsRecyclerViewAdapter(@NonNull FirestoreRecyclerOptions<GoalModel> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull GoalsViewHolder goalsViewHolder, int i, @NonNull GoalModel goalModel) {

    }

    @NonNull
    @Override
    public GoalsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.activity_stats_view_row, parent, false);
        return new GoalsRecyclerViewAdapter.GoalsViewHolder(view);
    }

    public class GoalsViewHolder extends RecyclerView.ViewHolder {

        public GoalsViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}