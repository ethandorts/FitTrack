package com.example.fittrack;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class GamificationGraph extends AppCompatActivity {
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private String GroupID;
    private GamificationUtil gamificationUtil = new GamificationUtil();
    private GroupsDatabaseUtil groupsUtil = new GroupsDatabaseUtil(db);
    private RecyclerView leaderboardRecyclerView;
    private ArrayList<LeaderboardModel> leaderboardStats = new ArrayList<>();
    private TextView txtGraphName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gamification_graph);

        Intent intent = getIntent();
        GroupID = intent.getStringExtra("GroupID");

        txtGraphName = findViewById(R.id.txtStatsTitle);
        Spinner graphMetricSpinner = findViewById(R.id.graph_selector);
        leaderboardRecyclerView = findViewById(R.id.leaderboard);
        txtGraphName.setText("Distance Ran This Week");

        ArrayAdapter<CharSequence> adapter = ArrayAdapter
                .createFromResource(this, R.array.graph_metric, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(androidx.appcompat.R.layout.support_simple_spinner_dropdown_item);
        graphMetricSpinner.setAdapter(adapter);

        graphMetricSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String metric = adapterView.getItemAtPosition(i).toString();
                System.out.println("Metric: " + metric);
                if(metric.equals("By Week")) {
                    loadByWeekFragment(GroupID);
                } else if(metric.equals("By Month")) {
                    loadByMonthFragment(GroupID);
                } else {
                    loadByWeekFragment(GroupID);
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void loadByWeekFragment(String GroupID) {
        txtGraphName.setText(" Top Distance Ran This Week");
        leaderboardStats.clear();
        groupsUtil.retrieveUsersinGroup(GroupID, new GroupsDatabaseUtil.UsersinGroupsCallback() {
            @Override
            public void onCallback(ArrayList<String> runners) {
                ArrayList<LeaderboardModel> leaderboardStats = new ArrayList<>();
                int runnersSize = runners.size();
                int[] totalCallbacks = {0};

                for(String runner : runners) {
                    System.out.println(runner);
                    gamificationUtil.calculateUserDistancePerWeek(runner, new GamificationUtil.UserDistancePerMonthCallback() {
                        @Override
                        public void onCallback(double distancePerMonth) {
                            leaderboardStats.add(new LeaderboardModel(runner, distancePerMonth));
                            totalCallbacks[0]++;

                            if(totalCallbacks[0] == runnersSize) {
                                LeaderboardRecyclerAdapter adapter = new LeaderboardRecyclerAdapter(getApplicationContext(), leaderboardStats);
                                leaderboardRecyclerView.setAdapter(adapter);
                                adapter.notifyDataSetChanged();
                                leaderboardRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                                leaderboardRecyclerView.addItemDecoration(new DividerItemDecoration(getApplicationContext(), DividerItemDecoration.VERTICAL));
                            }
                        }
                    });
                }
            }
        });

        Fragment currentFragment = getSupportFragmentManager().findFragmentByTag("GraphFragment");
        if (currentFragment != null) {
            getSupportFragmentManager().beginTransaction()
                    .remove(currentFragment)
                    .commit();
        }

        Fragment graphFragment = new GraphFragment(true, GroupID);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.GraphFrame, graphFragment, "GraphFragment")
                .commit();
    }

    private void loadByMonthFragment(String GroupID) {
        txtGraphName.setText("Top Distance Ran This Month");
        leaderboardStats.clear();
        groupsUtil.retrieveUsersinGroup(GroupID, new GroupsDatabaseUtil.UsersinGroupsCallback() {
            @Override
            public void onCallback(ArrayList<String> runners) {
                ArrayList<LeaderboardModel> leaderboardStats = new ArrayList<>();
                int runnersSize = runners.size();
                int[] totalCallbacks = {0};

                for(String runner : runners) {
                    System.out.println(runner);
                    gamificationUtil.calculateUserDistancePerMonth(runner, new GamificationUtil.UserDistancePerMonthCallback() {
                        @Override
                        public void onCallback(double distancePerMonth) {
                            leaderboardStats.add(new LeaderboardModel(runner, distancePerMonth));
                            totalCallbacks[0]++;

                            if(totalCallbacks[0] == runnersSize) {
                                LeaderboardRecyclerAdapter adapter = new LeaderboardRecyclerAdapter(getApplicationContext(), leaderboardStats);
                                leaderboardRecyclerView.setAdapter(adapter);
                                adapter.notifyDataSetChanged();
                                leaderboardRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                                leaderboardRecyclerView.addItemDecoration(new DividerItemDecoration(getApplicationContext(), DividerItemDecoration.VERTICAL));
                            }
                        }
                    });
                }
            }
        });

        Fragment currentFragment = getSupportFragmentManager().findFragmentByTag("GraphFragment");
        if (currentFragment != null) {
            getSupportFragmentManager().beginTransaction()
                    .remove(currentFragment)
                    .commit();
        }

        Fragment graphFragmentMonth = new GraphFragment(false, GroupID);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.GraphFrame, graphFragmentMonth, "GraphFragment")
                .commit();
    }
}