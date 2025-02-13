package com.example.fittrack;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

public class GroupPostsViewModel extends ViewModel {
    private MutableLiveData<ArrayList<PostModel>> GroupPosts = new MutableLiveData<>();
    private MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private MutableLiveData<Boolean> isEndofArray = new MutableLiveData<>(false);
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private String UserID = mAuth.getUid();
    private GroupsDatabaseUtil GroupsUtil = new GroupsDatabaseUtil(db);
    private DocumentSnapshot lastVisible = null;
    public GroupPostsViewModel() {

    }
    public MutableLiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public MutableLiveData<Boolean> getIsEndofArray() {
        return isEndofArray;
    }

    public MutableLiveData<ArrayList<PostModel>> getGroupActivities() {
        return GroupPosts;
    }

    public void loadGroupPosts(String GroupID) {
        if (Boolean.TRUE.equals(isLoading.getValue()) || Boolean.TRUE.equals(isEndofArray.getValue())) {
            return;
        }
        ArrayList<PostModel> postCollection = new ArrayList<>();
        isLoading.setValue(true);
        GroupsUtil.retrieveGroupPosts(GroupID, new GroupsDatabaseUtil.PostsCallback() {
            @Override
            public void onCallback(List<Map<String, Object>> posts, DocumentSnapshot lastVisible) {
                List<Map<String, Object>> data = new ArrayList<>();
                for(Map<String, Object> post : posts) {
                    PostModel singlePost = new PostModel(
                            String.valueOf(post.get("UserID")),
                            (Timestamp) post.get("Date"),
                            String.valueOf(post.get("Description"))
                    );
                    postCollection.add(singlePost);
                }
                isLoading.setValue(false);
                GroupPosts.setValue(postCollection);
            }
        }, lastVisible);
    }
}
