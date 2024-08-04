package com.example.fittrack;

import static android.content.ContentValues.TAG;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
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

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.auth.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HomeActivity extends AppCompatActivity {

    ImageView addFriend;
    TextView UserName;
    FirebaseFirestore db;
    FirebaseUser mAuth;
    String UserID;
    ProgressBar loadingActivities;

    ArrayList<ActivityModel> UserActivities = new ArrayList<ActivityModel>();
    private String FullName;
    private ActivitiesRecyclerViewAdapter activitiesAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        UserName = findViewById(R.id.txtUserName);
        loadingActivities = findViewById(R.id.LoadingActivitiesProgressBar);

        db = FirebaseFirestore.getInstance();

        mAuth = FirebaseAuth.getInstance().getCurrentUser();
        UserID = mAuth.getUid();

        RecyclerView activitiesRecyclerView = findViewById(R.id.activitiesRecyclerView);
        activitiesAdapter = new ActivitiesRecyclerViewAdapter(this, UserActivities);
        activitiesRecyclerView.setAdapter(activitiesAdapter);
        activitiesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        activitiesRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                if(menuItem.getItemId() == R.id.home_bottom) {
                    // for future button
                } else if (menuItem.getItemId() == R.id.record_bottom) {
                    Intent intent = new Intent(HomeActivity.this, MainActivity.class);
                    startActivity(intent);
                } else if (menuItem.getItemId() == R.id.settings_bottom) {
                    // for future settings button
                }
                return false;
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        fetchActivitiesData(UserID);
    }

    private String formatRunTime(double timePassed) {
        int hours = (int) (timePassed / 3600);
        int minutes = (int) ((timePassed % 3600) / 60);
        int seconds = (int) (timePassed % 60);
        String formattedRunTime = String.format("%02d:%02d:%02d", hours, minutes, seconds);

        return formattedRunTime;
    }

    private void fetchUserName() {
            DocumentReference documentReference = db.collection("Users").document(UserID);
            documentReference.get()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            Map<String, Object> data = documentSnapshot.getData();
                            FullName = data.get("FirstName") + " " + data.get("Surname");
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    UserName.setText(FullName);
                                }
                            });
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            System.out.println("Couldn't fetch user details");
                            System.out.println(e.getMessage());
                        }
                    });

    }

    private void fetchActivitiesData(String UserID) {
        loadingActivities.setVisibility(View.VISIBLE);
        UserActivities.clear();
        new Thread(() -> {
            fetchUserName();
            if(UserID != null) {
                db.collection("Activities")
                        .whereEqualTo("UserID", UserID)
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        Map<String, Object> ActivitiesRetrieved = document.getData();
                                        String UserUUID = String.valueOf(ActivitiesRetrieved.get("UserID"));
                                        queryForUserFullName(UserUUID, new FullUserNameCallback() {
                                            @Override
                                            public void onCallback(String Username) {
                                                if (FullName != null) {
                                                    // create ActivityModels for each activity query returns
                                                    ActivityModel activityReturned = new ActivityModel(
                                                            String.valueOf(ActivitiesRetrieved.get("type")),
                                                            String.valueOf(ActivitiesRetrieved.get("typeImage")),
                                                            String.valueOf(ActivitiesRetrieved.get("date")),
                                                            String.valueOf(ActivitiesRetrieved.get("distance")),
                                                            formatRunTime(Double.parseDouble(String.valueOf(ActivitiesRetrieved.get("time")))),
                                                            String.valueOf(ActivitiesRetrieved.get("pace")),
                                                            FullName,
                                                            String.valueOf(ActivitiesRetrieved.get("UserImage")),
                                                            (List<Object>) ActivitiesRetrieved.get("activityCoordinates")
                                                    );
                                                    UserActivities.add(activityReturned);
                                                }
                                                System.out.println("UserActivities: " + UserActivities.size());
                                                runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        activitiesAdapter.notifyDataSetChanged();
                                                        loadingActivities.setVisibility(View.GONE);
                                                    }
                                                });
                                            }
                                        });
                                    }
                                } else {
                                    Log.d(TAG, "Retrieval Errors: ", task.getException());
                                }
                            }
                        });
            } else {
                System.out.println("No UserID");
            }
        }).start();
    }

    private void queryForUserFullName(String UserID, FullUserNameCallback callback) {
        DocumentReference documentReference = db.collection("Users").document(UserID);
        documentReference.get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        Map<String, Object> data = documentSnapshot.getData();
                        FullName = data.get("FirstName") + " " + data.get("Surname");
                        callback.onCallback(FullName);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        System.out.println("Couldn't fetch user details");
                        System.out.println(e.getMessage());
                        callback.onCallback(null);
                    }
                });
    }

}