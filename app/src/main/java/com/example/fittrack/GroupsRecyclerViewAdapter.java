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

public class GroupsRecyclerViewAdapter extends RecyclerView.Adapter<GroupsRecyclerViewAdapter.GroupsViewHolder> {
    private Context context;
    private ArrayList<GroupModel> groupsList;

    public GroupsRecyclerViewAdapter(Context context, ArrayList<GroupModel> groupsList) {
        this.context = context;
        this.groupsList = groupsList;
    }

    @NonNull
    @Override
    public GroupsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.group_option, parent, false);
        return new GroupsRecyclerViewAdapter.GroupsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GroupsViewHolder holder, int position) {
        GroupModel group = groupsList.get(position);

        holder.GroupName.setText(group.getGroupName());
        holder.GroupDescription.setText(group.getGroupDescription());
    }

    @Override
    public int getItemCount() {
        return groupsList.size();
    }

    public static class GroupsViewHolder extends RecyclerView.ViewHolder {
        TextView GroupName, GroupDescription;
        ImageView GroupImage;

        public GroupsViewHolder(@NonNull View itemView) {
            super(itemView);
            GroupName = itemView.findViewById(R.id.GroupName);
            GroupDescription = itemView.findViewById(R.id.GroupDescription);
            GroupImage = itemView.findViewById(R.id.group_icon);
        }
    }




}
