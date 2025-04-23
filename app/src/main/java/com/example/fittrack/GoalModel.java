package com.example.fittrack;

import com.google.firebase.Timestamp;

public class GoalModel {
    private String goalType;
    private String status;
    private String goalDescription;
    private double currentProgress;
    private Timestamp startDate;
    private Timestamp endDate;
    private Timestamp lastUpdated;
    private int bestTime;
    private double targetTime;
    private double targetDistance;
    private double targetCalories;
    private String activityType;

    public GoalModel() {
    }

    public GoalModel(String goalType, String status, String goalDescription, double currentProgress, Timestamp startDate, Timestamp endDate, Timestamp lastUpdated, int bestTime, double targetTime, double targetDistance, double targetCalories, String activityType) {
        this.goalType = goalType;
        this.status = status;
        this.goalDescription = goalDescription;
        this.currentProgress = currentProgress;
        this.startDate = startDate;
        this.endDate = endDate;
        this.lastUpdated = lastUpdated;
        this.bestTime = bestTime;
        this.targetTime = targetTime;
        this.targetDistance = targetDistance;
        this.targetCalories = targetCalories;
        this.activityType = activityType;
    }

    public String getGoalType() {
        return goalType;
    }

    public void setGoalType(String goalType) {
        this.goalType = goalType;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public double getCurrentProgress() {
        return currentProgress;
    }

    public void setCurrentProgress(double currentProgress) {
        this.currentProgress = currentProgress;
    }

    public Timestamp getStartDate() {
        return startDate;
    }

    public void setStartDate(Timestamp startDate) {
        this.startDate = startDate;
    }

    public Timestamp getEndDate() {
        return endDate;
    }

    public void setEndDate(Timestamp endDate) {
        this.endDate = endDate;
    }

    public Timestamp getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(Timestamp lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public int getBestTime() {
        return bestTime;
    }

    public void setBestTime(int bestTime) {
        this.bestTime = bestTime;
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

    public double getTargetCalories() {
        return targetCalories;
    }

    public void setTargetCalories(double targetCalories) {
        this.targetCalories = targetCalories;
    }

    public String getGoalDescription() {
        return goalDescription;
    }

    public void setGoalDescription(String goalDescription) {
        this.goalDescription = goalDescription;
    }

    public String getActivityType() {
        return activityType;
    }

    public void setActivityType(String activityType) {
        this.activityType = activityType;
    }
}
