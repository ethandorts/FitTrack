package com.example.fittrack;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashSet;

public class GroupOverviewActivity extends AppCompatActivity {
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private String currentUser = mAuth.getUid();
    private TextView txtRunningGroupInfo, txtGroupType, txtMembersSize, txtDescription;
    private ImageView imgGroupProfile;
    private Button btnAdminJoinRequests;
    private RecyclerView membersRecyclerView;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private GroupsDatabaseUtil groupUtil = new GroupsDatabaseUtil(db);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_overview);

        txtRunningGroupInfo = findViewById(R.id.txtClubNameOverview);
        txtGroupType = findViewById(R.id.txtGroupType);
        txtMembersSize = findViewById(R.id.txtMemberSize);
        txtDescription = findViewById(R.id.txtGroupDescription);
        btnAdminJoinRequests = findViewById(R.id.btnAdminRequests);
        membersRecyclerView = findViewById(R.id.membersRecyclerView);
        imgGroupProfile = findViewById(R.id.membersListLogo);

        Intent intent = getIntent();
        String GroupName = intent.getStringExtra("GroupName");
        int GroupSize = intent.getIntExtra("GroupSize", 0);
        String GroupActivity = intent.getStringExtra("GroupActivity");
        String GroupID = intent.getStringExtra("GroupID");
        String GroupShortDescription = intent.getStringExtra("GroupShortDescription");

        txtRunningGroupInfo.setText(GroupName);
        txtGroupType.setText(GroupActivity);
        txtMembersSize.setText(GroupSize + " members");
        txtDescription.setText(GroupShortDescription);

        groupUtil.retrieveGroupProfileImage(GroupID + ".jpg", new GroupsDatabaseUtil.GroupPictureCallback() {
            @Override
            public void onCallback(Uri PicturePath) {
                if(PicturePath != null) {
                    Glide.with(getApplicationContext())
                            .load(PicturePath)
                            .into(imgGroupProfile);
                } else {
                    Log.e("No profile picture found", "No profile picture found.");
                    imgGroupProfile.setImageResource(R.drawable.running_club_background);
                }
            }
        });

        btnAdminJoinRequests.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(GroupOverviewActivity.this, AdminGroupRequestsActivity.class);
                intent.putExtra("GroupID", GroupID);
                startActivity(intent);
            }
        });

        groupUtil.retrieveUserRoles(GroupID, new GroupsDatabaseUtil.UserRolesCallback() {
            @Override
            public void onCallback(ArrayList<String> runners, ArrayList<String> admins) {
                if(!admins.contains(currentUser)) {
                    btnAdminJoinRequests.setVisibility(View.INVISIBLE);
                }
                ArrayList<MemberModel> members = new ArrayList<>();
                HashSet<String> adminSet = new HashSet<>(admins);
                for(String admin : admins) {
                    members.add(new MemberModel(admin,  true));
                }
                for(String runner: runners) {
                    if(!adminSet.contains(runner)) {
                        members.add(new MemberModel(runner, false));
                    }
                }

                MembersRecyclerViewAdapter adapter = new MembersRecyclerViewAdapter(GroupOverviewActivity.this, members, GroupID);
                membersRecyclerView.setLayoutManager(new LinearLayoutManager(GroupOverviewActivity.this));
                membersRecyclerView.setAdapter(adapter);
                membersRecyclerView.addItemDecoration(new DividerItemDecoration(GroupOverviewActivity.this, DividerItemDecoration.VERTICAL));
            }
        });
    }
}