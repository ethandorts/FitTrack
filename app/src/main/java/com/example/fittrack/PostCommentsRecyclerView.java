package com.example.fittrack;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.FirebaseFirestore;

public class PostCommentsRecyclerView extends FirestoreRecyclerAdapter<CommentModel, PostCommentsRecyclerView.PostCommentHolder> {
    private Context context;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseDatabaseHelper DatabaseUtil = new FirebaseDatabaseHelper(db);
    private String GroupID;
    private String PostID;

    public PostCommentsRecyclerView(@NonNull FirestoreRecyclerOptions<CommentModel> options, Context context, String GroupID, String PostID) {
        super(options);
        this.context = context;
        this.GroupID = GroupID;
        this.PostID = PostID;
    }

    @Override
    protected void onBindViewHolder(@NonNull PostCommentHolder holder, int i, @NonNull CommentModel comment) {
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), DeletePostActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("GroupID", GroupID);
                intent.putExtra("PostID", PostID);
                context.startActivity(intent);
            }
        });
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
    public PostCommentHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.comment_row, parent, false);
        return new PostCommentHolder(view);
    }

    public static class PostCommentHolder extends RecyclerView.ViewHolder {
        TextView txtUser, txtComment;

        public PostCommentHolder(@NonNull View itemView) {
            super(itemView);
            txtUser = itemView.findViewById(R.id.txtNutrientNameLabel);
            txtComment = itemView.findViewById(R.id.txtNutrientValueLabel);
        }
    }
}
