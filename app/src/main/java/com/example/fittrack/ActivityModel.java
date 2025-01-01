package com.example.fittrack;

import com.google.firebase.Timestamp;

import java.util.List;

public class ActivityModel {
    private String type;
    private String activityTypeImage;
    private Timestamp date;
    private String distance;
    private double time;
    private String pace;
    private String UserID;
    private String activityUserImage;
    private List<Object> activityCoordinates;
    private String ActivityID;

    public ActivityModel(String type, String activityTypeImage, Timestamp date, String distance, double time, String pace, String userID, String activityUserImage, List<Object> activityCoordinates, String activityID) {
        this.type = type;
        this.activityTypeImage = activityTypeImage;
        this.date = date;
        this.distance = distance;
        this.time = time;
        this.pace = pace;
        UserID = userID;
        this.activityUserImage = activityUserImage;
        this.activityCoordinates = activityCoordinates;
        ActivityID = activityID;
    }

    public ActivityModel() {

    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getActivityTypeImage() {
        return activityTypeImage;
    }

    public void setActivityTypeImage(String activityTypeImage) {
        this.activityTypeImage = activityTypeImage;
    }

    public Timestamp getDate() {
        return date;
    }

    public void setDate(Timestamp date) {
        this.date = date;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public double getTime() {
        return time;
    }

    public void setTime(double time) {
        this.time = time;
    }

    public String getPace() {
        return pace;
    }

    public void setPace(String pace) {
        this.pace = pace;
    }

    public String getUserID() {
        return UserID;
    }

    public void setUserID(String userID) {
        UserID = userID;
    }

    public String getActivityUserImage() {
        return activityUserImage;
    }

    public void setActivityUserImage(String activityUserImage) {
        this.activityUserImage = activityUserImage;
    }

    public List<Object> getActivityCoordinates() {
        return activityCoordinates;
    }

    public void setActivityCoordinates(List<Object> activityCoordinates) {
        this.activityCoordinates = activityCoordinates;
    }

    public String getActivityID() {
        return ActivityID;
    }

    public void setActivityID(String activityID) {
        ActivityID = activityID;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ActivityModel that = (ActivityModel) o;

        return date.equals(that.date) &&
                type.equals(that.type) &&
                distance.equals(that.distance);
    }

    @Override
    public int hashCode() {
        int result = type.hashCode();
        result = 31 * result + date.hashCode();
        result = 31 * result + distance.hashCode();
        return result;
    }

}
