package com.example.fittrack;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class Head2HeadActivity extends AppCompatActivity {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseDatabaseHelper userUtil = new FirebaseDatabaseHelper(db);
    private GroupsDatabaseUtil groupsDatabaseUtil = new GroupsDatabaseUtil(db);
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();

    private String UserID;
    private String selectedOpponentId = null;
    private String groupId;
    private String activityType;

    private TextView txtDistanceUser1, txtDistanceUser2, txtActivitiesUser1, txtActivitiesUser2, txt1kmUser1, txt1kmUser2, txt5kmUser1, txt5kmUser2, txt10kmUser1, txt10kmUser2, txtStats;
    private ImageView imageUser1, imageUser2;

    private AutoCompleteTextView spinnerUser;
    private RadioGroup timeFilterGroup;

    private List<String> userIds = new ArrayList<>();
    private List<String> userNames = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_head2_head);

        spinnerUser = findViewById(R.id.spinnerUser);
        timeFilterGroup = findViewById(R.id.head2head_time_filter_group);

        txtDistanceUser1 = findViewById(R.id.txt_distance_user1);
        txtDistanceUser2 = findViewById(R.id.txt_distance_user2);
        txtActivitiesUser1 = findViewById(R.id.txt_activities_user1);
        txtActivitiesUser2 = findViewById(R.id.txt_activities_user2);
        txt1kmUser1 = findViewById(R.id.txt_1km_user1);
        txt1kmUser2 = findViewById(R.id.txt_1km_user2);
        txt5kmUser1 = findViewById(R.id.txt_5km_user1);
        txt5kmUser2 = findViewById(R.id.txt_5km_user2);
        txt10kmUser1 = findViewById(R.id.txt_10km_user1);
        txt10kmUser2 = findViewById(R.id.txt_10km_user2);
        txtStats = findViewById(R.id.txtStatisticsText);

        imageUser1 = findViewById(R.id.imgUser1);
        imageUser2 = findViewById(R.id.imgUser2);

        UserID = mAuth.getUid();

        Intent intent = getIntent();
        activityType = intent.getStringExtra("ActivityType");
        groupId = intent.getStringExtra("GroupID");

        retrieveUsersInGroup(groupId);

        spinnerUser.setOnItemClickListener((parent, view, position, id) -> {
            selectedOpponentId = userIds.get(position);
            clearStatsUI();
            fetchAndCompareStats();
        });

        timeFilterGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (selectedOpponentId != null) {
                clearStatsUI();
                fetchAndCompareStats();
            }
        });
    }

    private void retrieveUsersInGroup(String groupID) {
        groupsDatabaseUtil.retrieveUsersinGroup(groupID, new GroupsDatabaseUtil.UsersinGroupsCallback() {
            @Override
            public void onCallback(ArrayList<String> runners) {
                if (runners != null && !runners.isEmpty()) {
                    final int[] completedCount = {0};

                    for (String userId : runners) {
                        if (userId.equals(UserID)) {
                            completedCount[0]++;
                            continue;
                        }

                        userUtil.retrieveChatName(userId, new FirebaseDatabaseHelper.ChatUserCallback() {
                            @Override
                            public void onCallback(String chatName) {
                                userIds.add(userId);
                                userNames.add(chatName);
                                completedCount[0]++;

                                if (completedCount[0] == runners.size()) {
                                    ArrayAdapter<String> adapter = new ArrayAdapter<>(
                                            Head2HeadActivity.this,
                                            android.R.layout.simple_dropdown_item_1line,
                                            userNames
                                    );
                                    spinnerUser.setAdapter(adapter);

                                    if (!userNames.isEmpty()) {
                                        spinnerUser.setText(userNames.get(0), false);
                                        selectedOpponentId = userIds.get(0);
                                        fetchAndCompareStats();
                                    }

                                    spinnerUser.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            spinnerUser.showDropDown();
                                        }
                                    });
                                }
                            }
                        });
                    }
                } else {
                    Toast.makeText(Head2HeadActivity.this, "No users found in group", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void fetchAndCompareStats() {
        boolean isWeek = timeFilterGroup.getCheckedRadioButtonId() == R.id.radio_this_week;
        String statsDoc = isWeek ? activityType + "_lastWeek" : activityType + "_lastMonth";

        db.collection("Users").document(UserID).collection("Statistics").document(statsDoc)
                .get()
                .addOnSuccessListener(currentUserSnapshot -> {
                    if (currentUserSnapshot.exists()) {
                        db.collection("Users").document(selectedOpponentId).collection("Statistics").document(statsDoc)
                                .get()
                                .addOnSuccessListener(opponentSnapshot -> {
                                    if (opponentSnapshot.exists()) {
                                        updateStatsUI(currentUserSnapshot, opponentSnapshot);
                                    } else {
                                        Toast.makeText(this, "Opponent has no stats.", Toast.LENGTH_SHORT).show();
                                    }
                                })
                                .addOnFailureListener(e -> Toast.makeText(this, "Failed to load opponent stats.", Toast.LENGTH_SHORT).show());
                    } else {
                        Toast.makeText(this, "You have no stats.", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Failed to load your stats.", Toast.LENGTH_SHORT).show());
    }

    private void updateStatsUI(DocumentSnapshot user1, DocumentSnapshot user2) {
        userUtil.retrieveProfilePicture(UserID + ".jpeg", new FirebaseDatabaseHelper.ProfilePictureCallback() {
            @Override
            public void onCallback(Uri uri) {
                if (uri != null) {
                    Glide.with(Head2HeadActivity.this)
                            .load(uri)
                            .circleCrop()
                            .into(imageUser1);
                }
            }
        });

        userUtil.retrieveProfilePicture(selectedOpponentId + ".jpeg", new FirebaseDatabaseHelper.ProfilePictureCallback() {
            @Override
            public void onCallback(Uri uri) {
                if (uri != null) {
                    Glide.with(Head2HeadActivity.this)
                            .load(uri)
                            .circleCrop()
                            .into(imageUser2);
                }
            }
        });

        txtStats.setText(activityType + " Statistics");


        double distance1 = user1.getDouble("Distance") != null ? user1.getDouble("Distance") / 1000.0 : 0;
        double distance2 = user2.getDouble("Distance") != null ? user2.getDouble("Distance") / 1000.0 : 0;
        txtDistanceUser1.setText(String.format("%.2f KM", distance1));
        txtDistanceUser2.setText(String.format("%.2f KM", distance2));

        long activities1 = user1.getLong("ActivityFrequency") != null ? user1.getLong("ActivityFrequency") : 0;
        long activities2 = user2.getLong("ActivityFrequency") != null ? user2.getLong("ActivityFrequency") : 0;
        txtActivitiesUser1.setText(String.valueOf(activities1));
        txtActivitiesUser2.setText(String.valueOf(activities2));

        long time1kUser1 = user1.getLong("1K") != null ? user1.getLong("1K") : -1;
        long time1kUser2 = user2.getLong("1K") != null ? user2.getLong("1K") : -1;
        txt1kmUser1.setText(formatTime(time1kUser1));
        txt1kmUser2.setText(formatTime(time1kUser2));

        long time5kUser1 = user1.getLong("5K") != null ? user1.getLong("5K") : -1;
        long time5kUser2 = user2.getLong("5K") != null ? user2.getLong("5K") : -1;
        txt5kmUser1.setText(formatTime(time5kUser1));
        txt5kmUser2.setText(formatTime(time5kUser2));

        long time10kUser1 = user1.getLong("10K") != null ? user1.getLong("10K") : -1;
        long time10kUser2 = user2.getLong("10K") != null ? user2.getLong("10K") : -1;
        txt10kmUser1.setText(formatTime(time10kUser1));
        txt10kmUser2.setText(formatTime(time10kUser2));
    }

    private String formatTime(long millis) {
        if (millis == -1) return "-";
        long seconds = millis / 1000;
        long minutes = seconds / 60;
        long remainingSeconds = seconds % 60;
        return String.format("%02d:%02d", minutes, remainingSeconds);
    }

    private void clearStatsUI() {
        txtDistanceUser1.setText("-");
        txtDistanceUser2.setText("-");
        txtActivitiesUser1.setText("-");
        txtActivitiesUser2.setText("-");
        txt1kmUser1.setText("-");
        txt1kmUser2.setText("-");
        txt5kmUser1.setText("-");
        txt5kmUser2.setText("-");
        txt10kmUser1.setText("-");
        txt10kmUser2.setText("-");
    }
}
