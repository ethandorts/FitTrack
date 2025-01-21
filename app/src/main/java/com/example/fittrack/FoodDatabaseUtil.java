package com.example.fittrack;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class FoodDatabaseUtil {
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private String UserID = mAuth.getUid();

    public FoodDatabaseUtil(FirebaseFirestore db) {
        this.db = db;
    }

    public void saveFood(FoodModel food) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        Date today = new Date();
        String date = dateFormat.format(today);


        Map<String, Object> FoodMap = new HashMap<>();
        FoodMap.put("foodName", food.getFoodName());
        FoodMap.put("calories", food.getCalories());
        FoodMap.put("quantity", 1);
        FoodMap.put("mealType", food.getMealType());

        CollectionReference collectionRef = db.collection("Users")
                .document(UserID)
                .collection("Nutrition")
                .document(date)
                .collection("Meals");

//        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//            @Override
//            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//                if(task.isSuccessful()) {
//                    DocumentSnapshot document = task.getResult();
//                    if(document.exists()) {
//                        updateDoc(docRef, FoodMap);
//                    } else {
//                        createDoc(docRef, FoodMap);
//                    }
//                } else {
//                    System.out.println("No document got: " + task.getException());
//                }
//            }
//        });

        collectionRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                collectionRef.add(FoodMap).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        System.out.println("Added new food to the daily caloric intake");
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        System.out.println("Food couldn't be added");
                    }
                });
            }
        });

    }
//    private void updateDoc(CollectionReference docRef, Map<String, Object> FoodMap) {
//        docRef.update("food", FieldValue.arrayUnion(FoodMap))
//                .addOnSuccessListener(new OnSuccessListener<Void>() {
//                    @Override
//                    public void onSuccess(Void unused) {
//                        System.out.println("food saved");
//                    }
//                }).addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//                        System.out.println("food not saved" + e);
//                    }
//                });
//    }

//    private void createDoc(CollectionReference docRef, Map<String, Object> FoodMap) {
//        Map<String, Object> data = new HashMap<>();
//        data.put("food", FieldValue.arrayUnion(FoodMap));
//
//        docRef.set(data).addOnSuccessListener(new OnSuccessListener<Void>() {
//            @Override
//            public void onSuccess(Void unused) {
//                System.out.println("food saved");
//            }
//        }).addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception e) {
//                System.out.println("food not saved");
//            }
//        });
//    }
}
