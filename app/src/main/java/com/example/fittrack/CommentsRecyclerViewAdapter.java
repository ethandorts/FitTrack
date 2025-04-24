package com.example.fittrack;

import static android.provider.CalendarContract.CalendarCache.URI;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.FirebaseFirestore;

public class CommentsRecyclerViewAdapter extends FirestoreRecyclerAdapter<CommentModel,CommentsRecyclerViewAdapter.CommentHolder> {
    private Context context;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseDatabaseHelper DatabaseUtil = new FirebaseDatabaseHelper(db);

    public CommentsRecyclerViewAdapter(@NonNull FirestoreRecyclerOptions<CommentModel> options, Context context) {
        super(options);
        this.context = context;
    }

    @Override
    protected void onBindViewHolder(@NonNull CommentHolder holder, int i, @NonNull CommentModel comment) {
        DatabaseUtil.retrieveUserName(comment.getUserID(), new FirebaseDatabaseHelper.FirestoreUserNameCallback() {
            @Override
            public void onCallback(String FullName, long weight, long height, long activityFrequency, long dailyCalorieGoal, String level, String fitnessGoal) {
                holder.txtUser.setText(FullName);
            }
        });
        holder.txtComment.setText(comment.getComment());
    }

    @NonNull
    @Override
    public CommentHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.comment_row, parent, false);
        return new CommentsRecyclerViewAdapter.CommentHolder(view);
    }


    public static class CommentHolder extends RecyclerView.ViewHolder {
        TextView txtUser, txtComment;
        ImageView imageView;
        public CommentHolder (@NonNull View itemView) {
            super(itemView);
            txtUser = itemView.findViewById(R.id.txtNutrientNameLabel);
            txtComment = itemView.findViewById(R.id.txtNutrientValueLabel);
        }
    }
}
