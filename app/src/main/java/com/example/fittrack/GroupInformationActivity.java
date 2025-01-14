package com.example.fittrack;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class GroupInformationActivity extends AppCompatActivity {
    private TextView txtGroupName, txtMembersNumber, txtDescription, txtLocation, txtRequestStatus;
    private Button btnJoinClub;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private GroupsDatabaseUtil groupsUtil = new GroupsDatabaseUtil(db);
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private String currentUser = mAuth.getUid();
    private String GroupID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_information);

        txtGroupName = findViewById(R.id.txtClubNameOverview);
        txtMembersNumber = findViewById(R.id.txtMembersNoValue);
        txtLocation = findViewById(R.id.txtLocationOverviewValue);
        txtDescription = findViewById(R.id.txtGroupDescriptionValue);
        btnJoinClub = findViewById(R.id.btnJoinClub);
        txtRequestStatus = findViewById(R.id.txtRequestSent);
        btnJoinClub = findViewById(R.id.btnJoinClub);


        Intent intent = getIntent();
        String GroupName = intent.getStringExtra("GroupName");
        GroupID = intent.getStringExtra("GroupID");
        String GroupShortDescription = intent.getStringExtra("GroupShortDescription");
        String GroupDescription = intent.getStringExtra("GroupDescription");
        String Location = intent.getStringExtra("Location");
        int MembersNumber = intent.getIntExtra("MembersValue", 0);
        String ActivityType = intent.getStringExtra("ActivityType");

        txtGroupName.setText(GroupName);
        txtDescription.setText(GroupDescription);
        txtLocation.setText(Location);
        txtMembersNumber.setText(String.valueOf(MembersNumber) + " Members" + "\n" + ActivityType);

        groupsUtil.checkUserStatusinGroup(GroupID, currentUser, new GroupsDatabaseUtil.RequestsCallback() {
            @Override
            public void onCallback(boolean RequestStatus) {
                if(RequestStatus) {
                    txtRequestStatus.setText("Request Pending");
                    btnJoinClub.setClickable(false);
                } else {
                    txtRequestStatus.setText("Send a Request to Join this Fitness Group");
                }
            }
        });

        btnJoinClub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                groupsUtil.addGroupRequest(GroupID, currentUser);
                txtRequestStatus.setVisibility(View.VISIBLE);
                txtRequestStatus.setText("Request Sent to Join Group");
            }
        });

    }
}