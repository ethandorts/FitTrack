package com.example.fittrack;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class LikesRecyclerAdapter extends RecyclerView.Adapter<LikesRecyclerAdapter.LikesViewHolder>{
    private Context context;
    private ArrayList<LikeModel> likesList = new ArrayList<>();

    public LikesRecyclerAdapter(Context context, ArrayList<LikeModel> likesList) {
        this.context = context;
        this.likesList = likesList;
    }

    @NonNull
    @Override
    public LikesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.likes_row, parent, false);
        return new LikesRecyclerAdapter.LikesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LikesViewHolder holder, int position) {
        LikeModel model = likesList.get(position);

        holder.txtLikeName.setText(model.getLikeName());
    }

    @Override
    public int getItemCount() {
        return likesList.size();
    }

    public static class LikesViewHolder extends RecyclerView.ViewHolder {
        TextView txtLikeName;

        public LikesViewHolder(@NonNull View itemView) {
            super(itemView);
            txtLikeName = itemView.findViewById(R.id.txtLikeName);
        }
    }
}
