package com.example.fittrack;

import android.widget.ImageView;

public class RequestModel {
    private String userName;
    private ImageView userProfileImage;

    public RequestModel(String userName, ImageView userProfileImage) {
        this.userName = userName;
        this.userProfileImage = userProfileImage;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public ImageView getUserProfileImage() {
        return userProfileImage;
    }

    public void setUserProfileImage(ImageView userProfileImage) {
        this.userProfileImage = userProfileImage;
    }
}
