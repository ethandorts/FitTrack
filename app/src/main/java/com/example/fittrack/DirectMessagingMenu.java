package com.example.fittrack;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class DirectMessagingMenu extends AppCompatActivity implements RecyclerViewInterface {
    private FirebaseUser mAuth = FirebaseAuth.getInstance().getCurrentUser();
    private String UserID;
    private String GroupID;
    private DirectMessagingMenuViewAdapter chatsAdapter;
    private ArrayList<UserModel> userList = new ArrayList<UserModel>();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseDatabaseHelper DatabaseUtil = new FirebaseDatabaseHelper(db);
    private UserViewModel userViewModel;
    private RecyclerView chatsRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_direct_messaging_menu);

        chatsAdapter = new DirectMessagingMenuViewAdapter(this, this);
        chatsRecyclerView = findViewById(R.id.recyclerViewChats);
        chatsRecyclerView.setAdapter(chatsAdapter);
        chatsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        chatsRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

        Intent intent = getIntent();
        GroupID = intent.getStringExtra("GroupID");

        userViewModel = new ViewModelProvider(this, new UserViewModelFactory(GroupID)).get(UserViewModel.class);
        userViewModel.getChatUsers().observe(this, new Observer<ArrayList<UserModel>>() {
            @Override
            public void onChanged(ArrayList<UserModel> userModels) {
                chatsAdapter.updateUsers(userModels);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Refresh chat users when returning to this activity
        if (userViewModel != null) {
            userViewModel.refreshChatUsers();
        }
    }

    @Override
    public void onItemClick(int position) {
        Intent intent = new Intent(DirectMessagingMenu.this, MessagingChatActivity.class);
        startActivity(intent);
    }
}
