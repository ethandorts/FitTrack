package com.example.fittrack;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class ViewAttendeesActivity extends AppCompatActivity {
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final GroupsDatabaseUtil groupsUtil = new GroupsDatabaseUtil(db);
    private String GroupID;
    private String MeetupID;
    private RecyclerView recyclerAttendees;
    private LikesRecyclerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_attendees);

        Intent intent = getIntent();
        GroupID = intent.getStringExtra("GroupID");
        MeetupID = intent.getStringExtra("MeetupID");

        recyclerAttendees = findViewById(R.id.recyclerAttendees);
        recyclerAttendees.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadAttendees();
    }

    private void loadAttendees() {
        groupsUtil.getMeetupAccepted(GroupID, MeetupID, new GroupsDatabaseUtil.AcceptedCallback() {
            @Override
            public void onCallback(List<LikeModel> accepted) {
                System.out.println(accepted.size());
                adapter = new LikesRecyclerAdapter(ViewAttendeesActivity.this, new ArrayList<>(accepted));
                recyclerAttendees.setAdapter(adapter);
            }
        });
    }
}
