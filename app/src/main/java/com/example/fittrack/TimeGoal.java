package com.example.fittrack;

import com.google.firebase.Timestamp;

public class TimeGoal  {
    private double targetTime;
    private double targetDistance;

    public TimeGoal(double targetTime, double targetDistance) {
        this.targetTime = targetTime;
        this.targetDistance = targetDistance;
    }

    public double getTargetTime() {
        return targetTime;
    }

    public void setTargetTime(double targetTime) {
        this.targetTime = targetTime;
    }

    public double getTargetDistance() {
        return targetDistance;
    }

    public void setTargetDistance(double targetDistance) {
        this.targetDistance = targetDistance;
    }
}
