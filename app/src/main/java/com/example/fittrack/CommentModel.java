package com.example.fittrack;

import com.google.firebase.Timestamp;

public class CommentModel {
    private String UserID;
    private String Comment;
    private Timestamp date;

    public CommentModel(String userID, String comment, Timestamp date) {
        UserID = userID;
        Comment = comment;
        this.date = date;
    }

    public CommentModel() {

    }

    public String getComment() {
        return Comment;
    }

    public String getUserID() {
        return UserID;
    }

    public Timestamp getDate() {
        return date;
    }
}
