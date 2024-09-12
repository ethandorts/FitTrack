package com.example.fittrack;

import static android.content.ContentValues.TAG;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.wearable.DataClient;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Wearable;
import com.google.android.gms.wearable.WearableListenerService;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.auth.User;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HomeActivity extends AppCompatActivity {

    ImageView profileImage, addFriend, btnMessageFriends;
    TextView UserName;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseStorage storage = FirebaseStorage.getInstance();
    FirebaseUser mAuth;
    String UserID;
    ProgressBar loadingActivities;
    FirebaseDatabaseHelper DatabaseUtil = new FirebaseDatabaseHelper(db);

    ArrayList<ActivityModel> UserActivities = new ArrayList<ActivityModel>();
    private ActivitiesRecyclerViewAdapter activitiesAdapter;
    private DocumentSnapshot lastVisible = null;
    private boolean isLoading = false;
    private boolean isEndofArray = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        UserName = findViewById(R.id.txtUserName);
        profileImage = findViewById(R.id.profile_image);
        loadingActivities = findViewById(R.id.LoadingActivitiesProgressBar);
        btnMessageFriends = findViewById(R.id.btnMessageFriends);

        mAuth = FirebaseAuth.getInstance().getCurrentUser();
        UserID = mAuth.getUid();

        DatabaseUtil.retrieveUserName(UserID, new FirebaseDatabaseHelper.FirestoreUserNameCallback() {
            @Override
            public void onCallback(String FullName) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        UserName.setText(FullName);
                    }
                });
            }
        });

        DatabaseUtil.retrieveProfilePicture(UserID + ".jpeg", new FirebaseDatabaseHelper.ProfilePictureCallback() {
            @Override
            public void onCallback(Uri PicturePath) {
                Glide.with(HomeActivity.this)
                        .load(PicturePath)
                        .into(profileImage);
            }
        });

        RecyclerView activitiesRecyclerView = findViewById(R.id.activitiesRecyclerView);
        activitiesAdapter = new ActivitiesRecyclerViewAdapter(this, UserActivities);
        activitiesRecyclerView.setAdapter(activitiesAdapter);
        LinearLayoutManager layout = new LinearLayoutManager(this);
        activitiesRecyclerView.setLayoutManager(layout);
        activitiesRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

        activitiesRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if(layout != null) {
                    int onScreen = layout.getChildCount();
                    int totalItems = layout.getItemCount();
                    int firstItem = layout.findFirstVisibleItemPosition();

                    if( !isEndofArray && !isLoading && (onScreen + firstItem) >= totalItems) {
                        loadingActivities.setVisibility(View.VISIBLE);
                        loadUserActivities();
                    }
                }
            }
        });

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                if(menuItem.getItemId() == R.id.home_bottom) {
                    // for future button
                } else if (menuItem.getItemId() == R.id.record_bottom) {
                    Intent intent = new Intent(HomeActivity.this, MainActivity.class);
                    startActivity(intent);
                } else if (menuItem.getItemId() == R.id.group_bottom) {
                    Intent intent = new Intent(HomeActivity.this, GroupsMenu.class);
                    startActivity(intent);
                } else if (menuItem.getItemId() == R.id.settings_bottom) {
                    Intent intent = new Intent(HomeActivity.this, SettingsActivity.class);
                    startActivity(intent);
                }
                return false;
            }
        });

        btnMessageFriends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeActivity.this, DirectMessagingMenu.class);
                startActivity(intent);
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        UserActivities.clear();
        activitiesAdapter.notifyDataSetChanged();
        isEndofArray = false;
        lastVisible = null;
        loadUserActivities();
    }

    protected void onStop() {
        super.onStop();
    }

    private String formatRunTime(double timePassed) {
        int hours = (int) (timePassed / 3600);
        int minutes = (int) ((timePassed % 3600) / 60);
        int seconds = (int) (timePassed % 60);
        String formattedRunTime = String.format("%02d:%02d:%02d", hours, minutes, seconds);

        return formattedRunTime;
    }

    private void loadUserActivities() {
        if( isLoading || isEndofArray) {
            return;
        }

        isLoading = true;
        DatabaseUtil.retrieveUserActivities(UserID, new FirebaseDatabaseHelper.FirestoreActivitiesCallback() {
            @Override
            public void onCallback(List<Map<String, Object>> data, DocumentSnapshot lastItemVisible) {
                for(Map<String, Object> activity : data) {
                    isLoading = false;
                    loadingActivities.setVisibility(View.GONE);

                    if(data == null || data.isEmpty()) {
                        isEndofArray = true;
                        return;
                    }
                    ActivityModel activityInfo = new ActivityModel(
                            String.valueOf(activity.get("type")),
                            String.valueOf(activity.get("typeImage")),
                            (Timestamp) activity.get("date"),
                            String.valueOf(activity.get("distance")),
                            formatRunTime(Double.parseDouble(String.valueOf(activity.get("time")))),
                            String.valueOf(activity.get("pace")),
                            (String) UserName.getText(),
                            String.valueOf(activity.get("UserImage")),
                            (List<Object>) activity.get("activityCoordinates")
                    );
                        UserActivities.add(activityInfo);
                }
                lastVisible = lastItemVisible;
                System.out.println(UserActivities.size());
                activitiesAdapter.notifyDataSetChanged();
            }
        }, lastVisible);
    }
}