package com.example.fittrack;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class MessagesRecyclerViewAdapter extends RecyclerView.Adapter<MessagesRecyclerViewAdapter.MessageBoxHolder> {
    private Context context;
    private ArrayList<MessageModel> messages;

    public MessagesRecyclerViewAdapter(Context context, ArrayList<MessageModel> messages) {
        this.context = context;
        this.messages = messages;
    }

    @NonNull
    public MessageBoxHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.sender_message_box, parent, false);
        return new MessagesRecyclerViewAdapter.MessageBoxHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageBoxHolder holder, int position) {
        MessageModel message = messages.get(position);

        holder.MessageBody.setText(message.getMessageContent());

    }

    @Override
    public int getItemCount() {return messages.size();}

    public static class MessageBoxHolder extends RecyclerView.ViewHolder {
        TextView MessageBody;
        public MessageBoxHolder (@NonNull View itemView) {
            super(itemView);
            MessageBody = itemView.findViewById(R.id.MessageBody);
        }
    }
}
