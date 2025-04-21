package com.example.fittrack;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class LikesRecyclerActivity extends AppCompatActivity {
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CommentUtil commentUtil = new CommentUtil(db);
    private FirebaseDatabaseHelper userUtil = new FirebaseDatabaseHelper(db);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_likes_recycler);

        RecyclerView likesRecyclerView = findViewById(R.id.likesRecyclerView);
        ArrayList<LikeModel> likes = new ArrayList<>();

        Intent intent = getIntent();
        String ActivityID = intent.getStringExtra("ActivityID");

        commentUtil.retrieveLikes(ActivityID, new CommentUtil.LikesListCallback() {
            @Override
            public void onCallback(List<String> likesList) {
                for(String like : likesList) {
                    userUtil.retrieveUserName(like, new FirebaseDatabaseHelper.FirestoreUserNameCallback() {
                        @Override
                        public void onCallback(String FullName, long weight, long height, long activityFrequency, long dailyCalorieGoal, String level, String fitnessGoal) {
                            userUtil.retrieveProfilePicture(like + ".jpeg", new FirebaseDatabaseHelper.ProfilePictureCallback() {
                                @Override
                                public void onCallback(Uri PicturePath) {
                                    likes.add(new LikeModel(FullName, 0, PicturePath));
                                    LikesRecyclerAdapter adapter = new LikesRecyclerAdapter(getApplicationContext(), likes);
                                    likesRecyclerView.setAdapter(adapter);
                                    likesRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                                }
                            });
                        }
                    });
                }
            }
        });


        LikesRecyclerAdapter adapter = new LikesRecyclerAdapter(this, likes);
    }
}