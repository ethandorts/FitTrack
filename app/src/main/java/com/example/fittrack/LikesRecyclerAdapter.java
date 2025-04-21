package com.example.fittrack;

import android.content.Context;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class LikesRecyclerAdapter extends RecyclerView.Adapter<LikesRecyclerAdapter.LikesViewHolder>{
    private Context context;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private ArrayList<LikeModel> likesList = new ArrayList<>();
    private FirebaseDatabaseHelper DatabaseUtil = new FirebaseDatabaseHelper(db);

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
        DatabaseUtil.retrieveChatName(model.getLikeName(), new FirebaseDatabaseHelper.ChatUserCallback() {
            @Override
            public void onCallback(String Chatname) {
                holder.txtLikeName.setText(Chatname);
            }
        });
        System.out.println("URI: " + model.getProfilePicture());

        DatabaseUtil.retrieveProfilePicture(model.getLikeName() + ".jpeg", new FirebaseDatabaseHelper.ProfilePictureCallback() {
            @Override
            public void onCallback(Uri PicturePath) {
                if (PicturePath != null) {
                    Glide.with(holder.itemView.getContext())
                            .load(model.getProfilePicture())
                            .error(R.drawable.profile)
                            .into(holder.img);
                } else {
                    Log.e("No profile picture found", "No profile picture found.");
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return likesList.size();
    }

    public static class LikesViewHolder extends RecyclerView.ViewHolder {
        TextView txtLikeName;
        ImageView img;

        public LikesViewHolder(@NonNull View itemView) {
            super(itemView);
            txtLikeName = itemView.findViewById(R.id.txtLikeName);
            img = itemView.findViewById(R.id.imgLikeProfile);
        }
    }
}
