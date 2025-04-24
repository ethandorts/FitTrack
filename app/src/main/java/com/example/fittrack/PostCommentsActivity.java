package com.example.fittrack;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class PostCommentsActivity extends AppCompatActivity {
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    String UserID = mAuth.getUid();
    PostCommentsRecyclerView postCommentsRecyclerView;
    GroupsDatabaseUtil groupsUtil = new GroupsDatabaseUtil(db);
    CommentUtil commentUtil = new CommentUtil(db);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_comments);

        EditText editMessage = findViewById(R.id.editCommentPost);
        ImageButton btnSendMessage = findViewById(R.id.btnSendPostComment);

        Intent intent = getIntent();
        String GroupID = intent.getStringExtra("GroupID");
        String PostID = intent.getStringExtra("PostID");

        RecyclerView postCommentsRecycler = findViewById(R.id.recyclerPostComments);

        Query query = db.collection("Groups")
                .document(GroupID)
                .collection("Posts")
                .document(PostID)
                .collection("Comments")
                .orderBy("date", Query.Direction.DESCENDING);

        FirestoreRecyclerOptions<CommentModel> options =
                new FirestoreRecyclerOptions.Builder<CommentModel>()
                        .setQuery(query, CommentModel.class)
                        .build();

        postCommentsRecyclerView = new PostCommentsRecyclerView(options, getApplicationContext(), GroupID, PostID);
        postCommentsRecycler.setAdapter(postCommentsRecyclerView);
        postCommentsRecycler.setLayoutManager(new LinearLayoutManager(this));

        btnSendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String message = editMessage.getText().toString();
                CommentModel comment = new CommentModel(UserID, message, Timestamp.now());
                commentUtil.savePostComment(GroupID, PostID, comment);
                editMessage.setText("");
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        postCommentsRecyclerView.startListening();
    }

    @Override
    protected void onResume() {
        super.onResume();
        postCommentsRecyclerView.notifyDataSetChanged();
    }

    @Override
    protected void onStop() {
        super.onStop();
        postCommentsRecyclerView.stopListening();
    }
}