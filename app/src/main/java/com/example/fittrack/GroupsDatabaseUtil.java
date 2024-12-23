package com.example.fittrack;

import android.net.Uri;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GroupsDatabaseUtil {
    private FirebaseFirestore db;

    public GroupsDatabaseUtil(FirebaseFirestore db) {
        this.db = db;
    }

    public void createNewGroup(String UserID, String GroupName, String GroupDescription) {
        DocumentReference groupReference = db.collection("Groups").document();
        String documentID = groupReference.getId();
        Map<String, Object> groupMap = new HashMap<>();
        groupMap.put("GroupID", documentID);
        groupMap.put("Description", GroupDescription);
        groupMap.put("Name", GroupName);
        ArrayList<String> users = new ArrayList<>();
        users.add(UserID);
        groupMap.put("Admins", users);
        groupMap.put("Runners", users);

        groupReference.set(groupMap).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                System.out.println("Created new group.");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                System.out.println("Failure to create a new group");
            }
        });
    }

    public void createNewPost(String GroupID, String UserID, Timestamp date, String description) {
        CollectionReference groupReference = db.collection("Groups").document(GroupID).collection("Posts");
        Map<String, Object> postFields = new HashMap<>();
        postFields.put("UserID", UserID);
        postFields.put("Date", date);
        postFields.put("Description", description);

        groupReference.add(postFields).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                System.out.println("Post added for group: " + GroupID);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                System.out.println("No Post added");
            }
        });
    }

    public void createNewMeetup(String GroupID, String UserID, String title, String date, String location, String description) {
        CollectionReference groupReference = db.collection("Groups").document(GroupID).collection("Meetups");
        Map<String, Object> meetupFields = new HashMap<>();
        meetupFields.put("Title", title);
        meetupFields.put("User", UserID);
        meetupFields.put("Date", date);
        meetupFields.put("Location", location);
        meetupFields.put("Description", description);

        groupReference.add(meetupFields).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                System.out.println("Meetup added for group " + GroupID);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                System.out.println("No Meetup added");
            }
        });
    }

    public void updateRunners(String GroupID, String UserID) {
        DocumentReference groupReference = db.collection("Groups").document(GroupID);
        groupReference.update("Runners", FieldValue.arrayUnion(UserID))
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        System.out.println("Updated runner list for: " + GroupID);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        System.out.println("Failed to update runner list for: " + GroupID);
                    }
                });
    }

    public void retrieveGroupPosts(String GroupID, PostsCallback callback, DocumentSnapshot lastVisible) {

        Query query = db.collection("Groups").document(GroupID).collection("Posts");

        if (lastVisible != null) {
            query = query.startAfter(lastVisible);
        }
        query.get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            List<Map<String, Object>> data = new ArrayList<>();
                            DocumentSnapshot lastVisible = null;
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                data.add(document.getData());
                                lastVisible = document;
                            }
                            callback.onCallback(data, lastVisible);
                        } else {
                            System.out.println("Error getting data: " + task.getException());
                            callback.onCallback(null, null);
                        }
                    }
                });
    }

    public void retrieveGroupMeetups(String GroupID, MeetupsCallback callback, DocumentSnapshot lastVisible) {
        Query query = db.collection("Groups").document(GroupID).collection("Meetups");

        if (lastVisible != null) {
            query = query.startAfter(lastVisible);
        }
        query.get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            List<Map<String, Object>> data = new ArrayList<>();
                            DocumentSnapshot lastVisible = null;
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                data.add(document.getData());
                                lastVisible = document;
                            }
                            callback.onCallback(data, lastVisible);
                        } else {
                            System.out.println("Error getting data: " + task.getException());
                            callback.onCallback(null, null);
                        }
                    }
                });
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

    public void retrieveUsersinGroup(String groupId, UsersinGroupsCallback callback) {
        db.collection("Groups")
                .document(groupId)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        ArrayList<String> runners = (ArrayList<String>) documentSnapshot.get("Runners");
                        callback.onCallback(runners);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        System.out.println("Failure getting runners of a group");
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

    public interface UsersinGroupsCallback {
        void onCallback(ArrayList<String> runners);
    }

    public interface PostsCallback {
        void onCallback(List<Map<String, Object>> posts, DocumentSnapshot lastVisible);
    }

    public interface MeetupsCallback {
        void onCallback(List<Map<String, Object>> meetups, DocumentSnapshot lastVisible);
    }
}
