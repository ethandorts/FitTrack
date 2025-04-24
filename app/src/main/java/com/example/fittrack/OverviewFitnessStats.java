package com.example.fittrack;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;

import androidx.appcompat.widget.Toolbar;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.viewpager2.widget.ViewPager2;


import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class OverviewFitnessStats extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_overview_fitness_stats);

        Toolbar toolbar = findViewById(R.id.ActivityOverviewToolbar);

        Intent intent = getIntent();
        String ActivityID = intent.getStringExtra("ActivityID");
        TabLayout tabs = findViewById(R.id.activityTabLayout);
        ViewPager2 pager = findViewById(R.id.viewPager3);
        OverviewActivityFragmentStateAdapter adapter = new OverviewActivityFragmentStateAdapter(this);
        adapter.addFragment(new ActivityOverviewFragment(ActivityID));
        adapter.addFragment(new ActivityStatisticsFragment(ActivityID));
        adapter.addFragment(new ActivitySplitsFragment(ActivityID));
        adapter.addFragment(new ActivityGraphs(ActivityID));
        pager.setAdapter(adapter);

        new TabLayoutMediator(tabs, pager,
                new TabLayoutMediator.TabConfigurationStrategy() {
                    @Override
                    public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                        switch (position) {
                            case 0:
                                tab.setText("Overview");
                                break;
                            case 1:
                                tab.setText("Statistics");
                                break;
                            case 2:
                                tab.setText("Splits");
                                break;
                            case 3:
                                tab.setText("Graphs");
                                break;
                        }
                    }
                }).attach();
    }
}