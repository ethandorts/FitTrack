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

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

import java.util.ArrayList;

public class GroupsRecyclerViewAdapter extends FirestoreRecyclerAdapter<GroupModel, GroupsRecyclerViewAdapter.GroupsViewHolder> {
    private Context context;

    public GroupsRecyclerViewAdapter(@NonNull FirestoreRecyclerOptions<GroupModel> options, Context context) {
        super(options);
        this.context = context;
    }


    @NonNull
    @Override
    public GroupsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.group_option, parent, false);
        return new GroupsRecyclerViewAdapter.GroupsViewHolder(view);
    }

//    @Override
//    public void onBindViewHolder(@NonNull GroupsViewHolder holder, int position) {
//        GroupModel group = groupsList.get(position);
//
//        holder.GroupName.setText(group.getGroupName());
//        holder.GroupDescription.setText(group.getShortDescription());
//        if(isFinding) {
//            holder.itemView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    Intent intent = new Intent(holder.itemView.getContext(), GroupInformationActivity.class);
//                    intent.putExtra("GroupName", group.getGroupName());
//                    intent.putExtra("GroupID", group.getGroupID());
//                    intent.putExtra("GroupShortDescription", group.getShortDescription());
//                    intent.putExtra("GroupDescription", group.getGroupDescription());
//                    intent.putExtra("Location", group.getGroupLocation());
//                    intent.putExtra("MembersValue", group.getMembersNumber().size());
//                    intent.putExtra("ActivityType", group.getActivity());
//                    context.startActivity(intent);
//                }
//            });
//        } else {
//            holder.itemView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    Intent intent = new Intent(holder.itemView.getContext(), GroupActivity.class);
//                    intent.putExtra("GroupName", group.getGroupName());
//                    intent.putExtra("GroupID", group.getGroupID());
//                    intent.putExtra("GroupSize", group.getMembersNumber().size());
//                    intent.putExtra("GroupActivity", group.getActivity());
//                    context.startActivity(intent);
//                }
//            });
//        }
//    }


    @Override
    public int getItemCount() {
        return super.getItemCount();
    }

    @Override
    protected void onBindViewHolder(@NonNull GroupsViewHolder holder, int i, @NonNull GroupModel group) {
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(holder.itemView.getContext(), GroupActivity.class);
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
        holder.GroupName.setText(group.getName());
        holder.GroupDescription.setText(group.getShortDescription());
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
