package com.example.fittrack;

public class LeaderboardModel {
    private String username;
    private double distance;
    private String ActivityID;

    public LeaderboardModel(String username, double distance, String ActivityID) {
        this.username = username;
        this.distance = distance;
        this.ActivityID = ActivityID;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public String getActivityID() {
        return ActivityID;
    }

    public void setActivityID(String activityID) {
        ActivityID = activityID;
    }
}
