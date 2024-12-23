package com.example.fittrack;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class GroupMeetupsViewModel extends ViewModel {
    private MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private MutableLiveData<Boolean> isEndofArray = new MutableLiveData<>(false);
    private MutableLiveData<ArrayList<MeetupModel>> meetupsList = new MutableLiveData<>();
    private ArrayList<MeetupModel> meetups = new ArrayList<>();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private GroupsDatabaseUtil GroupsUtil = new GroupsDatabaseUtil(db);
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private String UserID = mAuth.getUid();
    private DocumentSnapshot lastVisible = null;
    public GroupMeetupsViewModel() {

    }
    public MutableLiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public MutableLiveData<Boolean> getIsEndofArray() {
        return isEndofArray;
    }

    public MutableLiveData<ArrayList<MeetupModel>> getGroupMeetups() {
        return meetupsList;
    }

    public void loadGroupMeetups(String GroupID) {
        if (Boolean.TRUE.equals(isLoading.getValue()) || Boolean.TRUE.equals(isEndofArray.getValue())) {
            return;
        }
        ArrayList<MeetupModel> meetupCollection = new ArrayList<>();
        isLoading.setValue(true);

        GroupsUtil.retrieveGroupMeetups(GroupID, new GroupsDatabaseUtil.MeetupsCallback() {
            @Override
            public void onCallback(List<Map<String, Object>> meetups, DocumentSnapshot lastVisible) {
                List<Map<String, Object>> data = new ArrayList<>();
                for(Map<String, Object> singleMeetup : meetups) {
                    MeetupModel meetup = new MeetupModel(
                            (String) singleMeetup.get("Title"),
                            (String) singleMeetup.get("User"),
                            (String) singleMeetup.get("Date"),
                            (String) singleMeetup.get("Location"),
                            (String) singleMeetup.get("Details"),
                            (String) singleMeetup.get("Status")
                    );
                    meetupCollection.add(meetup);
                }
                isLoading.setValue(false);
                meetupsList.setValue(meetupCollection);
            }
        }, lastVisible);
    }
}
