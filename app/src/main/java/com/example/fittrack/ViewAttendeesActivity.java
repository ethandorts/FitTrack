package com.example.fittrack;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class ViewAttendeesActivity extends AppCompatActivity {
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    GroupsDatabaseUtil groupsUtil = new GroupsDatabaseUtil(db);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_attendees);

        Intent intent = getIntent();
        String GroupID = intent.getStringExtra("GroupID");
        String MeetupID = intent.getStringExtra("MeetupID");

        RecyclerView recyclerAttendees = findViewById(R.id.recyclerAttendees);
        groupsUtil.getMeetupAccepted(GroupID, MeetupID, new GroupsDatabaseUtil.AcceptedCallback() {
            @Override
            public void onCallback(List<LikeModel> accepted) {
                LikesRecyclerAdapter adapter = new LikesRecyclerAdapter(getApplicationContext(), (ArrayList<LikeModel>) accepted);
                recyclerAttendees.setAdapter(adapter);
                recyclerAttendees.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
            }
        });
    }
}