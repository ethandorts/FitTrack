package com.example.fittrack;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class LeaderboardRecyclerAdapter extends RecyclerView.Adapter<LeaderboardRecyclerAdapter.LeaderboardViewHolder>{
    private Context context;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseDatabaseHelper userUtil = new FirebaseDatabaseHelper(db);
    private ArrayList<LeaderboardModel> leaderboardList = new ArrayList<>();
    private String selectedMetric;

    public LeaderboardRecyclerAdapter(Context context, ArrayList<LeaderboardModel> leaderboardList, String selectedMetric) {
        this.context = context;
        this.leaderboardList = leaderboardList;
        this.selectedMetric = selectedMetric;
    }

    @NonNull
    @Override
    public LeaderboardRecyclerAdapter.LeaderboardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.leaderboard_stat_row, parent, false);
        return new LeaderboardRecyclerAdapter.LeaderboardViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LeaderboardViewHolder holder, int position) {
        LeaderboardModel leaderboardModel = leaderboardList.get(position);
        holder.Number.setText(String.valueOf(position + 1));
        if(selectedMetric.equals("Time")) {
            holder.DistanceValue.setText(ConversionUtil.convertSecondsToTime((int) leaderboardModel.getDistance()));
        } else if (selectedMetric.equals("ActivityFrequency")) {
            holder.DistanceValue.setText(String.format("%d", (int) leaderboardModel.getDistance()));
        } else if (selectedMetric.equals("Distance")) {
            holder.DistanceValue.setText(String.format("%.2f KM", leaderboardModel.getDistance()));
        }
        userUtil.retrieveUserName(leaderboardModel.getUsername(), new FirebaseDatabaseHelper.FirestoreUserNameCallback() {
            @Override
            public void onCallback(String FullName, long weight, long height, long activityFrequency, long dailyCalorieGoal, String level, String fitnessGoal) {
                holder.UserName.setText(FullName);
            }
        });

        if(leaderboardModel.getActivityID() != null) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, OverviewFitnessStats.class);
                    intent.putExtra("ActivityID", String.valueOf(leaderboardModel.getActivityID()));
                    context.startActivity(intent);
                }
            });
        }
    }


    @Override
    public int getItemCount() {
        return leaderboardList.size();
    }

    public static class LeaderboardViewHolder extends RecyclerView.ViewHolder {
        TextView Number, UserName, DistanceValue;

        public LeaderboardViewHolder(@NonNull View itemView) {
            super(itemView);
            Number = itemView.findViewById(R.id.leaderboard_number);
            DistanceValue = itemView.findViewById(R.id.txtNutrientValueLabel);
            UserName = itemView.findViewById(R.id.txtNutrientNameLabel);
        }
    }
}
