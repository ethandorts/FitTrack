package com.example.fittrack;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.firestore.FirebaseFirestore;

public class GroupActivity extends AppCompatActivity {
    TextView txtGroupName;
    ViewPager2 pager;
    private ActivitiesRecyclerViewAdapter groupActivitiesAdapter;
    private GroupActivitiesViewModel groupActivityViewModel;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private GroupsDatabaseUtil databaseUtil = new GroupsDatabaseUtil(db);
    private ProgressBar loadingActivities;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group);

        txtGroupName = findViewById(R.id.txtGroupName);
        TabLayout tabs = findViewById(R.id.activityTabLayout);
        Intent intent = getIntent();
        String GroupID = intent.getStringExtra("GroupID");
        String groupName = intent.getStringExtra("GroupName");
        txtGroupName.setText(groupName);

        pager = findViewById(R.id.viewPager3);
        GroupActivitiesFragmentStateAdapter adapter = new GroupActivitiesFragmentStateAdapter(this);
        adapter.addFragment(new GroupActivitiesFragment());
        adapter.addFragment(new GroupPostsFragment());
        adapter.addFragment(new GroupMeetupsFragment());
        pager.setAdapter(adapter);

        new TabLayoutMediator(tabs, pager,
                new TabLayoutMediator.TabConfigurationStrategy() {
                    @Override
                    public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                        switch (position) {
                            case 0:
                                tab.setText("Activities");
                                break;
                            case 1:
                                tab.setText("Posts");
                                break;
                            case 2:
                                tab.setText("Meetup Requests");
                                break;
                        }
                    }
                }).attach();
    }
}