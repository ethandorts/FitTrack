package com.example.fittrack;

import android.net.Uri;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class GroupsDatabaseUtil {
    FirebaseFirestore db;

    public GroupsDatabaseUtil(FirebaseFirestore db) {
        this.db = db;
    }

    public void retrieveAllGroups(AllGroupsCallback callback) {
        db.collection("Groups")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        List<Map<String, Object>> groupsData = new ArrayList<>();
                        for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            Map<String, Object> data = documentSnapshot.getData();
                            groupsData.add(data);
                        }
                        callback.onCallback(groupsData);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        System.out.println("Couldn't get groups information");
                        System.out.println(e.getMessage());
                        callback.onCallback(null);
                    }
                });
    }

    public void retrieveUserGroups(String UserID, AllGroupsCallback callback) {
        db.collection("Groups")
                .whereArrayContains("Runners", UserID)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        List<Map<String, Object>> groupsData = new ArrayList<>();
                        for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            Map<String, Object> data = documentSnapshot.getData();
                            groupsData.add(data);
                        }
                        callback.onCallback(groupsData);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        System.out.println("Couldn't get groups information");
                        System.out.println(e.getMessage());
                        callback.onCallback(null);
                    }
                });
    }

    public interface AllGroupsCallback {
        void onCallback(List<Map<String, Object>> groupsData);
    }

    public interface UserGroupsCallback {
        void onCallback(List<Map<String, Object>> userGroupsData);
    }
}
