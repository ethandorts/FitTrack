package com.example.fittrack;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class GoalProgressReportActivity extends AppCompatActivity {
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private GoalsUtil goalsUtil = new GoalsUtil(db);
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private String UserID = mAuth.getUid();
    private TextView txtGoalDescription, txtTarget, txtProgress, txtDeadline, txtGoalType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_goal_progress_report);

        txtGoalType = findViewById(R.id.txtGoalType);
        txtGoalDescription = findViewById(R.id.txtGoalDescription);
        txtTarget = findViewById(R.id.txtTarget);
        txtProgress = findViewById(R.id.txtProgress);
        txtDeadline = findViewById(R.id.txtDeadline);

        Intent intent = getIntent();
        String GoalID = intent.getStringExtra("GoalID");

        goalsUtil.retrieveGoalSpecificDescription(UserID, GoalID, new GoalsUtil.SpecificGoalCallback() {
            @Override
            public void onCallback(double targetDistance, int targetTime, String status, String goalType, int currentProgress, Timestamp startDate, Timestamp endDate, String description) {
                txtGoalType.setText("🛣️ Distance Goal");
                txtGoalDescription.setText(description);
                txtTarget.setText(String.format("\uD83C\uDFAF Target: %.2f KM", targetDistance / 1000));
                txtDeadline.setText("⏳ Deadline: " + ConversionUtil.AltTimestamptoString(endDate));
            }
        });
    }



}