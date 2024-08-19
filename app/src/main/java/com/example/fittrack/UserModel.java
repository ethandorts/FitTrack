package com.example.fittrack;

import java.io.Serializable;

public class UserModel {
    String UserFullName;
    String UserLastMessage;

    public UserModel(String UserFullName, String UserLastMessage) {
        this.UserFullName = UserFullName;
        this.UserLastMessage = UserLastMessage;
    }

    public String getUserFullName() {
        return UserFullName;
    }

    public String getUserLastMessage() {
        return UserLastMessage;
    }
}
