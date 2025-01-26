package com.example.fittrack;

public class LikeModel {
    private String likeName;
    private int imgLike;

    public LikeModel(String likeName, int imgLike) {
        this.likeName = likeName;
        this.imgLike = imgLike;
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
}
