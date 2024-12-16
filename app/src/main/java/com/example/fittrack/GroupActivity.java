package com.example.fittrack;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class GroupActivity extends AppCompatActivity {
    private ActivitiesRecyclerViewAdapter groupActivitiesAdapter;
    private GroupActivitiesViewModel groupActivityViewModel;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private GroupsDatabaseUtil databaseUtil = new GroupsDatabaseUtil(db);
    private ProgressBar loadingActivities;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerViewGroups);
        loadingActivities = findViewById(R.id.groupsProgressBar);
        groupActivitiesAdapter = new ActivitiesRecyclerViewAdapter(this);
        recyclerView.setAdapter(groupActivitiesAdapter);
        groupActivityViewModel = new ViewModelProvider(this).get(GroupActivitiesViewModel.class);
        groupActivityViewModel.getGroupActivities().observe(this, new Observer<ArrayList<ActivityModel>>() {
            @Override
            public void onChanged(ArrayList<ActivityModel> activityModels) {
                groupActivitiesAdapter.updateAdapter(activityModels);
            }
        });
        LinearLayoutManager layout = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layout);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

        groupActivityViewModel.getIsLoading().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                loadingActivities.setVisibility(View.GONE);
            }
        });

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if(layout != null) {
                    int onScreen = layout.getChildCount();
                    int totalItems = layout.getItemCount();
                    int firstItem = layout.findFirstVisibleItemPosition();

                    if(!groupActivityViewModel.getIsLoading().getValue() && !groupActivityViewModel.getIsEndofArray().getValue()) {
                        if((onScreen + firstItem) >= totalItems - 1 && firstItem >= 0) {
                            loadingActivities.setVisibility(View.VISIBLE);
                            groupActivityViewModel.loadGroupActivities();
                        }
                    }
                }
            }
        });
    }
}