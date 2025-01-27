package com.example.fittrack;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.Date;

public class LogFoodNotificationReceiver extends BroadcastReceiver {
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private String UserID = mAuth.getUid();
    @Override
    public void onReceive(Context context, Intent intent) {
        NotificationUtil.createLogFoodNotificationChannel(context);
        String mealType = intent.getStringExtra("mealType");
        checkNoFoodItems(mealType, new NoItemsCallback() {
            @Override
            public void onCallback(boolean noItems) {
                if(noItems) {
                    System.out.println("No " + mealType + " items");
                    NotificationUtil.showLogFoodNotification(context, mealType);
                } else {
                    System.out.println("Lunch items recorded");
                }
            }
        });
    }

    public void checkNoFoodItems(String mealType, NoItemsCallback callback) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        Date today = new Date();
        String shortDate = dateFormat.format(today);

        Query query = db.collection("Users")
                .document(UserID)
                .collection("Nutrition")
                .document(shortDate)
                .collection("Meals")
                .whereEqualTo("mealType", mealType);

        query.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot querySnapshot) {
                if(querySnapshot.getDocuments().size() == 0) {
                    callback.onCallback(true);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("Food Recorded Checker", "Failure to check if food has been recorded");
            }
        });
    }

    public interface NoItemsCallback {
        void onCallback(boolean noItems);
    }
}
