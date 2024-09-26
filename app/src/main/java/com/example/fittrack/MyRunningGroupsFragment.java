package com.example.fittrack;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MyRunningGroupsFragment extends Fragment {
    View view;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private String UserID = mAuth.getUid();
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private ArrayList<GroupModel> groupList = new ArrayList<>();
    private UserGroupsViewModel groupsViewModel = new UserGroupsViewModel();
    private GroupsRecyclerViewAdapter groupsAdapter;
    private GroupsDatabaseUtil groupsDatabaseUtil = new GroupsDatabaseUtil(db);

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.find_running_groups_fragment, container, false);

        RecyclerView groupsRecyclerView = view.findViewById(R.id.groupsRecyclerView);
        groupsAdapter = new GroupsRecyclerViewAdapter(getContext());
        groupsRecyclerView.setAdapter(groupsAdapter);
        groupsViewModel.getGroupsList().observe(getViewLifecycleOwner(), new Observer<ArrayList<GroupModel>>() {
            @Override
            public void onChanged(ArrayList<GroupModel> groupModels) {
                groupsAdapter.updateGroups(groupModels);
            }
        });
        LinearLayoutManager layout = new LinearLayoutManager(getContext());
        groupsRecyclerView.setLayoutManager(layout);
        groupsRecyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
        return view;
    }
}
