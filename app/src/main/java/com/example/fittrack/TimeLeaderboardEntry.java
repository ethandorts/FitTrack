package com.example.fittrack;

public class TimeLeaderboardEntry {
    private String UserID;
    private int timeSeconds;

    public TimeLeaderboardEntry(String userID, int timeSeconds) {
        UserID = userID;
        this.timeSeconds = timeSeconds;
    }

    public String getUserID() {
        return UserID;
    }

    public void setUserID(String userID) {
        UserID = userID;
    }

    public int getTimeSeconds() {
        return timeSeconds;
    }

    public void setTimeSeconds(int timeSeconds) {
        this.timeSeconds = timeSeconds;
    }
}
