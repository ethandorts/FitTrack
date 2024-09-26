package com.example.fittrack;

import static java.security.AccessController.getContext;

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

public class GroupsRecyclerViewAdapter extends RecyclerView.Adapter<GroupsRecyclerViewAdapter.GroupsViewHolder> {
    private Context context;
    private ArrayList<GroupModel> groupsList = new ArrayList<>();

    public GroupsRecyclerViewAdapter(Context context) {
        this.context = context;
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
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(holder.itemView.getContext(), GroupActivity.class);
                intent.putExtra("GroupName", group.getGroupName());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return groupsList.size();
    }

    public void updateGroups(ArrayList<GroupModel> groups) {
        this.groupsList = groups;
        notifyDataSetChanged();
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
