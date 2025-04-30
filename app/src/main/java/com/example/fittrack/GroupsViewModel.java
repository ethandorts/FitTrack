package com.example.fittrack;

import android.widget.ImageView;

import androidx.constraintlayout.widget.Group;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class GroupsViewModel extends ViewModel {
    private MutableLiveData<ArrayList<GroupModel>> groupsList = new MutableLiveData<>();
    private ArrayList<GroupModel> groups = new ArrayList<>();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private GroupsDatabaseUtil groupsDatabaseUtil = new GroupsDatabaseUtil(db);
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private String UserID = mAuth.getUid();

    public GroupsViewModel () {
        loadGroups(UserID);
    }

    public MutableLiveData<ArrayList<GroupModel>> getGroupsList() {
        return groupsList;
    }

    private void loadGroups(String UserID) {
        groupsDatabaseUtil.retrieveAllGroups(new GroupsDatabaseUtil.AllGroupsCallback() {
            @Override
            public void onCallback(List<Map<String, Object>> groupsData) {
                List<GroupModel> availableGroups = new ArrayList<>();
                for(Map<String, Object> data : groupsData) {
                    ArrayList<String> runners = (ArrayList<String>) data.get("Runners");
                    System.out.println("Runners in group: " + runners);
                    if (runners == null || !runners.contains(UserID)) {
                        GroupModel group = new GroupModel(
                                (String) data.get("GroupID"),
                                (String) data.get("Name"),
                                (String) data.get("shortDescription"),
                                (String) data.get("Description"),
                                (String) data.get("Location"),
                                (ArrayList<String>) data.get("Runners"),
                                (String) data.get("Activity")
                        );
                        availableGroups.add(group);
                    }
                }
                System.out.println("Available Groups To Show: " + availableGroups.size());
                groups.clear();
                groups.addAll(availableGroups);
                System.out.println("Groups Value Display:" + groups.size());
                groupsList.postValue(groups);
            }
        });
    }

    public void loadUserGroups(String UserID) {
        groupsDatabaseUtil.retrieveUserGroups(UserID, new GroupsDatabaseUtil.AllGroupsCallback() {
            @Override
            public void onCallback(List<Map<String, Object>> groupsData) {
                for(Map<String, Object> data : groupsData) {
                    GroupModel group = new GroupModel(
                            (String) data.get("GroupID"),
                            (String) data.get("Name"),
                            (String) data.get("shortDescription"),
                            (String) data.get("Description"),
                            (String) data.get("Location"),
                            (ArrayList<String>) data.get("Runners"),
                            (String) data.get("Activity")
                    );
                    groups.add(group);
                    groupsList.setValue(groups);
                }
            }
        });
    }
}
