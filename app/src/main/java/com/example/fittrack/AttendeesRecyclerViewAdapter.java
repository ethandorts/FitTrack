package com.example.fittrack;


import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class AttendeesRecyclerViewAdapter extends RecyclerView.Adapter<AttendeesRecyclerViewAdapter.AttendeeViewHolder>{
    private Context context;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private ArrayList<AttendeeModel> attendees = new ArrayList<>();
    private FirebaseDatabaseHelper DatabaseUtil = new FirebaseDatabaseHelper(db);

    public AttendeesRecyclerViewAdapter(Context context, ArrayList<AttendeeModel> attendeesList) {
        this.context = context;
        this.attendees = attendeesList;
    }

    @NonNull
    @Override
    public AttendeeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.likes_row, parent, false);
        return new AttendeesRecyclerViewAdapter.AttendeeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AttendeeViewHolder holder, int position) {
        AttendeeModel model = attendees.get(position);
        DatabaseUtil.retrieveChatName(model.getAttendee(), new FirebaseDatabaseHelper.ChatUserCallback() {
            @Override
            public void onCallback(String Chatname) {
                System.out.println(Chatname);
                System.out.println(model.getAttendee());
                holder.txtAttendee.setText(Chatname);
            }
        });
        DatabaseUtil.retrieveProfilePicture(model.getAttendee() + ".jpeg", new FirebaseDatabaseHelper.ProfilePictureCallback() {
            @Override
            public void onCallback(Uri PicturePath) {
                Glide.with(holder.itemView.getContext())
                        .load(PicturePath)
                        .error(R.drawable.profile)
                        .into(holder.img);
            }
        });
    }

    @Override
    public int getItemCount() {
        return attendees.size();
    }

    public static class AttendeeViewHolder extends RecyclerView.ViewHolder {
        TextView txtAttendee;
        ImageView img;

        public AttendeeViewHolder(@NonNull View itemView) {
            super(itemView);
            txtAttendee = itemView.findViewById(R.id.txtLikeName);
            img = itemView.findViewById(R.id.imgLikeProfile);
        }
    }
}

