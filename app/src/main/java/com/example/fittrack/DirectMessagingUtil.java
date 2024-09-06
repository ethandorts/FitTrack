package com.example.fittrack;

import static android.content.ContentValues.TAG;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DirectMessagingUtil {
    FirebaseFirestore db;
    private String messageDocumentID;

    DirectMessagingUtil(FirebaseFirestore db) {
        this.db = db;
    }

    public void CreateDirectMessagingChannel(String sender, String recipient) {
        messageDocumentID = getDocumentID(sender, recipient);

        Map<String, Object> UsersInChat = new HashMap<>();
        ArrayList<String> usersList = new ArrayList<>();
        usersList.add(sender);
        usersList.add(recipient);
        UsersInChat.put("UsersInChat", usersList);

        DocumentReference documentReference = db.collection("DM").document(messageDocumentID);
        documentReference.set(UsersInChat)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("New document DM channel", "Successfully written");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error writing document", e);
                    }
                });
    }

    public void AddMessage(String sender, String recipient, String message) {
        Map<String, Object> IndividualMessageMap = new HashMap<>();
        IndividualMessageMap.put("sender", sender);
        IndividualMessageMap.put("recipient", recipient);
        IndividualMessageMap.put("message", message);
        IndividualMessageMap.put("timestamp", Timestamp.now());

        DocumentReference documentReference = db.collection("DM").document(messageDocumentID);

        documentReference.collection("Messages").
                add(IndividualMessageMap).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d("Successful Write", "Written to sub-collection");
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("Unsuccessful Write", "Unsuccessfully written to sub-collection");
                    }
                });

        Map<String, Object> lastMessageMap = new HashMap<>();
        lastMessageMap.put("LastMessage", message);
        documentReference.set(lastMessageMap, SetOptions.merge());
    }

    public void retrieveChannelMessages(String sender, String recipient , ChannelMessagesCallback callback) {
        messageDocumentID = getDocumentID(sender, recipient);
        db.collection("DM")
                .document(messageDocumentID)
                .collection("Messages")
                .orderBy("timestamp", Query.Direction.ASCENDING)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        List<Map<String, Object>> documentMessages = new ArrayList<>();
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                System.out.println(task.getResult());
                                documentMessages.add(document.getData());
                            }
                            callback.onCallback(documentMessages);
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                            callback.onCallback(null);
                        }
                    }
                });
    }

    public static String getDocumentID(String sender, String recipient) {
        List<String> users = new ArrayList<>();
        users.add(sender);
        users.add(recipient);
        users.sort(String::compareTo);

        return String.join("-", users);
    }

    public interface ChannelMessagesCallback {
        void onCallback(List<Map<String, Object>> documentMessages);
    }
}
