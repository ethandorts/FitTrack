package com.example.fittrack;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

import java.util.ArrayList;

public class MessagesRecyclerViewAdapter extends FirestoreRecyclerAdapter<MessageModel, MessagesRecyclerViewAdapter.MessageBoxHolder> {
    private Context context;
    private String UserID;
    private ArrayList<MessageModel> messages = new ArrayList<>();

    public MessagesRecyclerViewAdapter(@NonNull FirestoreRecyclerOptions options, Context context, String UserID) {
        super(options);
        this.context = context;
        this.UserID = UserID;
    }

    @NonNull
    public MessageBoxHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.sender_message_box, parent, false);
        return new MessagesRecyclerViewAdapter.MessageBoxHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageBoxHolder holder, int position, @NonNull MessageModel message) {
        if(message.getSender().equals(UserID)) {
            holder.currentUser.setVisibility(View.VISIBLE);
            holder.currentUserMessageBody.setText(message.getMessage());
            holder.recipientUser.setVisibility(View.GONE);
        } else {
            holder.recipientUser.setVisibility(View.VISIBLE);
            holder.recipientUserMessageBody.setText(message.getMessage());
            holder.currentUser.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {return super.getItemCount();}

    public static class MessageBoxHolder extends RecyclerView.ViewHolder {
        LinearLayout currentUser, recipientUser;
        TextView currentUserMessageBody, recipientUserMessageBody;
        public MessageBoxHolder (@NonNull View itemView) {
            super(itemView);
            currentUser = itemView.findViewById(R.id.currentUserMessage);
            recipientUser = itemView.findViewById(R.id.currentRecipientMessage);
            currentUserMessageBody = itemView.findViewById(R.id.currentUserMessageBody);
            recipientUserMessageBody = itemView.findViewById(R.id.recipientUserMessageBody);
        }
    }
}
