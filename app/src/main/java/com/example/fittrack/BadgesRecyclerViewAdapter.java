package com.example.fittrack;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class BadgesRecyclerViewAdapter extends RecyclerView.Adapter<BadgesRecyclerViewAdapter.BadgeViewHolder> {
    private Context context;
    private ArrayList<BadgeModel> badgeList = new ArrayList<>();

    public BadgesRecyclerViewAdapter(Context context, ArrayList<BadgeModel> badgeList) {
        this.context = context;
        this.badgeList = badgeList;
    }

    @NonNull
    @Override
    public BadgesRecyclerViewAdapter.BadgeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.badge_row, parent, false);
        return new BadgesRecyclerViewAdapter.BadgeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BadgesRecyclerViewAdapter.BadgeViewHolder holder, int position) {
        BadgeModel badge = badgeList.get(position);
        holder.txtBadgeDescription.setText(badge.getBadgeName());
        if(badge.getBadgeName().contains("KM")) {
            holder.badgeActivityType.setImageResource(R.drawable.distance_goal);
        } else if (badge.getBadgeName().contains("First")) {
            holder.badgeActivityType.setImageResource(R.drawable.first_activity);
        } else if (badge.getBadgeName().contains("hour")) {
            holder.badgeActivityType.setImageResource(R.drawable.time_completed);
        } else if (badge.getBadgeName().contains("minutes")) {
            holder.badgeActivityType.setImageResource(R.drawable.time_completed);
        } else if(badge.getBadgeName().contains("Completed a 5KM")) {
            holder.badgeActivityType.setImageResource(R.drawable.fivekm_badge);
        } else if(badge.getBadgeName().contains("Completed a 10KM")) {
            holder.badgeActivityType.setImageResource(R.drawable.ten_km_badge);
        } else if(badge.getBadgeName().contains("Completed a Half Marathon")) {
            holder.badgeActivityType.setImageResource(R.drawable.half_marathon);
        } else if(badge.getBadgeName().contains("Completed a Marathon")) {
            holder.badgeActivityType.setImageResource(R.drawable.marathon_badge);
        } else {
            holder.badgeActivityType.setImageResource(R.drawable.fitness_badge);
        }
    }

    @Override
    public int getItemCount() {
        return badgeList.size();
    }

    public static class BadgeViewHolder extends RecyclerView.ViewHolder {
        TextView txtBadgeDescription;
        ImageView badgeActivityType;
        public BadgeViewHolder(@NonNull View itemView) {
            super(itemView);
            txtBadgeDescription = itemView.findViewById(R.id.txtBadgeDescription);
            badgeActivityType = itemView.findViewById(R.id.imgBadge);
        }
    }
}
