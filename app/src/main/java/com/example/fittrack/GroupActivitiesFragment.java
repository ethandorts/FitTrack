package com.example.fittrack;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class GroupActivitiesFragment extends Fragment {
    private ActivitiesRecyclerViewAdapter groupActivitiesAdapter;
    private GroupActivitiesViewModel groupActivityViewModel;
    private ProgressBar loadingActivities;
    private boolean isLoading = false;
    private Handler handler = new Handler();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_group_activities, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView recyclerView = view.findViewById(R.id.fragmentGroupActivitiesRecyclerView);
        loadingActivities = view.findViewById(R.id.groupsProgressBar);
        groupActivitiesAdapter = new ActivitiesRecyclerViewAdapter(getActivity());
        recyclerView.setAdapter(groupActivitiesAdapter);
        groupActivityViewModel = new ViewModelProvider(this).get(GroupActivitiesViewModel.class);
        groupActivityViewModel.loadGroupActivities("Sg8JLYf9lpE1akjQRHBv");
        groupActivityViewModel.getGroupActivities().observe(getActivity(), new Observer<ArrayList<ActivityModel>>() {
            @Override
            public void onChanged(ArrayList<ActivityModel> activityModels) {
                groupActivitiesAdapter.updateAdapter(activityModels);
            }
        });
        LinearLayoutManager layout = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layout);
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));

        groupActivityViewModel.getIsLoading().observe(getActivity(), new Observer<Boolean>() {
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
                        if((onScreen + firstItem) >= totalItems - 1) {
                            isLoading = true;
                            loadingActivities.setVisibility(View.VISIBLE);

                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    groupActivityViewModel.loadGroupActivities("Sg8JLYf9lpE1akjQRHBv");
                                    isLoading = false;
                                }
                            }, 3000);
                        }
                    }
                }
            }
        });
    }
}
