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
    private ArrayList<UserModel> userList;

    public DirectMessagingMenuViewAdapter(Context context, ArrayList<UserModel> userList, RecyclerViewInterface recyclerViewInterface) {
        this.context = context;
        this.userList = userList;
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
        holder.LastMessage.setText(user.getUserLastMessage());
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, MessagingChatActivity.class);
            intent.putExtra("name", user.getUserFullName());
            context.startActivity(intent);
        });
    }

    public static class ChatUsersViewHolder extends RecyclerView.ViewHolder {
        TextView UserName, LastMessage;
        ImageView UserProfileImage;

        public ChatUsersViewHolder(@NonNull View itemView, RecyclerViewInterface recyclerViewInterface) {
            super(itemView);
            UserName = itemView.findViewById(R.id.txtUserMessagingName);
            LastMessage = itemView.findViewById(R.id.txtLastMesssageSent);
            UserProfileImage = itemView.findViewById(R.id.user_icon);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(recyclerViewInterface != null) {
                        int position = getAdapterPosition();
                        if( position != RecyclerView.NO_POSITION) {
                            recyclerViewInterface.onItemClick(position);
                        }
                    }
                }
            });
        }
    }
}
