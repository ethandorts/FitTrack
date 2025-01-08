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

public class CommentsRecyclerViewAdapter extends FirestoreRecyclerAdapter<CommentModel,CommentsRecyclerViewAdapter.CommentHolder> {
    private Context context;

    public CommentsRecyclerViewAdapter(@NonNull FirestoreRecyclerOptions<CommentModel> options, Context context) {
        super(options);
        this.context = context;
    }

    @Override
    protected void onBindViewHolder(@NonNull CommentHolder holder, int i, @NonNull CommentModel comment) {
        holder.txtUser.setText(comment.getUserID());
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
        public CommentHolder (@NonNull View itemView) {
            super(itemView);
            txtUser = itemView.findViewById(R.id.txtCommentName);
            txtComment = itemView.findViewById(R.id.txtComment);
        }
    }

}
