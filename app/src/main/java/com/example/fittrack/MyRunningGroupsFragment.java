package com.example.fittrack;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
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
    private GroupsRecyclerViewAdapter groupsAdapter;
    private GroupsDatabaseUtil groupsDatabaseUtil = new GroupsDatabaseUtil(db);

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.find_running_groups_fragment, container, false);

        groupsDatabaseUtil.retrieveUserGroups(UserID, new GroupsDatabaseUtil.AllGroupsCallback() {
            @Override
            public void onCallback(List<Map<String, Object>> groupsData) {
                for(Map<String, Object> data : groupsData) {
                    GroupModel group = new GroupModel(
                            (String) data.get("Name"),
                            (String) data.get("Description"),
                            null
                    );
                    groupList.add(group);
                    groupsAdapter.notifyDataSetChanged();
                }
            }
        });

        RecyclerView groupsRecyclerView = view.findViewById(R.id.groupsRecyclerView);
        groupsAdapter = new GroupsRecyclerViewAdapter(getContext(), groupList);
        groupsRecyclerView.setAdapter(groupsAdapter);
        LinearLayoutManager layout = new LinearLayoutManager(getContext());
        groupsRecyclerView.setLayoutManager(layout);
        groupsRecyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
        return view;
    }
}
