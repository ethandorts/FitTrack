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

    public LeaderboardRecyclerAdapter(Context context, ArrayList<LeaderboardModel> leaderboardList) {
        this.context = context;
        this.leaderboardList = leaderboardList;
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
        holder.DistanceValue.setText(String.valueOf(leaderboardModel.getDistance()));
        userUtil.retrieveUserName(leaderboardModel.getUsername(), new FirebaseDatabaseHelper.FirestoreUserNameCallback() {
            @Override
            public void onCallback(String FullName, long weight, long height, long activityFrequency, long dailyCalorieGoal) {
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
