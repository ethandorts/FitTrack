package com.example.fittrack;

import android.net.Uri;
import android.provider.DocumentsContract;
import android.util.Log;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.w3c.dom.Document;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FirebaseDatabaseHelper {
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    List<Map<String, Object>> data;

    FirebaseDatabaseHelper( FirebaseFirestore db) {
        this.db = db;
    }
    public void retrieveUserActivities(String userID, FirestoreActivitiesCallback callback, DocumentSnapshot lastVisible) {

        Query query = db.collection("Activities")
                .whereEqualTo("UserID", userID)
                .orderBy("date", Query.Direction.DESCENDING)
                .limit(5);

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
                                Map<String, Object > activityData = document.getData();
                                activityData.put("ActivityID", document.getId());
                                System.out.println(document.getId());
                                data.add(activityData);
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

    public void retrieveSpecificActivity(String ActivityID, SpecificActivityCallback callback) {
        DocumentReference documentReference = db.collection("Activities").document(ActivityID);
        documentReference.get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        Map<String, Object> data = documentSnapshot.getData();
                        callback.onCallback(data);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        System.out.println("Couldn't fetch user details");
                        System.out.println(e.getMessage());
                        callback.onCallback(null);
                    }
                });
    }

    public void retrieveUserName(String UserID, FirestoreUserNameCallback callback) {
        DocumentReference documentReference = db.collection("Users").document(UserID);
        documentReference.get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists() && documentSnapshot.getData() != null) {
                            Map<String, Object> data = documentSnapshot.getData();
                            System.out.println("Data: " + data);
                            callback.onCallback((String) data.get("FullName"), (long) data.get("Weight"), (long) data.get("Height"), (long) data.get("ActivityFrequency"), (long) data.get("DailyCalorieGoal"));
                        } else {
                            callback.onCallback(null, 0, 0, 0, 0);
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        System.out.println("Couldn't fetch user details");
                        System.out.println(e.getMessage());
                        callback.onCallback(null, 0, 0, 0, 0);
                    }
                });
    }

    public void retrieveAllUsers( String loggedInID, FirestoreAllUsersCallback callback) {
        db.collection("Users")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()) {
                            List<Map<String, Object>> data = new ArrayList<>();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String documentID = document.getId();
                                if (!documentID.equals(loggedInID)) {
                                    Map<String, Object> userInfo = document.getData();
                                    userInfo.put("UserID", document.getId());
                                    data.add(userInfo);
                                }
                            }
                            callback.onCallback(data);
                        }
                    }
                });
    }

    public void retrieveProfilePicture( String userPath, ProfilePictureCallback callback) {

        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageReference = storage.getReference("profile-images/" + userPath);

        storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                callback.onCallback(uri);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e("Profile Picture Failure", "Couldn't retrieve profile picture");
            }
        });
    }

    public interface FirestoreUserNameCallback {
        void onCallback(String FullName, long weight, long height, long activityFrequency, long dailyCalorieGoal);
    }


    public interface FirestoreActivitiesCallback {
        void onCallback(List<Map<String, Object>> data, DocumentSnapshot lastVisible);
    }

    public interface SpecificActivityCallback {
        void onCallback(Map<String, Object> data);
    }

    public interface FirestoreAllUsersCallback {
        void onCallback(List<Map<String, Object>> data);
    }

    public interface ProfilePictureCallback {
        void onCallback(Uri PicturePath);
    }

}
