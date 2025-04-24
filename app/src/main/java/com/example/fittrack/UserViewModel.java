package com.example.fittrack;

import android.util.Log;

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
import java.util.concurrent.atomic.AtomicInteger;

public class UserViewModel extends ViewModel {
    MutableLiveData<ArrayList<UserModel>> users = new MutableLiveData<>();
    MutableLiveData<String> lastMessageSeen = new MutableLiveData<>();
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private String UserID = mAuth.getUid();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseDatabaseHelper DatabaseUtil = new FirebaseDatabaseHelper(db);
    private GroupsDatabaseUtil groupsDatabaseUtil = new GroupsDatabaseUtil(db);
    private String groupId; // Store the groupId for reuse

    public UserViewModel(String GroupID) {
        this.groupId = GroupID;
        loadGroupUsers(GroupID);
    }

    public UserViewModel() {}

    public MutableLiveData<ArrayList<UserModel>> getChatUsers() {
        return users;
    }

    public void refreshChatUsers() {
        if (groupId != null && !groupId.isEmpty()) {
            loadGroupUsers(groupId);
        } else {
            Log.e("UserViewModel", "groupId is null or empty, cannot refresh.");
        }
    }

    public void loadUsersToChat() {
        DatabaseUtil.retrieveAllUsers(UserID, new FirebaseDatabaseHelper.FirestoreAllUsersCallback() {
            @Override
            public void onCallback(List<Map<String, Object>> data) {
                ArrayList<UserModel> userList = new ArrayList<>();
                System.out.println("Data retrieved size: " + data.size());
                for(Map<String, Object> userMap : data) {

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

    public void loadGroupUsers(String GroupID) {
        if (GroupID == null || GroupID.isEmpty()) {
            Log.e("loadGroupUsers", "GroupID is null or empty");
            return;
        }

        groupsDatabaseUtil.retrieveUsersinGroup(GroupID, new GroupsDatabaseUtil.UsersinGroupsCallback() {
            @Override
            public void onCallback(ArrayList<String> runners) {
                ArrayList<UserModel> userList = new ArrayList<>();

                if (runners.isEmpty()) {
                    users.setValue(userList);
                    return;
                }

                AtomicInteger completedTasks = new AtomicInteger(0);  // Track completed tasks

                for (String runner : runners) {
                    if (UserID == null || runner == null) {
                        Log.e("loadGroupUsers", "UserID or runner is null, skipping.");
                        continue;
                    }

                    String documentID = DirectMessagingUtil.getDocumentID(UserID, runner);
                    if (documentID == null || documentID.isEmpty()) {
                        Log.e("loadGroupUsers", "documentID is null or empty for runner: " + runner);
                        continue;
                    }

                    db.collection("DM").document(documentID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            String lastMessage = "";

                            if (task.isSuccessful() && task.getResult() != null) {
                                DocumentSnapshot retrievedSnapshot = task.getResult();
                                lastMessage = retrievedSnapshot.exists() ? retrievedSnapshot.getString("LastMessage") : "";
                            } else {
                                Log.e("Firestore", "Error retrieving document: " + task.getException());
                            }

                            final String finalLastMessage = (lastMessage != null) ? lastMessage : "";

                            DatabaseUtil.retrieveChatName(runner, new FirebaseDatabaseHelper.ChatUserCallback() {
                                @Override
                                public void onCallback(String FullName) {
                                    UserModel userModel = new UserModel(FullName, finalLastMessage, runner);
                                    if(!userModel.getUserID().equals(UserID)) {
                                        userList.add(userModel);
                                    }

                                    if (completedTasks.incrementAndGet() == runners.size()) {
                                        users.setValue(userList);
                                    }
                                }
                            });
                        }
                    });
                }
            }
        });
    }
}