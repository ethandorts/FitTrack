package com.example.fittrack;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DirectMessagingMenu extends AppCompatActivity implements RecyclerViewInterface {
    private DirectMessagingMenuViewAdapter chatsAdapter;
    private ArrayList<UserModel> userList = new ArrayList<UserModel>();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseDatabaseHelper DatabaseUtil = new FirebaseDatabaseHelper(db);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_direct_messaging_menu);

        DatabaseUtil.retrieveAllUsers(new FirebaseDatabaseHelper.FirestoreAllUsersCallback() {
            @Override
            public void onCallback(List<Map<String, Object>> data) {
                for(Map<String, Object> userMap : data) {
                    UserModel user = new UserModel((String) userMap.get("FirstName") + " " + userMap.get("Surname"),
                            "Hello",
                            (String) userMap.get("UserID"));
                    userList.add(user);
                }
                chatsAdapter.notifyDataSetChanged();
            }
        });

        chatsAdapter = new DirectMessagingMenuViewAdapter(this, userList, this);
        RecyclerView chatsRecyclerView = findViewById(R.id.recyclerViewChats);
        chatsRecyclerView.setAdapter(chatsAdapter);
        chatsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        chatsRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

    }

    @Override
    public void onItemClick(int position) {
        Intent intent = new Intent(DirectMessagingMenu.this, MessagingChatActivity.class);
        startActivity(intent);
    }
}