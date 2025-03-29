package com.example.fittrack;

import android.net.Uri;
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
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GroupsDatabaseUtil {
    private FirebaseFirestore db;

    public GroupsDatabaseUtil(FirebaseFirestore db) {
        this.db = db;
    }

    public void createNewGroup(String UserID, String GroupName, String Activity, String Location, String Motto, String GroupDescription) {
        DocumentReference groupReference = db.collection("Groups").document();
        String documentID = groupReference.getId();
        Map<String, Object> groupMap = new HashMap<>();
        groupMap.put("GroupID", documentID);
        groupMap.put("Activity", Activity);
        groupMap.put("Description", GroupDescription);
        groupMap.put("Name", GroupName);
        groupMap.put("Location", Location);
        groupMap.put("shortDescription", Motto);

        ArrayList<String> users = new ArrayList<>();
        users.add(UserID);
        groupMap.put("Admins", users);
        groupMap.put("Runners", users);
        groupMap.put("JoinRequests", new ArrayList<>());

        groupReference.set(groupMap).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                System.out.println("Created new group.");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                System.out.println("Failure to create a new group");
            }
        });
    }

    public void createNewPost(String GroupID, String UserID, Timestamp date, String description) {
        CollectionReference groupReference = db.collection("Groups").document(GroupID).collection("Posts");
        Map<String, Object> postFields = new HashMap<>();
        postFields.put("UserID", UserID);
        postFields.put("Date", date);
        postFields.put("Description", description);

        groupReference.add(postFields).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                System.out.println("Post added for group: " + GroupID);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                System.out.println("No Post added");
            }
        });
    }

    public void createNewMeetup(String GroupID, String UserID, String title, Timestamp date, String location, String description) {
        CollectionReference groupReference = db.collection("Groups").document(GroupID).collection("Meetups");
        Map<String, Object> meetupFields = new HashMap<>();
        meetupFields.put("Title", title);
        meetupFields.put("User", UserID);
        meetupFields.put("Date", date);
        meetupFields.put("Location", location);
        meetupFields.put("Description", description);
        meetupFields.put("MeetupID", " ");

        groupReference.add(meetupFields).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                documentReference.update("MeetupID", documentReference.getId())
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Log.d("Added MeetupID", "ID added for Meetup");
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.d("Failure to Add MeetupID", "ID failed to be added for Meetup");
                            }
                        });
                System.out.println("Meetup added for group " + GroupID);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                System.out.println("No Meetup added");
            }
        });
    }

    public void updateRunners(String GroupID, String UserID) {
        DocumentReference groupReference = db.collection("Groups").document(GroupID);
        groupReference.update("Runners", FieldValue.arrayUnion(UserID))
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        System.out.println("Updated runner list for: " + GroupID);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        System.out.println("Failed to update runner list for: " + GroupID);
                    }
                });

        groupReference.update("JoinRequests", FieldValue.arrayRemove(UserID))
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        System.out.println("Rejected request successful");
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        System.out.println("Failed to rejected request " + GroupID);
                    }
                });
    }

    public void getMeetupAccepted(String GroupID, String MeetupID, AcceptedCallback callback) {
        DocumentReference docReference = db.collection("Groups").document(GroupID).collection("Meetups").document(MeetupID);
        docReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Map<String, Object> data = documentSnapshot.getData();
                List<String> accepted = (List<String>) data.get("Accepted");
                List<LikeModel> people = new ArrayList<>();

                for(String accept : accepted) {
                    LikeModel likeModel = new LikeModel(accept, 0, null);
                    people.add(likeModel);
                }
                callback.onCallback(people);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e("No Accepted Failure", "No accepted members failure");
            }
        });
    }

    public void deleteMeetup(String GroupID, String MeetupID) {
        DocumentReference docReference = db.collection("Groups").document(GroupID).collection("Meetups").document(MeetupID);
        docReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Log.d("Meetup Deleted", "Meetup Successfully Deleted");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e("Delete Meetup Failure", "Delete Meetup Failure");
            }
        });
    }

    public void deletePost(String GroupID, String PostID) {
        db.collection("Groups").document(GroupID).collection("Posts").document(PostID).delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
    }

    public void updateMeetup(String GroupID, String MeetupID, String UserID, String Title, Timestamp Date, String Location, String Description, List<String> Accepted, List<String> Rejected) {
        DocumentReference docReference = db.collection("Groups").document(GroupID).collection("Meetups").document(MeetupID);
        Map<String, Object> meetupData = new HashMap<>();
        meetupData.put("GroupID", GroupID);
        meetupData.put("MeetupID", MeetupID);
        meetupData.put("User", UserID);
        meetupData.put("Title", Title);
        meetupData.put("Date", Date);
        meetupData.put("Location", Location);
        meetupData.put("Description", Description);
        meetupData.put("Accepted", Accepted);
        meetupData.put("Rejected", Rejected);
        docReference.update(meetupData).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }

    public void removeRequested(String GroupID, String UserID) {
        DocumentReference groupReference = db.collection("Groups").document(GroupID);
        groupReference.update("JoinRequests", FieldValue.arrayRemove(UserID))
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        System.out.println("Rejected request successful");
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        System.out.println("Failed to rejected request " + GroupID);
                    }
                });
    }

    public void retrieveGroupPosts(String GroupID, PostsCallback callback, DocumentSnapshot lastVisible) {

        Query query = db.collection("Groups").document(GroupID).collection("Posts");

        if (lastVisible != null) {
            query = query.startAfter(lastVisible);
        }
        query.get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            List<Map<String, Object>> data = new ArrayList<>();
                            DocumentSnapshot lastVisible = null;
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                data.add(document.getData());
                                lastVisible = document;
                            }
                            callback.onCallback(data, lastVisible);
                        } else {
                            System.out.println("Error getting data: " + task.getException());
                            callback.onCallback(null, null);
                        }
                    }
                });
    }

//    public void retrieveGroupMeetups(String GroupID, MeetupsCallback callback, DocumentSnapshot lastVisible) {
//        Query query = db.collection("Groups").document(GroupID).collection("Meetups");
//
//        if (lastVisible != null) {
//            query = query.startAfter(lastVisible);
//        }
//        query.get()
//                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                    @Override
//                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                        if (task.isSuccessful()) {
//                            List<Map<String, Object>> data = new ArrayList<>();
//                            DocumentSnapshot lastVisible = null;
//                            for (QueryDocumentSnapshot document : task.getResult()) {
//                                data.add(document.getData());
//                                lastVisible = document;
//                            }
//                            callback.onCallback(data, lastVisible);
//                        } else {
//                            System.out.println("Error getting data: " + task.getException());
//                            callback.onCallback(null, null);
//                        }
//                    }
//                });
//    }

    public void retrieveMeetup(String GroupID, String MeetupID, MeetupCallback callback) {
        DocumentReference docReference = db.collection("Groups").document(GroupID).collection("Meetups").document(MeetupID);
        docReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Map<String, Object> data = documentSnapshot.getData();
                callback.onCallback(data);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e("Meetup Retrival Failure", "Meetup Retrieval Failure");
            }
        });
    }

    public void retrieveSpecificGroup(String GroupID, GroupInformationCallback callback) {
        db.collection("Groups")
                .document(GroupID)
                .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        callback.onCallback(documentSnapshot);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("Group Document Retrieval Failure", "Failure to retrieve Group document: " + e);
                    }
                });
    }


    public void retrieveAllGroups(AllGroupsCallback callback) {
        db.collection("Groups")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        List<Map<String, Object>> groupsData = new ArrayList<>();
                        for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            Map<String, Object> data = documentSnapshot.getData();
                            groupsData.add(data);
                        }
                        callback.onCallback(groupsData);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        System.out.println("Couldn't get groups information");
                        System.out.println(e.getMessage());
                        callback.onCallback(null);
                    }
                });
    }

    public void retrieveUserGroups(String UserID, AllGroupsCallback callback) {
        db.collection("Groups")
                .whereArrayContains("Runners", UserID)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        List<Map<String, Object>> groupsData = new ArrayList<>();
                        for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            Map<String, Object> data = documentSnapshot.getData();
                            groupsData.add(data);
                        }
                        callback.onCallback(groupsData);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        System.out.println("Couldn't get groups information");
                        System.out.println(e.getMessage());
                        callback.onCallback(null);
                    }
                });
    }

    public void retrieveUsersinGroup(String groupId, UsersinGroupsCallback callback) {
        db.collection("Groups")
                .document(groupId)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        ArrayList<String> runners = (ArrayList<String>) documentSnapshot.get("Runners");
                        callback.onCallback(runners);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        System.out.println("Failure getting runners of a group");
                        callback.onCallback(null);
                    }
                });
    }

    public void retrieveUserRoles(String GroupID, UserRolesCallback callback) {
        db.collection("Groups")
                .document(GroupID)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        ArrayList<String> runners = (ArrayList<String>) documentSnapshot.get("Runners");
                        ArrayList<String> admins = (ArrayList<String>) documentSnapshot.get("Admins");
                        callback.onCallback(runners, admins);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        System.out.println("Failure getting roles of a group");
                        callback.onCallback(null, null);
                    }
                });
    }

    public void addAdmin(String GroupID, String UserID) {
        db.collection("Groups")
                .document(GroupID).update("Admins", FieldValue.arrayUnion(UserID))
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d("Add Admin Request", "Request to Add Admin Successful");
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("Add Admin Request Failure", "Request to Add Admin Failure: " + e);
                    }
                });
    }

    public void removeAdmin(String GroupID, String UserID) {
        db.collection("Groups")
                .document(GroupID).update("Admins", FieldValue.arrayRemove(UserID))
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d("Remove Admin Request", "Request to Remove Admin Successful");
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("Remove Admin Request Failure", "Request to Remove Admin Failure: " + e);
                    }
                });
    }

    public void retrieveGroupRequests(String GroupID, GroupRequestsCallback callback) {
        db.collection("Groups")
                .document(GroupID)
                .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        List<String> requests = (List<String>) documentSnapshot.get("JoinRequests");
                        if(requests != null) {
                            callback.onCallback(requests);
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("Group Requests Failure", "Failure to retrieve group requests: " + e);
                    }
                });
    }

    public void addGroupRequest(String GroupID, String currentUser) {
        db.collection("Groups")
                .document(GroupID).update("JoinRequests", FieldValue.arrayUnion(currentUser))
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d("Join Group Request", "Request to Join Group Successful");
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("Join Group Request Failure ", "Request to Join Group Failure: " + e);
                    }
                });
    }

    public void checkUserStatusinGroup(String GroupID, String currentUser, RequestsCallback callback) {
        db.collection("Groups")
                .document(GroupID)
                .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        ArrayList<String> requests = (ArrayList<String>) documentSnapshot.get("JoinRequests");
                        if(requests != null) {
                            if (requests.contains(currentUser)) {
                                callback.onCallback(true);
                            } else {
                                callback.onCallback(false);
                            }
                        } else {
                            callback.onCallback(false);
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("Request check failed", "Failure to check request status: " + e);
                    }
                });
    }

    public void retrieveGroupProfileImage(String groupPath, GroupPictureCallback callback) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageReference = storage.getReference("group-profile-images/" + groupPath);

        storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                callback.onCallback(uri);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e("Profile Picture Failure", "Couldn't retrieve profile picture");
            }
        });
    }

    public interface AllGroupsCallback {
        void onCallback(List<Map<String, Object>> groupsData);
    }

    public interface UserGroupsCallback {
        void onCallback(List<Map<String, Object>> userGroupsData);
    }

    public interface UsersinGroupsCallback {
        void onCallback(ArrayList<String> runners);
    }

    public interface UserRolesCallback {
        void onCallback(ArrayList<String> runners, ArrayList<String> admins);
    }

    public interface GroupRequestsCallback {
        void onCallback(List<String> requests);
    }

    public interface GroupInformationCallback {
        void onCallback(DocumentSnapshot documentSnapshot);
    }

    public interface PostsCallback {
        void onCallback(List<Map<String, Object>> posts, DocumentSnapshot lastVisible);
    }

    public interface MeetupsCallback {
        void onCallback(List<Map<String, Object>> meetups, DocumentSnapshot lastVisible);
    }

    public interface RequestsCallback{
        void onCallback(boolean RequestStatus);
    }

    public interface AcceptedCallback {
        void onCallback(List<LikeModel> accepted);
    }

    public interface MeetupCallback {
        void onCallback(Map<String, Object> meetupData);
    }

    public interface GroupPictureCallback {
        void onCallback(Uri PicturePath);
    }
}
