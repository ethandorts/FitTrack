package com.example.fittrack;

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

public class DirectMessagingMenuViewAdapter extends RecyclerView.Adapter<DirectMessagingMenuViewAdapter.ChatUsersViewHolder> {
    private final RecyclerViewInterface recyclerViewInterface;
    private Context context;
    private ArrayList<UserModel> userList = new ArrayList<>();

    public DirectMessagingMenuViewAdapter(Context context, RecyclerViewInterface recyclerViewInterface) {
        this.context = context;
        this.recyclerViewInterface = recyclerViewInterface;
    }

    @NonNull
    @Override
    public ChatUsersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.user_messaging_row, parent, false);
        return new ChatUsersViewHolder(view, recyclerViewInterface);
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }
    

    public void onBindViewHolder(@NonNull ChatUsersViewHolder holder, int position) {
        UserModel user = userList.get(position);
        holder.UserName.setText(user.getUserFullName());
        holder.LastMessage.setText(user.getLastMessage());
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, MessagingChatActivity.class);
            intent.putExtra("name", user.getUserFullName());
            intent.putExtra("UserID", user.getUserID());
            context.startActivity(intent);
        });
    }

    public void updateUsers(ArrayList<UserModel> users) {
        this.userList = users;
        notifyDataSetChanged();
    }

    public static class ChatUsersViewHolder extends RecyclerView.ViewHolder {
        TextView UserName, LastMessage;
        ImageView UserProfileImage;

        public ChatUsersViewHolder(@NonNull View itemView, RecyclerViewInterface recyclerViewInterface) {
            super(itemView);
            UserName = itemView.findViewById(R.id.txtCommentName);
            LastMessage = itemView.findViewById(R.id.txtComment);
            UserProfileImage = itemView.findViewById(R.id.user_comment_icon);
        }
    }
}
