package com.example.fittrack;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class MeetupsRecyclerViewAdapter extends FirestoreRecyclerAdapter<MeetupModel, MeetupsRecyclerViewAdapter.MeetupsViewHolder> {
    private Context context;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CommentUtil commentUtil = new CommentUtil(db);
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private String currentUser = mAuth.getCurrentUser().getUid();
    private String GroupID;

    public MeetupsRecyclerViewAdapter(@NonNull FirestoreRecyclerOptions<MeetupModel> options, Context context, String GroupID) {
        super(options);
        this.context = context;
        this.GroupID = GroupID;
    }

    @NonNull
    @Override
    public MeetupsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.meetup_row, parent, false);
        return new MeetupsRecyclerViewAdapter.MeetupsViewHolder(view);
    }

    @Override
    protected void onBindViewHolder(@NonNull MeetupsViewHolder holder, int i, @NonNull MeetupModel meetup) {
        holder.itemView.setTag(meetup.getMeetupID());
        holder.Title.setText(meetup.getTitle());
        holder.Username.setText(meetup.getUser());
        holder.Date.setText(dateFormatter(meetup.getDate()));
        holder.Details.setText(meetup.getDescription());
        holder.Location.setText(meetup.getLocation());
        System.out.println("MeetupID is " + meetup.getMeetupID());

        commentUtil.checkMeetupStatus(GroupID, currentUser, meetup.getMeetupID(), new CommentUtil.StatusCheckCallback() {
            @Override
            public void onCallback(boolean statusCheck) {
                if(statusCheck) {
                    holder.Status.setImageResource(R.drawable.green_accept);
                } else {
                    holder.Status.setImageResource(R.drawable.red_reject);
                }
            }
        });

        holder.btnAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.Status.setImageResource(R.drawable.green_accept);
                commentUtil.acceptMeetup(GroupID, meetup.getMeetupID(), currentUser);
            }
        });
        holder.btnReject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.Status.setImageResource(R.drawable.red_reject);
                commentUtil.rejectMeetup(GroupID, meetup.getMeetupID(), currentUser);
            }
        });
    }

    public static class MeetupsViewHolder extends RecyclerView.ViewHolder {
        TextView Title, Username, Location, Details, Date;
        Button btnAccept, btnReject;
        ImageView Status;

        public MeetupsViewHolder(@NonNull View itemView) {
            super(itemView);
            Title = itemView.findViewById(R.id.txtMeetupTitle);
            Username = itemView.findViewById(R.id.txtMeetupUser);
            Date = itemView.findViewById(R.id.txtMeetupDate);
            Location = itemView.findViewById(R.id.txtMeetupLocation);
            Details = itemView.findViewById(R.id.txtMeetupDetails);
            Status = itemView.findViewById(R.id.imgStatus);
            btnAccept = itemView.findViewById(R.id.btnAccept);
            btnReject = itemView.findViewById(R.id.btnReject);
        }
    }
    private String dateFormatter(Timestamp timestamp) {
        Date date = timestamp.toDate();
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        String formattedDate = formatter.format(date);

        return formattedDate;
    }
}
