package com.example.fittrack;

public class UserModel {
    String UserFullName;
    String UserID;
    String lastMessage;

    public UserModel(String UserFullName, String lastMessage, String UserID) {
        this.UserFullName = UserFullName;
        this.lastMessage = lastMessage;
        this.UserID = UserID;
    }

    public String getUserFullName() {
        return UserFullName;
    }

    public String getUserID() {
        return UserID;
    }

    public String getLastMessage() {
        return lastMessage;
    }
}
