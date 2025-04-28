package com.example.fittrack;

import static androidx.core.content.ContextCompat.startActivity;

import android.content.Context;
import android.content.Intent;
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

public class MembersRecyclerViewAdapter extends RecyclerView.Adapter<MembersRecyclerViewAdapter.MembersViewHolder> {
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private String UserID = mAuth.getUid();
    private Context context;
    private ArrayList<MemberModel> memberList = new ArrayList<>();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private GroupsDatabaseUtil groupUtil = new GroupsDatabaseUtil(db);
    private FirebaseDatabaseHelper userUtil = new FirebaseDatabaseHelper(db);
    private String GroupID;


    public MembersRecyclerViewAdapter(Context context, ArrayList<MemberModel> memberList, String GroupID) {
        this.context = context;
        this.memberList = memberList;
        this.GroupID = GroupID;
    }

    @NonNull
    @Override
    public MembersRecyclerViewAdapter.MembersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.members_row, parent, false);
        return new MembersRecyclerViewAdapter.MembersViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MembersViewHolder holder, int position) {
        MemberModel member = memberList.get(position);
        userUtil.retrieveChatName(member.getUserName(), new FirebaseDatabaseHelper.ChatUserCallback() {
            @Override
            public void onCallback(String Chatname) {
                holder.txtMemberName.setText(Chatname);
            }
        });
        userUtil.retrieveProfilePicture(member.getUserName() + ".jpeg", new FirebaseDatabaseHelper.ProfilePictureCallback() {
            @Override
            public void onCallback(Uri PicturePath) {
                if(PicturePath != null) {
                    Glide.with(context)
                            .load(PicturePath)
                            .into(holder.UserImage);
                } else {
                    Log.e("No profile picture found", "No profile picture found.");
                    holder.UserImage.setImageResource(R.drawable.profile);
                }
            }
        });
        if(!member.isAdmin()) {
            holder.AdminImage.setImageResource(R.drawable.make_admin);
            holder.AdminImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    groupUtil.addAdmin(GroupID, member.getUserName());
                    holder.AdminImage.setImageResource(R.drawable.admin);
                    member.setAdmin(true);
                    //updateAdminPhoto(holder, true);
                }
            });
        } else {
            holder.AdminImage.setImageResource(R.drawable.admin);
            holder.AdminImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    groupUtil.removeAdmin(GroupID, member.getUserName());
                    holder.AdminImage.setImageResource(R.drawable.make_admin);
                    member.setAdmin(false);
                    //updateAdminPhoto(holder, false);
                }
            });
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, ProfileOverview.class);
                intent.putExtra("UserID", member.getUserName());
                context.startActivity(intent);
            }
        });
    }

//    public void updateAdminPhoto(MembersViewHolder holder, boolean isAdmin) {
//        if(isAdmin) {
//            holder.AdminImage.setImageResource(R.drawable.admin);
//        } else {
//            holder.AdminImage.setImageResource(R.drawable.make_admin);
//        }
//    }

    @Override
    public int getItemCount() {
        return memberList.size();
    }

    public static class MembersViewHolder extends RecyclerView.ViewHolder {
        TextView txtMemberName;
        ImageView UserImage, AdminImage;

        public MembersViewHolder(@NonNull View itemView) {
            super(itemView);
            txtMemberName = itemView.findViewById(R.id.txtLikeName);
            AdminImage = itemView.findViewById(R.id.imgAdmin);
            UserImage = itemView.findViewById(R.id.imgLikeProfile);
        }
    }
}
