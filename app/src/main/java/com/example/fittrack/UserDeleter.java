package com.example.fittrack;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class UserDeleter {
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    public UserDeleter(FirebaseFirestore db) {
        this.db = db;
    }

    public void DeleteFromUsersTable(String documentID) {
        db.collection("Users")
                .document(documentID)
                .delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        System.out.println("User successfully deleted from Users Table");
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        System.out.println("User failed to deleted from User table");
                    }
                });
    }

    public void DeleteActivitiesFromUsers(String UserID) {
        db.collection("Activities")
                .whereEqualTo("UserID", UserID)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        for(QueryDocumentSnapshot document : task.getResult()) {
                            db.collection("Activities")
                                    .document(document.getId())
                                    .delete()
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            System.out.println("Users Activities Deleted");
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            System.out.println("Users Activities Deletion Failed");
                                        }
                                    });
                        }
                    }
                });
    }

    public void DeleteUsersChatChannels(String UserID) {
        db.collection("DM")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        for(QueryDocumentSnapshot document: task.getResult()) {
                            if(document.getId().contains(UserID)) {
                                CollectionReference ref = db.collection("DM")
                                        .document(document.getId()).collection("Messages");

                                ref.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        for(QueryDocumentSnapshot document : task.getResult()) {
                                            ref.document(document.getId())
                                                    .delete()
                                                    .addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            System.out.println("Error for deleting sub collections: " + e);
                                                        }
                                                    });
                                        }
                                    }
                                });

                                db.collection("DM")
                                        .document(document.getId())
                                        .delete()
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                System.out.println("User chats deleted");
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                System.out.println("User chats failed to delete");
                                            }
                                        });
                            }
                        }
                    }
                });
    }
}
