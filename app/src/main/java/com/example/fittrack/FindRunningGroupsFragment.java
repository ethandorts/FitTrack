package com.example.fittrack;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Map;

public class FindRunningGroupsFragment extends Fragment {
    private View view;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private GroupsViewModel groupsViewModel = new GroupsViewModel();
    private FindGroupsRecyclerViewAdapter groupsAdapter;
    private ArrayList<GroupModel> groupsList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.find_running_groups_fragment, container, false);

//        groupsDatabaseUtil.retrieveAllGroups(new GroupsDatabaseUtil.AllGroupsCallback() {
//            @Override
//            public void onCallback(List<Map<String, Object>> groupsData) {
//                for(Map<String, Object> data : groupsData) {
//                    GroupModel group = new GroupModel(
//                            (String) data.get("Name"),
//                            (String) data.get("Description"),
//                            null
//                    );
//                    groupList.add(group);
//                    groupsAdapter.notifyDataSetChanged();
//                }
//            }
//        });

        EditText searchGroups = view.findViewById(R.id.editSearchGroups);
        RecyclerView groupsRecyclerView = view.findViewById(R.id.GroupsRecyclerView);
        groupsAdapter = new FindGroupsRecyclerViewAdapter(getContext(), true);
        groupsViewModel = new ViewModelProvider(this).get(GroupsViewModel.class);
        groupsRecyclerView.setAdapter(groupsAdapter);
        groupsViewModel.getGroupsList().observe(getViewLifecycleOwner(), new Observer<ArrayList<GroupModel>>() {
            @Override
            public void onChanged(ArrayList<GroupModel> groupModels) {
                System.out.println("Group Models: " + groupModels);
                groupsList.clear();
                groupsList.addAll(groupModels);
                groupsAdapter.updateGroups(groupsList);
            }
        });

        searchGroups.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterSearchGroups(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        LinearLayoutManager layout = new LinearLayoutManager(getContext());
        groupsRecyclerView.setLayoutManager(layout);
        groupsRecyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
        return view;
    }

    private void filterSearchGroups(String searchValue) {
        ArrayList<GroupModel> searchGroups = new ArrayList<>();
        for (GroupModel group : groupsList) {
            if (group.getName().toLowerCase().contains(searchValue.toLowerCase())) {
                searchGroups.add(group);
            }
        }
        groupsAdapter.updateGroups(searchGroups);
    }
}
