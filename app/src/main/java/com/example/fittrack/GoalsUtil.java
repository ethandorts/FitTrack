package com.example.fittrack;

import com.google.firebase.firestore.FirebaseFirestore;

public class GoalsUtil {
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    public GoalsUtil(FirebaseFirestore db) {
        this.db = db;
    }

    public void setGoals(String UserID) {
        db.collection("Users")
                .document(UserID)
                .collection("Goals");
    }
}
