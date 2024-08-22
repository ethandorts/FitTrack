package com.example.fittrack;

import java.io.Serializable;

public class UserModel {
    String UserFullName;
    String UserLastMessage;
    String UserID;

    public UserModel(String UserFullName, String UserLastMessage, String UserID) {
        this.UserFullName = UserFullName;
        this.UserLastMessage = UserLastMessage;
        this.UserID = UserID;
    }

    public String getUserFullName() {
        return UserFullName;
    }

    public String getUserLastMessage() {
        return UserLastMessage;
    }

    public String getUserID() {
        return UserID;
    }
}
