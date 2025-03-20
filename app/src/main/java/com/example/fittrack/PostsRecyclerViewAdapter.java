package com.example.fittrack;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class PostsRecyclerViewAdapter extends FirestoreRecyclerAdapter<PostModel, PostsRecyclerViewAdapter.PostsViewHolder> {
    private Context context;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseDatabaseHelper DatabaseUtil = new FirebaseDatabaseHelper(db);
    private FirebaseDatabaseHelper userUtil = new FirebaseDatabaseHelper(db);
    private String GroupID;

    public PostsRecyclerViewAdapter(@NonNull FirestoreRecyclerOptions<PostModel> options, Context context, String GroupID) {
        super(options);
        this.context = context;
        this.GroupID = GroupID;
    }

    @NonNull
    @Override
    public PostsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.group_post_row, parent, false);
        return new PostsRecyclerViewAdapter.PostsViewHolder(view);
    }

    @Override
    protected void onBindViewHolder(@NonNull PostsViewHolder holder, int i, @NonNull PostModel post) {
        String PostID = getSnapshots().getSnapshot(i).getId();
        DatabaseUtil.retrieveUserName(post.getUserID(), new FirebaseDatabaseHelper.FirestoreUserNameCallback() {
            @Override
            public void onCallback(String FullName, long weight, long height, long activityFrequency, long dailyCalorieGoal) {
                holder.txtName.setText(FullName);
            }
        });
        holder.txtDate.setText(ConversionUtil.TimestamptoString(post.getDate()));
        holder.txtDescription.setText(post.getDescription());
        holder.btnComments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), PostCommentsActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("GroupID", GroupID);
                intent.putExtra("PostID", PostID);
                context.startActivity(intent);
            }
        });
        userUtil.retrieveProfilePicture(post.getUserID() + ".jpeg", new FirebaseDatabaseHelper.ProfilePictureCallback() {
            @Override
            public void onCallback(Uri PicturePath) {
                System.out.println(PicturePath);
                System.out.println("Post Comment UserID: " + post.getUserID());
                System.out.println(context);
                if(PicturePath != null) {
                    Glide.with(holder.itemView.getContext())
                            .load(PicturePath)
                            .into(holder.PostUserImage);
                } else {
                    Log.e("No profile picture found", "No profile picture found.");
                    holder.PostUserImage.setImageResource(R.drawable.profile);
                }
            }
        });
    }

    public static class PostsViewHolder extends RecyclerView.ViewHolder {
        TextView txtName, txtDate, txtDescription;
        ImageView PostUserImage;
        Button btnComments;

        public PostsViewHolder(@NonNull View itemView) {
            super(itemView);
            txtName = itemView.findViewById(R.id.txtPostUserName);
            txtDate = itemView.findViewById(R.id.txtPostDate);
            txtDescription = itemView.findViewById(R.id.txtPostDescription);
            btnComments = itemView.findViewById(R.id.btnComments);
            PostUserImage = itemView.findViewById(R.id.imgPostLogo);
        }
    }
}
