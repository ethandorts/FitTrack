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
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.auth.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DirectMessagingMenu extends AppCompatActivity implements RecyclerViewInterface {
    private FirebaseUser mAuth = FirebaseAuth.getInstance().getCurrentUser();
    private String UserID;
    private String GroupID;
    private DirectMessagingMenuViewAdapter chatsAdapter;
    private ArrayList<UserModel> userList = new ArrayList<UserModel>();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseDatabaseHelper DatabaseUtil = new FirebaseDatabaseHelper(db);
    private UserViewModel userViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_direct_messaging_menu);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Intent intent = getIntent();
        GroupID = intent.getStringExtra("GroupID");
        System.out.println("GroupID: " + GroupID);
//        userList.clear();
//        String UserID = mAuth.getUid();

//        DatabaseUtil.retrieveAllUsers(UserID, new FirebaseDatabaseHelper.FirestoreAllUsersCallback() {
//            @Override
//            public void onCallback(List<Map<String, Object>> data) {
//                for(Map<String, Object> userMap : data) {
//                    // data got - formatting users
//                    String documentID = DirectMessagingUtil.getDocumentID(UserID, (String) userMap.get("UserID"));
//                    db.collection("DM").document(documentID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//                        @Override
//                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//                            String lastMessage ="";
//                            DocumentSnapshot retrievedSnapshot = task.getResult();
//                            lastMessage = (String) retrievedSnapshot.get("LastMessage");
//                            UserModel user = new UserModel((String) userMap.get("FirstName") + " " + userMap.get("Surname"),
//                                    lastMessage,
//                                    (String) userMap.get("UserID"));
//                            userList.add(user);
//                            chatsAdapter.notifyDataSetChanged();
//                        }
//                    });
//                }
//            }
//        });

        chatsAdapter = new DirectMessagingMenuViewAdapter(this, this);
        RecyclerView chatsRecyclerView = findViewById(R.id.recyclerViewChats);
        chatsRecyclerView.setAdapter(chatsAdapter);
        userViewModel = new ViewModelProvider(this, new UserViewModelFactory(GroupID)).get(UserViewModel.class);
        userViewModel.getChatUsers().observe(this, new Observer<ArrayList<UserModel>>() {
            @Override
            public void onChanged(ArrayList<UserModel> userModels) {
                chatsAdapter.updateUsers(userModels);
            }
        });
        chatsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        chatsRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
    }

    @Override
    public void onItemClick(int position) {
        Intent intent = new Intent(DirectMessagingMenu.this, MessagingChatActivity.class);
        startActivity(intent);
    }
}