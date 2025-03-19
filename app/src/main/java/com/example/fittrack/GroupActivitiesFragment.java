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

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GroupActivitiesFragment extends Fragment {
    private ActivitiesRecyclerViewAdapter groupActivitiesAdapter;
    private GroupActivitiesViewModel groupActivityViewModel;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private GroupsDatabaseUtil groupsUtil = new GroupsDatabaseUtil(db);
    private ProgressBar loadingActivities;
    private String GroupID;
    private boolean isLoading = false;
    private Handler handler = new Handler();
    private List<String> groupRunners = new ArrayList<>();

    public GroupActivitiesFragment(String GroupID) {
        this.GroupID = GroupID;
    }

    public GroupActivitiesFragment() {
    }

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
        loadingActivities.setVisibility(View.VISIBLE);

        Query intialQuery = db.collection("Activities");

        FirestoreRecyclerOptions<ActivityModel> options = new FirestoreRecyclerOptions.Builder<ActivityModel>()
                .setQuery(intialQuery, ActivityModel.class)
                .build();

        groupActivitiesAdapter = new ActivitiesRecyclerViewAdapter(options, getContext());
        recyclerView.setAdapter(groupActivitiesAdapter);

        groupsUtil.retrieveUsersinGroup(GroupID, new GroupsDatabaseUtil.UsersinGroupsCallback() {
            @Override
            public void onCallback(ArrayList<String> runners) {
                Query query = db.collection("Activities")
                .whereIn("UserID", runners)
                .orderBy("date", Query.Direction.DESCENDING);

                FirestoreRecyclerOptions<ActivityModel> options = new FirestoreRecyclerOptions.Builder<ActivityModel>()
                        .setQuery(query, ActivityModel.class)
                        .build();

                groupActivitiesAdapter.updateOptions(options);
                LinearLayoutManager layout = new LinearLayoutManager(getActivity());
                recyclerView.setLayoutManager(layout);
                loadingActivities.setVisibility(View.INVISIBLE);
            }
        });


//        Query query = db.collection("Activities")
//                .whereIn("UserID", )
//                .orderBy("date", Query.Direction.DESCENDING)
//                .limit(20);


//        groupActivitiesAdapter = new ActivitiesRecyclerViewAdapter(getActivity());
//        recyclerView.setAdapter(groupActivitiesAdapter);
//        groupActivityViewModel = new ViewModelProvider(this).get(GroupActivitiesViewModel.class);
//        groupActivityViewModel.loadGroupActivities("Sg8JLYf9lpE1akjQRHBv");
//        groupActivityViewModel.getGroupActivities().observe(getActivity(), new Observer<ArrayList<ActivityModel>>() {
//            @Override
//            public void onChanged(ArrayList<ActivityModel> activityModels) {
//                groupActivitiesAdapter.updateAdapter(activityModels);
//            }
//        });
//        LinearLayoutManager layout = new LinearLayoutManager(getActivity());
//        recyclerView.setLayoutManager(layout);
//        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));
//
//        groupActivityViewModel.getIsLoading().observe(getActivity(), new Observer<Boolean>() {
//            @Override
//            public void onChanged(Boolean aBoolean) {
//                loadingActivities.setVisibility(View.GONE);
//            }
//        });
//
//        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
//            @Override
//            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
//                super.onScrolled(recyclerView, dx, dy);
//                if(layout != null) {
//                    int onScreen = layout.getChildCount();
//                    int totalItems = layout.getItemCount();
//                    int firstItem = layout.findFirstVisibleItemPosition();
//
//                    if(!groupActivityViewModel.getIsLoading().getValue() && !groupActivityViewModel.getIsEndofArray().getValue()) {
//                        if((onScreen + firstItem) >= totalItems - 1) {
//                            isLoading = true;
//                            loadingActivities.setVisibility(View.VISIBLE);
//
//                            handler.postDelayed(new Runnable() {
//                                @Override
//                                public void run() {
//                                    groupActivityViewModel.loadGroupActivities("Sg8JLYf9lpE1akjQRHBv");
//                                    isLoading = false;
//                                }
//                            }, 3000);
//                        }
//                    }
//                }
//            }
//        });
    }

    @Override
    public void onStart() {
        super.onStart();
        groupActivitiesAdapter.startListening();
    }

    @Override
    public void onResume() {
        super.onResume();
        groupActivitiesAdapter.startListening();
        groupActivitiesAdapter.notifyDataSetChanged();
    }

    @Override
    public void onStop() {
        super.onStop();
        groupActivitiesAdapter.stopListening();
    }
}
