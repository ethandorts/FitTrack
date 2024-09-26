package com.example.fittrack;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class UserGroupsViewModel extends ViewModel {
    private MutableLiveData<ArrayList<GroupModel>> groupsList = new MutableLiveData<>();
    private ArrayList<GroupModel> groups = new ArrayList<>();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private GroupsDatabaseUtil groupsDatabaseUtil = new GroupsDatabaseUtil(db);
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private String UserID = mAuth.getUid();

    public UserGroupsViewModel () {
        loadUserGroups(UserID);
    }

    public MutableLiveData<ArrayList<GroupModel>> getGroupsList() {
        return groupsList;
    }

    public void loadUserGroups(String UserID) {
        groupsDatabaseUtil.retrieveUserGroups(UserID, new GroupsDatabaseUtil.AllGroupsCallback() {
            @Override
            public void onCallback(List<Map<String, Object>> groupsData) {
                for(Map<String, Object> data : groupsData) {
                    GroupModel group = new GroupModel(
                            (String) data.get("Name"),
                            (String) data.get("Description"),
                            null
                    );
                    groups.add(group);
                    groupsList.setValue(groups);
                }
            }
        });
    }
}
