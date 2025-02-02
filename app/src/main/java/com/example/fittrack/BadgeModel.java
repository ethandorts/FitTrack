package com.example.fittrack;

public class BadgeModel {
    private String badgeName;
    private int imgBadge;
    public BadgeModel(String badgeName, int imgBadge) {
        this.badgeName = badgeName;
        this.imgBadge = imgBadge;
    }

    public String getBadgeName() {
        return badgeName;
    }

    public void setBadgeName(String badgeName) {
        this.badgeName = badgeName;
    }

    public int getImgBadge() {
        return imgBadge;
    }

    public void setImgBadge(int imgBadge) {
        this.imgBadge = imgBadge;
    }
}
