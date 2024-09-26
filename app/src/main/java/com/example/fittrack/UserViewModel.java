package com.example.fittrack;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class UserViewModel extends ViewModel {
    MutableLiveData<ArrayList<UserModel>> users = new MutableLiveData<>();
    MutableLiveData<String> lastMessageSeen = new MutableLiveData<>();
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private String UserID = mAuth.getUid();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseDatabaseHelper DatabaseUtil = new FirebaseDatabaseHelper(db);

    public UserViewModel() {
        loadUsersToChat();
    }

    public MutableLiveData<ArrayList<UserModel>> getChatUsers() {
        return users;
    }

    public void loadUsersToChat() {
        DatabaseUtil.retrieveAllUsers(UserID, new FirebaseDatabaseHelper.FirestoreAllUsersCallback() {
            @Override
            public void onCallback(List<Map<String, Object>> data) {
                ArrayList<UserModel> userList = new ArrayList<>();
                System.out.println("Data retrieved size: " + data.size());
                for(Map<String, Object> userMap : data) {
                    // data got - formatting users
                    String documentID = DirectMessagingUtil.getDocumentID(UserID, (String) userMap.get("UserID"));
                    System.out.println("User Retrieved: " + userMap.get("UserID"));
                    db.collection("DM").document(documentID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            String lastMessage ="";
                            DocumentSnapshot retrievedSnapshot = task.getResult();
                            lastMessage = (String) retrievedSnapshot.get("LastMessage");
                            UserModel user = new UserModel((String) userMap.get("FirstName") + " " + userMap.get("Surname"),
                                    lastMessage,
                                    (String) userMap.get("UserID"));
                            System.out.println(userMap.get("FirstName" + " hello " + userMap.get("Surname")));
                            userList.add(user);
                            users.setValue(userList);
                        }
                    });
                }
            }
        });
    }

}
