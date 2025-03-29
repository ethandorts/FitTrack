package com.example.fittrack;

import android.net.Uri;

public class LikeModel {
    private String likeName;
    private Uri profilePicture;
    private int imgLike;

    public LikeModel(String likeName, int imgLike, Uri profilePicture) {
        this.likeName = likeName;
        this.imgLike = imgLike;
        this.profilePicture = profilePicture;
    }

    public String getLikeName() {
        return likeName;
    }

    public void setLikeName(String likeName) {
        this.likeName = likeName;
    }

    public int getImgLike() {
        return imgLike;
    }

    public void setImgLike(int imgLike) {
        this.imgLike = imgLike;
    }

    public Uri getProfilePicture() {
        return profilePicture;
    }

    public void setProfilePicture(Uri profilePicture) {
        this.profilePicture = profilePicture;
    }
}
