package com.example.fittrack;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.Group;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class FitnessPointsActivity extends AppCompatActivity {
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private GroupsDatabaseUtil groupsUtil = new GroupsDatabaseUtil(db);
    private FirebaseDatabaseHelper userUtil = new FirebaseDatabaseHelper(db);
    private TableLayout table;
    private TextView leaderboardTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fitness_points);

        Intent intent = getIntent();
        String GroupID = intent.getStringExtra("GroupID");
        String activityType = intent.getStringExtra("activityType");

        table = findViewById(R.id.pointsTable);
        getFitnessPoints(GroupID, activityType);
    }

    public int calculateFitnessPoints(double activityFrequency, double distanceInMeters, double durationInSeconds) {
        double distanceKm = distanceInMeters / 1000.0;
        double durationMinutes = durationInSeconds / 60.0;

        double points = (activityFrequency * 10) + (distanceKm * 1) + (durationMinutes * 0.2);
        return (int) Math.round(points);
    }


    public void getFitnessPoints(String GroupID, String activityType) {
        groupsUtil.retrieveUsersinGroup(GroupID, new GroupsDatabaseUtil.UsersinGroupsCallback() {
            @Override
            public void onCallback(final ArrayList<String> runners) {
                final List<String> userIds = new ArrayList<>(runners);
                final List<Task<DocumentSnapshot>> tasks = new ArrayList<>();

                for (String runner : userIds) {
                    Task<DocumentSnapshot> t = db.collection("Users")
                            .document(runner)
                            .collection("Statistics")
                            .document(activityType + "_lastMonth")
                            .get();
                    tasks.add(t);
                }

                Tasks.whenAllSuccess(tasks)
                        .addOnSuccessListener(new OnSuccessListener<List<Object>>() {
                            @Override
                            public void onSuccess(List<Object> documents) {
                                ArrayList<LeaderboardModel> stats = new ArrayList<>();
                                for (int i = 0; i < documents.size(); i++) {
                                    DocumentSnapshot snapshot = (DocumentSnapshot) documents.get(i);
                                    Double frequency = snapshot.getDouble("ActivityFrequency");
                                    Double distance = snapshot.getDouble("Distance");
                                    Double duration = snapshot.getDouble("Duration");

                                    if (frequency == null) frequency = 0.0;
                                    if (distance == null) distance = 0.0;
                                    if (duration == null) duration = 0.0;

                                    int points = calculateFitnessPoints(frequency, distance, duration);
                                    stats.add(new LeaderboardModel(userIds.get(i), points, null));
                                }

                                Collections.sort(stats, new Comparator<LeaderboardModel>() {
                                    @Override
                                    public int compare(LeaderboardModel a, LeaderboardModel b) {
                                        return Double.compare(b.getDistance(), a.getDistance());
                                    }
                                });
                                updateLeaderboard(stats);
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.e("Leaderboard", "Error loading stats" + e.getMessage());
                            }
                        });
            }
        });
    }

    private void updateLeaderboard(ArrayList<LeaderboardModel> leaderboardStats) {
        table.removeViews(1, Math.max(0, table.getChildCount() - 1));

        int rank = 1;
        for (final LeaderboardModel stats : leaderboardStats) {
            TableRow row = new TableRow(this);

            final TextView rankView = new TextView(this);
            final TextView nameView = new TextView(this);
            final TextView valueView = new TextView(this);

            rankView.setText(String.valueOf(rank++));
            rankView.setGravity(Gravity.CENTER);
            nameView.setGravity(Gravity.CENTER);
            valueView.setGravity(Gravity.CENTER);

            valueView.setText(String.valueOf((int) stats.getDistance()));

            userUtil.retrieveChatName(stats.getUsername(), new FirebaseDatabaseHelper.ChatUserCallback() {
                @Override
                public void onCallback(String chatName) {
                    nameView.setText(chatName != null ? chatName : stats.getUsername());
                }
            });

            float textSize = 18f;
            int padding = 12;

            rankView.setTextSize(textSize);
            nameView.setTextSize(textSize);
            valueView.setTextSize(textSize);

            rankView.setPadding(padding, padding, padding, padding);
            nameView.setPadding(padding, padding, padding, padding);
            valueView.setPadding(padding, padding, padding, padding);

            row.addView(rankView);
            row.addView(nameView);
            row.addView(valueView);

            table.addView(row);
        }
    }
}
