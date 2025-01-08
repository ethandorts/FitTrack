package com.example.fittrack;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

public class CommentUtil {
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    public CommentUtil(FirebaseFirestore db) {
        this.db = db;
    }

    public void addLike(String ActivityID) {
        CollectionReference reference = db.collection("Activities").document(ActivityID).collection("Likes");
        reference.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot querySnapshot) {

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }

    public void saveComment(String ActivityID, CommentModel comment) {
        Map<String, Object> commentMap = new HashMap<>();
        commentMap.put("UserID", comment.getUserID());
        commentMap.put("Comment", comment.getComment());
        commentMap.put("date", comment.getDate());

        db.collection("Activities").document(ActivityID).collection("Comments").document()
                .set(commentMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        System.out.println("Logged comment");
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        System.out.println("Failure saving comment: " + e);
                    }
                });
    }
}
