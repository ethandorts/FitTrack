package com.example.fittrack;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.widget.ViewPager2;

import com.anychart.AnyChart;
import com.anychart.AnyChartView;
import com.anychart.chart.common.dataentry.DataEntry;
import com.anychart.chart.common.dataentry.ValueDataEntry;
import com.anychart.charts.Cartesian;
import com.anychart.core.cartesian.series.Column;
import com.anychart.enums.Anchor;
import com.anychart.enums.HoverMode;
import com.anychart.enums.Position;
import com.anychart.enums.TooltipPositionMode;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class GamificationGraph extends AppCompatActivity {
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private String GroupID;
    private GamificationUtil gamificationUtil = new GamificationUtil();
    private ViewPager2 gamificationPager;
    private TabLayout gamificationTabs;
    private AutoCompleteTextView graphMetricSpinner;
    private RadioButton btnWeek, btnMonth;
    private GroupsDatabaseUtil groupsUtil = new GroupsDatabaseUtil(db);
    private RecyclerView leaderboardRecyclerView;
    private ArrayList<LeaderboardModel> leaderboardStats = new ArrayList<>();
    private TextView txtGraphName;
    private ImageButton btnHead2Head, btnPoints;
    private String ActivityType;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gamification_graph);

        Intent intent = getIntent();
        GroupID = intent.getStringExtra("GroupID");
        ActivityType = intent.getStringExtra("ActivityType");
        System.out.println("GroupID: " + GroupID);

        gamificationPager = findViewById(R.id.gamificationPager);
        gamificationTabs = findViewById(R.id.gamTabLayout);

        graphMetricSpinner = findViewById(R.id.graph_selector);
        btnWeek = findViewById(R.id.btnWeek);
        btnMonth = findViewById(R.id.btnMonth);
        btnHead2Head = findViewById(R.id.btnHead2Head);
        btnPoints = findViewById(R.id.btnPoints);

//        txtGraphName = findViewById(R.id.txtStatsTitle);
//        leaderboardRecyclerView = findViewById(R.id.leaderboard);
//        txtGraphName.setText("Distance Ran This Week");
        btnWeek.setChecked(true);

        String[] graphOptions = {"Distance", "Activity Frequency", "1 KM", "5 KM", "10 KM"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_dropdown_item_1line,
                graphOptions
        );
        graphMetricSpinner.setAdapter(adapter);
        graphMetricSpinner.setText("Distance", false);

        graphMetricSpinner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                graphMetricSpinner.showDropDown();
            }
        });

        loadGamificationFragments();

        graphMetricSpinner.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                loadGamificationFragments();
            }
        });

        btnWeek.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadGamificationFragments();
            }
        });

        btnMonth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadGamificationFragments();
            }
        });

        btnHead2Head.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(GamificationGraph.this, Head2HeadActivity.class);
                intent.putExtra("GroupID", GroupID);
                intent.putExtra("ActivityType", ActivityType);
                startActivity(intent);
            }
        });

        btnPoints.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(GamificationGraph.this, FitnessPointsActivity.class);
                intent.putExtra("GroupID", GroupID);
                intent.putExtra("activityType", ActivityType);
                startActivity(intent);
            }
        });


//        graphMetricSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
//                gamAdapter.addFragment(new GroupActivitiesFragment(GroupID));
//                gamAdapter.addFragment(new GroupPostsFragment(GroupID));
//                gamificationPager.setAdapter(gamAdapter);
//                updateLeaderBoardChart(graphMetricSpinner, btnWeek);
//            }
//            @Override
//            public void onNothingSelected(AdapterView<?> adapterView) {
//
//            }
//        });
//
//        btnWeek.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                updateLeaderBoardChart(graphMetricSpinner, btnWeek);
//            }
//        });
//
//        btnMonth.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                updateLeaderBoardChart(graphMetricSpinner, btnWeek);
//            }
//        });
    }

    private void loadGamificationFragments() {
        GroupActivitiesFragmentStateAdapter gamAdapter = new GroupActivitiesFragmentStateAdapter(this);
        gamAdapter.addFragment(new LeaderboardStatsFragment(graphMetricSpinner.getText().toString(),
                btnWeek.isChecked(),
                GroupID,
                ActivityType));
        gamAdapter.addFragment(new GraphFragment(graphMetricSpinner.getText().toString(),
                btnWeek.isChecked(),
                GroupID,
                ActivityType)
        );

        gamificationPager.setAdapter(gamAdapter);

        new TabLayoutMediator(gamificationTabs, gamificationPager,
                new TabLayoutMediator.TabConfigurationStrategy() {
                    @Override
                    public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                        switch (position) {
                            case 0:
                                tab.setText("Leaderboards");
                                break;
                            case 1:
                                tab.setText("Visual Stats");
                                break;
                        }
                    }
                }).attach();
    }

//    private void loadByWeekFragment(String GroupID) {
//        txtGraphName.setText(" Top Distance Ran This Week");
//        leaderboardStats.clear();
//        groupsUtil.retrieveUsersinGroup(GroupID, new GroupsDatabaseUtil.UsersinGroupsCallback() {
//            @Override
//            public void onCallback(ArrayList<String> runners) {
//                ArrayList<LeaderboardModel> leaderboardStats = new ArrayList<>();
//                int runnersSize = runners.size();
//                int[] totalCallbacks = {0};
//
//                for(String runner : runners) {
//                    System.out.println(runner);
//                    gamificationUtil.calculateUserDistancePerWeek(runner, new GamificationUtil.UserDistancePerMonthCallback() {
//                        @Override
//                        public void onCallback(double distancePerMonth) {
//                            leaderboardStats.add(new LeaderboardModel(runner, distancePerMonth));
//                            totalCallbacks[0]++;
//
//                            if(totalCallbacks[0] == runnersSize) {
//                                LeaderboardRecyclerAdapter adapter = new LeaderboardRecyclerAdapter(getApplicationContext(), leaderboardStats);
//                                leaderboardRecyclerView.setAdapter(adapter);
//                                adapter.notifyDataSetChanged();
//                                leaderboardRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
//                                leaderboardRecyclerView.addItemDecoration(new DividerItemDecoration(getApplicationContext(), DividerItemDecoration.VERTICAL));
//                            }
//                        }
//                    });
//                }
//            }
//        });
//
//        Fragment currentFragment = getSupportFragmentManager().findFragmentByTag("GraphFragment");
//        if (currentFragment != null) {
//            getSupportFragmentManager().beginTransaction()
//                    .remove(currentFragment)
//                    .commit();
//        }
//
//        Fragment graphFragment = new GraphFragment(true, GroupID);
//        getSupportFragmentManager()
//                .beginTransaction()
//                .replace(R.id.GraphFrame, graphFragment, "GraphFragment")
//                .commit();
//    }
//
//    private void loadByMonthFragment(String GroupID) {
//        txtGraphName.setText("Top Distance Ran This Month");
//        leaderboardStats.clear();
//        groupsUtil.retrieveUsersinGroup(GroupID, new GroupsDatabaseUtil.UsersinGroupsCallback() {
//            @Override
//            public void onCallback(ArrayList<String> runners) {
//                ArrayList<LeaderboardModel> leaderboardStats = new ArrayList<>();
//                int runnersSize = runners.size();
//                int[] totalCallbacks = {0};
//
//                for(String runner : runners) {
//                    System.out.println(runner);
//                    gamificationUtil.calculateUserDistancePerMonth(runner, new GamificationUtil.UserDistancePerMonthCallback() {
//                        @Override
//                        public void onCallback(double distancePerMonth) {
//                            leaderboardStats.add(new LeaderboardModel(runner, distancePerMonth));
//                            totalCallbacks[0]++;
//
//                            if(totalCallbacks[0] == runnersSize) {
//                                LeaderboardRecyclerAdapter adapter = new LeaderboardRecyclerAdapter(getApplicationContext(), leaderboardStats);
//                                leaderboardRecyclerView.setAdapter(adapter);
//                                adapter.notifyDataSetChanged();
//                                leaderboardRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
//                                leaderboardRecyclerView.addItemDecoration(new DividerItemDecoration(getApplicationContext(), DividerItemDecoration.VERTICAL));
//                            }
//                        }
//                    });
//                }
//            }
//        });
//
//        Fragment currentFragment = getSupportFragmentManager().findFragmentByTag("GraphFragment");
//        if (currentFragment != null) {
//            getSupportFragmentManager().beginTransaction()
//                    .remove(currentFragment)
//                    .commit();
//        }
//
//        Fragment graphFragmentMonth = new GraphFragment(false, GroupID);
//        getSupportFragmentManager()
//                .beginTransaction()
//                .replace(R.id.GraphFrame, graphFragmentMonth, "GraphFragment")
//                .commit();
//    }

//    public void updateLeaderBoardChart(Spinner metric, RadioButton isWeek) {
//        String selectedMetric = metric.getSelectedItem().toString();
//        boolean weekSelected = isWeek.isChecked();
//
//        switch (selectedMetric) {
//            case "Most Distance":
//                if (weekSelected) {
//                    loadByWeekFragment(GroupID);
//                } else {
//                    loadByMonthFragment(GroupID);
//                }
//                findViewById(R.id.distanceTypeGroup).setVisibility(View.GONE);
//                break;
//        }
//    }

}