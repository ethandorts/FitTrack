package com.example.fittrack;

import android.content.Intent;
import android.os.Bundle;

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
import java.util.Map;

public class DirectMessagingMenu extends AppCompatActivity implements RecyclerViewInterface {
    private DirectMessagingMenuViewAdapter chatsAdapter;
    private ArrayList<UserModel> userList = new ArrayList<UserModel>();
    DatabaseHelper dbHelper;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_direct_messaging_menu);

        db = FirebaseFirestore.getInstance();
        getUsers();

        RecyclerView chatsRecyclerView = findViewById(R.id.recyclerViewChats);
        chatsAdapter = new DirectMessagingMenuViewAdapter(this, userList, this);
        chatsRecyclerView.setAdapter(chatsAdapter);
        chatsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        chatsRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
    }

    public void getUsers() {
        db.collection("Users")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()) {
                            for(QueryDocumentSnapshot document : task.getResult()) {
                                Map<String, Object> UserMap = document.getData();
                                UserModel user = new UserModel(
                                        UserMap.get("FirstName") + " " + UserMap.get("Surname"),
                                        "Hello"
                                );
                                userList.add(user);
                            }
                            chatsAdapter.notifyDataSetChanged();
                        }
                    }
                });
    }

    @Override
    public void onItemClick(int position) {
        Intent intent = new Intent(DirectMessagingMenu.this, MessagingChatActivity.class);
        //intent.putExtra("Name", )
        startActivity(intent);
    }
}