package com.example.fittrack;

import android.widget.ImageView;

public class MemberModel {
    private String userName;
    private ImageView profileImage;
    private boolean isAdmin;
    public MemberModel(String userName, ImageView profileImage, boolean isAdmin) {
        this.userName = userName;
        this.profileImage = profileImage;
        this.isAdmin = isAdmin;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public ImageView getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(ImageView profileImage) {
        this.profileImage = profileImage;
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    public void setAdmin(boolean admin) {
        isAdmin = admin;
    }
}
