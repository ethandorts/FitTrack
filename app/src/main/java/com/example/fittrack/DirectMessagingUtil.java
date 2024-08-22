package com.example.fittrack;

import static android.content.ContentValues.TAG;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

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

    public void AddMessage(String sender, String recipient, String message_content) {
        Map<String, Object> IndividualMessageMap = new HashMap<>();
        IndividualMessageMap.put("sender", sender);
        IndividualMessageMap.put("recipient", recipient);
        IndividualMessageMap.put("message-content", message_content);
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
    }

    private String getDocumentID (String sender, String recipient) {
        List<String> users = new ArrayList<>();
        users.add(sender);
        users.add(recipient);
        users.sort(String::compareTo);

        return String.join("-", users);
    }
}
