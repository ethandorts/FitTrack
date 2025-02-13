package com.example.fittrack;

import com.google.firebase.Timestamp;

public class PostModel {
    private String UserID;
    private Timestamp Date;
    private String Description;

    public PostModel(String userID, Timestamp date, String description) {
        UserID = userID;
        this.Date = date;
        this.Description = description;
    }

    public PostModel() {

    }

    public String getUserID() {
        return UserID;
    }

    public void setUserID(String userID) {
        UserID = userID;
    }

    public Timestamp getDate() {
        return Date;
    }

    public void setDate(Timestamp date) {
        Date = date;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        this.Description = description;
    }
}
