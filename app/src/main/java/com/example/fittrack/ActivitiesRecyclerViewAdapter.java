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

public class ActivitiesRecyclerViewAdapter extends RecyclerView.Adapter<ActivitiesRecyclerViewAdapter.MyViewHolder> {

    private Context context;
    private ArrayList<ActivityModel> userActivities;

    public ActivitiesRecyclerViewAdapter(Context context, ArrayList<ActivityModel> userActivities) {
        this.context = context;
        this.userActivities = userActivities;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.activity_stats_view_row, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        ActivityModel activity = userActivities.get(position);

        holder.activityType.setText(activity.getActivityType());
        holder.activityDate.setText(activity.getActivityDate());
        holder.activityDistance.setText(activity.getActivityDistance());
        holder.activityTime.setText(activity.getActivityTime());
        holder.activityPace.setText(activity.getActivityPace());
        holder.activityUser.setText(activity.getActivityUser());

    }

    @Override
    public int getItemCount() {
        return userActivities.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView activityType, activityDate, activityDistance, activityTime, activityPace, activityUser;
        ImageView activityTypeImage, activityUserImage;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            activityType = itemView.findViewById(R.id.txtRunningActivityTitle);
            activityDate = itemView.findViewById(R.id.txtActivityDateValue);
            activityDistance = itemView.findViewById(R.id.txtDistanceValue);
            activityTime = itemView.findViewById(R.id.txtTimeValue);
            activityPace = itemView.findViewById(R.id.txtPaceValue);
            activityUser = itemView.findViewById(R.id.txtUserNameValue);
            activityTypeImage = itemView.findViewById(R.id.running_icon);
            activityUserImage = itemView.findViewById(R.id.profileImage);
        }
    }
}
