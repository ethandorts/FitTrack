package com.example.fittrack;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class MembersRecyclerViewAdapter extends RecyclerView.Adapter<MembersRecyclerViewAdapter.MembersViewHolder> {
    private Context context;
    private ArrayList<MemberModel> memberList = new ArrayList<>();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private GroupsDatabaseUtil groupUtil = new GroupsDatabaseUtil(db);
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
        holder.txtMemberName.setText(member.getUserName());
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
            txtMemberName = itemView.findViewById(R.id.txtRequestName);
            AdminImage = itemView.findViewById(R.id.imgAdmin);
        }
    }
}
