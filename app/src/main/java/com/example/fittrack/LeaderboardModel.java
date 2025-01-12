package com.example.fittrack;

public class LeaderboardModel {
    private String username;
    private double distance;

    public LeaderboardModel(String username, double distance) {
        this.username = username;
        this.distance = distance;
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
}
