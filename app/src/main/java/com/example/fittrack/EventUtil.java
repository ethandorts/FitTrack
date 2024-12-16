package com.example.fittrack;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.auth.User;

import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class EventUtil {
    private FirebaseFirestore db;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private String UserID = mAuth.getUid();

    public EventUtil(FirebaseFirestore db) {
        this.db = db;
    }

    public void addNewEvent(EventModel event) {

        Map<String, Object> eventMap = new HashMap<>();
        eventMap.put("ActivityType", event.getActivityType());
        eventMap.put("DateTime", event.getDateTime());
        eventMap.put("Description", event.getDescription());
        eventMap.put("EventName", event.getEventName());
        eventMap.put("Completed", event.getCompleted());

        if(UserID == null) {
            System.out.println("User is null");
        } else {
            System.out.println("User is not null");
        }

        CollectionReference collectionReference = db.collection("Users").
                document(UserID)
                .collection("Calendar");

        db.collection("Users").document(UserID).collection("Calendar")
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot querySnapshot) {
                            db.collection("Users").document(UserID).collection("Calendar").document()
                                    .set(eventMap)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            System.out.println("Logged event");
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            System.out.println("Sub collection for user could not be created");
                                        }
                                    });
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        System.out.println("No sub collection found");
                    }
                });
    }
}
