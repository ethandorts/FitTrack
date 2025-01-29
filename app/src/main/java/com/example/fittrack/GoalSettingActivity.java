package com.example.fittrack;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class GoalSettingActivity extends AppCompatActivity {
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private String UserID = mAuth.getUid();
    private ImageButton btnAddGoal;
    private ViewPager2 pager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_goal_setting);

        pager = findViewById(R.id.pagerCompleted);
        TabLayout tabsCompleted = findViewById(R.id.tabLayoutCompleted);
        btnAddGoal = findViewById(R.id.imgAddGoal);

        CompletedFragmentStateAdapter adapter = new CompletedFragmentStateAdapter(this);
        adapter.addFragment(new InProgressGoalsFragment("In Progress"));
        adapter.addFragment(new InProgressGoalsFragment("Completed"));
        pager.setAdapter(adapter);

        new TabLayoutMediator(tabsCompleted, pager,
                new TabLayoutMediator.TabConfigurationStrategy() {
                    @Override
                    public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                        switch(position) {
                            case 0:
                                tab.setText("In Progress");
                                break;
                            case 1:
                                tab.setText("Completed");
                                break;
                        }
                    }
                }).attach();

        btnAddGoal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(GoalSettingActivity.this, CreateGoalActivity.class);
                startActivity(intent);
            }
        });
    }
}