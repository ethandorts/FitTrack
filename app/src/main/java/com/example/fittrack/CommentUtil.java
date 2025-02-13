package com.example.fittrack;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.auth.User;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CommentUtil {
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private boolean hasLiked = false;

    public CommentUtil(FirebaseFirestore db) {
        this.db = db;
    }

    public void checkLike(String ActivityID, String UserID, LikeCheckCallback callback) {
        DocumentReference document = db.collection("Activities").document(ActivityID);
        document.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                List<String> likes = (List<String>) documentSnapshot.get("likes");
                System.out.println(likes);
                if (likes != null) {
                    if (likes.contains(UserID)) {
                        callback.onCallback(true);
                    } else {
                        callback.onCallback(false);
                    }
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("Like Check Failure", "Failed to check like: " + e);
            }
        });
    }

    public void addLike(String ActivityID, String UserID) {
        db.collection("Activities").document(ActivityID)
                .update("likes", FieldValue.arrayUnion(UserID))
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d("Like Added", "Like from user was added to this activity: " + ActivityID);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("Like Failure", "Failure to add like to: " + ActivityID);
                    }
                });
    }

    public void removeLike(String ActivityID, String UserID) {
        db.collection("Activities").document(ActivityID)
                .update("likes", FieldValue.arrayRemove(UserID))
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d("Like Removed", "Like was removed from this activity: " + ActivityID);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("Like Failure", "Failure to remove like from: " + ActivityID);
                    }
                });
    }

    public void retrieveLikes(String ActivityID, LikesListCallback callback) {
        db.collection("Activities").document(ActivityID)
                .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        List<String> likesList = (List<String>) documentSnapshot.get("likes");
                        if(likesList != null) {
                            callback.onCallback(likesList);
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("Likes Retrieval Failure", "Failure to retrieve likes from: " + ActivityID);
                    }
                });
    }

    public void retrieveLikeCount(String ActivityID, LikeNumberCallback callback) {
        db.collection("Activities").document(ActivityID)
                .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        List<String> likesSize = (List<String>) documentSnapshot.get("likes");
                        if(likesSize != null) {
                            callback.onCallback(likesSize.size());
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("Likes Size Retrieval Failure", "Failure to retrieve likes size from: " + ActivityID);
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

    public void savePostComment(String GroupID, String PostID, CommentModel comment) {
        Map<String, Object> commentMap = new HashMap<>();
        commentMap.put("UserID", comment.getUserID());
        commentMap.put("Comment", comment.getComment());
        commentMap.put("date", comment.getDate());

        db.collection("Groups").document(GroupID).collection("Posts").document(PostID).collection("Comments").document()
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

    public void acceptMeetup(String GroupID, String Meetup, String UserID) {
        DocumentReference documentReference = db.collection("Groups")
                .document(GroupID)
                .collection("Meetups")
                .document(Meetup);

        documentReference.update("Accepted", FieldValue.arrayUnion(UserID))
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d("Meetup Accepted", UserID + " accepted this meetup: " + Meetup);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("Meetup Accept Failure", "Failure: " + e);
                    }
                });
        documentReference.update("Rejected", FieldValue.arrayRemove(UserID))
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d("Reverse Meetup Accepted", UserID + " accepted this meetup: " + Meetup);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("Reverse Meetup Accepted Failure", "Failure: " + e);
                    }
                });
    }

    public void rejectMeetup(String GroupID, String Meetup, String UserID) {
        DocumentReference documentReference = db.collection("Groups")
                .document(GroupID)
                .collection("Meetups")
                .document(Meetup);

        documentReference.update("Rejected", FieldValue.arrayUnion(UserID))
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d("Reverse Meetup Rejected", UserID + " rejected this meetup: " + Meetup);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("Reserve Meetup Rejection Failure", "Failure: " + e);
                    }
                });

        documentReference.update("Accepted", FieldValue.arrayRemove(UserID))
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d("Meetup Rejected", UserID + " rejected this meetup: " + Meetup);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("Meetup Rejection Failure", "Failure: " + e);
                    }
                });
    }

    public void checkMeetupStatus(String GroupID, String UserID, String MeetupID, StatusCheckCallback callback) {

        DocumentReference documentReference = db.collection("Groups")
                .document(GroupID)
                .collection("Meetups")
                .document(MeetupID);

        documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                List<String> acceptedList = (List<String>) documentSnapshot.get("Accepted");
                List<String> rejectedList = (List<String>) documentSnapshot.get("Rejected");

                boolean isAccepted = acceptedList != null && acceptedList.contains(UserID);
                boolean isRejected = rejectedList != null && rejectedList.contains(UserID);

                if(isAccepted) {
                    callback.onCallback(true);
                } else if (isRejected) {
                    callback.onCallback(false);
                } else {
                    callback.onCallback(false);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("Meetup Status Failure", "Could not retrieve meetup status due to " + e);
            }
        });
    }

    public interface LikeCheckCallback {
        void onCallback(boolean hasLiked);
    }

    public interface LikeNumberCallback {
        void onCallback(int likesNumber);
    }

    public interface LikesListCallback {
        void onCallback(List<String> likesList);
    }

    public interface StatusCheckCallback {
        void onCallback(boolean statusCheck);
    }
}
