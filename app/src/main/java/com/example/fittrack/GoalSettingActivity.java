package com.example.fittrack;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

public class GoalSettingActivity extends AppCompatActivity {
    private RecyclerView goalsRecyclerView;
    private ImageButton btnAddGoal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_goal_setting);

        goalsRecyclerView = findViewById(R.id.goalsRecyclerView);
        btnAddGoal = findViewById(R.id.imgAddGoal);

        btnAddGoal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(GoalSettingActivity.this, CreateGoalActivity.class);
                startActivity(intent);
            }
        });
    }
}