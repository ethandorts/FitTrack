package com.example.fittrack;

import com.google.firebase.Timestamp;

public class PostModel {
    private String UserID;
    private String Date;
    private String Description;

    public PostModel(String userID, String date, String description) {
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

    public String getDate() {
        return Date;
    }

    public void setDate(String date) {
        this.Date = date;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        this.Description = description;
    }
}
