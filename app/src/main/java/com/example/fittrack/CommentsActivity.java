package com.example.fittrack;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class CommentsActivity extends AppCompatActivity {
    private EditText editComment;
    private ImageButton btnSendComment;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private String currentUser = mAuth.getCurrentUser().getUid();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CommentUtil commentUtil = new CommentUtil(db);
    private CommentsRecyclerViewAdapter commentsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);

        editComment = findViewById(R.id.editComment);
        btnSendComment = findViewById(R.id.btnSendComment);

        Intent intent = getIntent();
        String ActivityID = intent.getStringExtra("ActivityID");

        RecyclerView commentsRecyclerView = findViewById(R.id.commentsRecyclerView);

        Query query = db.collection("Activities")
                .document(ActivityID)
                .collection("Comments")
                .orderBy("date", Query.Direction.DESCENDING);

        FirestoreRecyclerOptions<CommentModel> options =
                new FirestoreRecyclerOptions.Builder<CommentModel>()
                        .setQuery(query, CommentModel.class)
                        .build();

        commentsAdapter = new CommentsRecyclerViewAdapter(options, this);
        commentsRecyclerView.setAdapter(commentsAdapter);
        commentsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        commentsRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

        btnSendComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String comment = editComment.getText().toString().trim();
                Timestamp timeNow = Timestamp.now();
                CommentModel commentModel = new CommentModel(currentUser, comment, timeNow);
                commentUtil.saveComment(ActivityID, commentModel);
                editComment.setText("");
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        commentsAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        commentsAdapter.stopListening();
    }
}