package com.example.fittrack;

import android.content.Context;
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

public class PostsRecyclerViewAdapter extends FirestoreRecyclerAdapter<PostModel, PostsRecyclerViewAdapter.PostsViewHolder> {
    private Context context;

    public PostsRecyclerViewAdapter(@NonNull FirestoreRecyclerOptions<PostModel> options, Context context) {
        super(options);
        this.context = context;
    }

    @NonNull
    @Override
    public PostsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.group_post_row, parent, false);
        return new PostsRecyclerViewAdapter.PostsViewHolder(view);
    }

    @Override
    protected void onBindViewHolder(@NonNull PostsViewHolder holder, int i, @NonNull PostModel post) {
        holder.txtName.setText(post.getUserID());
        holder.txtDate.setText(post.getDate());
        holder.txtDescription.setText(post.getDescription());
    }

    public static class PostsViewHolder extends RecyclerView.ViewHolder {
        TextView txtName, txtDate, txtDescription;
        ImageView PostUserImage;

        public PostsViewHolder(@NonNull View itemView) {
            super(itemView);
            txtName = itemView.findViewById(R.id.txtPostUserName);
            txtDate = itemView.findViewById(R.id.txtPostDate);
            txtDescription = itemView.findViewById(R.id.txtPostDescription);
        }
    }
}
