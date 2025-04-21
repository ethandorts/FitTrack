package com.example.fittrack;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class RequestsRecyclerViewAdapter extends RecyclerView.Adapter<RequestsRecyclerViewAdapter.RequestsViewHolder>  {
    private Context context;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private ArrayList<RequestModel> requestList = new ArrayList<>();
    private GroupsDatabaseUtil groupsDatabaseUtil = new GroupsDatabaseUtil(db);
    private FirebaseDatabaseHelper DatabaseUtil = new FirebaseDatabaseHelper(db);
    private String GroupID;

    public RequestsRecyclerViewAdapter(Context context, ArrayList<RequestModel> requestList, String GroupID) {
        this.context = context;
        this.requestList = requestList;
        this.GroupID = GroupID;
    }

    @NonNull
    @Override
    public RequestsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.request_row, parent, false);
        return new RequestsRecyclerViewAdapter.RequestsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RequestsViewHolder holder, int position) {
        RequestModel request = requestList.get(position);
        DatabaseUtil.retrieveChatName(request.getUserName(), new FirebaseDatabaseHelper.ChatUserCallback() {
            @Override
            public void onCallback(String Chatname) {
                holder.txtRequestUsername.setText(Chatname);
            }
        });

        DatabaseUtil.retrieveProfilePicture(request.getUserName() + ".jpeg", new FirebaseDatabaseHelper.ProfilePictureCallback() {
            @Override
            public void onCallback(Uri PicturePath) {
                Glide.with(holder.itemView.getContext())
                        .load(PicturePath)
                        .error(R.drawable.profile)
                        .into(holder.UserImage);
            }
        });

        holder.btnAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                groupsDatabaseUtil.updateRunners(GroupID, request.getUserName());
                holder.btnAccept.setVisibility(View.INVISIBLE);
                holder.btnReject.setVisibility(View.INVISIBLE);
                holder.RequestMessage.setVisibility(View.VISIBLE);
                holder.RequestMessage.setText("Request Accepted");
            }
        });

        holder.btnReject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println(request.getUserName());
                groupsDatabaseUtil.removeRequested(GroupID, request.getUserName());
                holder.btnAccept.setVisibility(View.INVISIBLE);
                holder.btnReject.setVisibility(View.INVISIBLE);
                holder.RequestMessage.setVisibility(View.VISIBLE);
                holder.RequestMessage.setText("Request Rejected");
            }
        });
    }

    @Override
    public int getItemCount() {
        return requestList.size();
    }

    public static class RequestsViewHolder extends RecyclerView.ViewHolder {
        private TextView txtRequestUsername, RequestMessage;
        private Button btnAccept, btnReject;
        private ImageView UserImage;

        public RequestsViewHolder(@NonNull View itemView) {
            super(itemView);
            txtRequestUsername = itemView.findViewById(R.id.txtLikeName);
            UserImage = itemView.findViewById(R.id.imgLikeProfile);
            btnAccept = itemView.findViewById(R.id.btnRequestAccept);
            btnReject = itemView.findViewById(R.id.btnRequestReject);
//            RequestMessage = itemView.findViewById(R.id.txtRequestMessage);
        }
    }
}
