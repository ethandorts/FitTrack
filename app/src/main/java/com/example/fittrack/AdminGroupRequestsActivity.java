package com.example.fittrack;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class AdminGroupRequestsActivity extends AppCompatActivity {
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private GroupsDatabaseUtil groupsUtil = new GroupsDatabaseUtil(db);
//    private TextView txtRequestMessage;
    private RecyclerView requestsRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_group_requests);

        Intent intent = getIntent();
        String GroupID = intent.getStringExtra("GroupID");

        requestsRecyclerView = findViewById(R.id.requestRecyclerView);
//        txtRequestMessage = findViewById(R.id.txtRequestMessage);


        groupsUtil.retrieveGroupRequests(GroupID, new GroupsDatabaseUtil.GroupRequestsCallback() {
            @Override
            public void onCallback(List<String> requests) {
                ArrayList<RequestModel> groupRequests = new ArrayList<>();
                for(String request : requests) {
                    RequestModel GroupRequest = new RequestModel(
                            request,
                            null
                    );
                    groupRequests.add(GroupRequest);
                }
                RequestsRecyclerViewAdapter adapter = new RequestsRecyclerViewAdapter(getApplicationContext(), groupRequests, GroupID);
                requestsRecyclerView.setAdapter(adapter);
                requestsRecyclerView.setLayoutManager(new LinearLayoutManager(AdminGroupRequestsActivity.this));
                requestsRecyclerView.setAdapter(adapter);
                requestsRecyclerView.addItemDecoration(new DividerItemDecoration(AdminGroupRequestsActivity.this, DividerItemDecoration.VERTICAL));
            }
        });
    }
}