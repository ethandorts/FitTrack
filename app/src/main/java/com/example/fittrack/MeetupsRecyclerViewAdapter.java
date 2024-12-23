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

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

import java.util.ArrayList;

public class MeetupsRecyclerViewAdapter extends FirestoreRecyclerAdapter<MeetupModel, MeetupsRecyclerViewAdapter.MeetupsViewHolder> {
    private Context context;

    public MeetupsRecyclerViewAdapter(@NonNull FirestoreRecyclerOptions<MeetupModel> options, Context context) {
        super(options);
        this.context = context;
    }


    @NonNull
    @Override
    public MeetupsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.meetup_row, parent, false);
        return new MeetupsRecyclerViewAdapter.MeetupsViewHolder(view);
    }

    @Override
    protected void onBindViewHolder(@NonNull MeetupsViewHolder holder, int i, @NonNull MeetupModel meetup) {
        holder.Title.setText(meetup.getTitle());
        holder.Username.setText(meetup.getUser());
        holder.Details.setText(meetup.getDetails());
        holder.Location.setText(meetup.getLocation());
    }

    public static class MeetupsViewHolder extends RecyclerView.ViewHolder {
        TextView Title, Username, Location, Details;
        ImageView Status;

        public MeetupsViewHolder(@NonNull View itemView) {
            super(itemView);
            Title = itemView.findViewById(R.id.txtMeetupTitle);
            Username = itemView.findViewById(R.id.txtMeetupUser);
            Location = itemView.findViewById(R.id.txtMeetupLocation);
            Details = itemView.findViewById(R.id.txtMeetupDetails);
            Status = itemView.findViewById(R.id.imgStatus);
        }
    }
}
