package com.example.fittrack;


import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class FindGroupsRecyclerViewAdapter extends RecyclerView.Adapter<FindGroupsRecyclerViewAdapter.GroupsViewHolder> {
    private Context context;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private GroupsDatabaseUtil groupsUtil = new GroupsDatabaseUtil(db);
    private ArrayList<GroupModel> groupsList = new ArrayList<>();
    private boolean isFinding;

    public FindGroupsRecyclerViewAdapter(Context context, boolean isFinding) {
        this.context = context;
        this.isFinding = isFinding;
    }

    @NonNull
    @Override
    public GroupsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.group_option, parent, false);
        return new FindGroupsRecyclerViewAdapter.GroupsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GroupsViewHolder holder, int position) {
        GroupModel group = groupsList.get(position);

        holder.GroupName.setText(group.getName());
        holder.GroupDescription.setText(group.getShortDescription());

        groupsUtil.retrieveGroupProfileImage(group.getGroupID() + ".jpg", new GroupsDatabaseUtil.GroupPictureCallback() {
            @Override
            public void onCallback(Uri PicturePath) {
                if (PicturePath != null) {
                    Glide.with(context)
                            .load(PicturePath)
                            .into(holder.GroupImage);
                } else {
                    Log.e("No profile picture found", "No profile picture found.");
                    holder.GroupImage.setImageResource(R.drawable.running_club_background);
                }
            }
        });

        if(isFinding) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(holder.itemView.getContext(), GroupInformationActivity.class);
                    intent.putExtra("GroupName", group.getName());
                    intent.putExtra("GroupID", group.getGroupID());
                    intent.putExtra("GroupShortDescription", group.getShortDescription());
                    intent.putExtra("GroupDescription", group.getDescription());
                    intent.putExtra("Location", group.getLocation());
                    intent.putExtra("MembersValue", group.getRunners().size());
                    intent.putExtra("ActivityType", group.getActivity());
                    context.startActivity(intent);
                }
            });
        } else {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(holder.itemView.getContext(), GroupActivity.class);
                    intent.putExtra("GroupName", group.getName());
                    intent.putExtra("GroupID", group.getGroupID());
                    intent.putExtra("GroupSize", group.getRunners().size());
                    intent.putExtra("GroupActivity", group.getActivity());
                    context.startActivity(intent);
                }
            });
        }
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