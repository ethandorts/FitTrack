package com.example.fittrack;

import com.google.firebase.Timestamp;

import java.util.List;

public class ActivityModel {
    private String activityType;
    private String activityTypeImage;
    private Timestamp activityDate;
    private String activityDistance;
    private String activityTime;
    private String activityPace;
    private String activityUser;
    private String activityUserImage;
    private List<Object> activityCoordinates;
    private String ActivityID;

    public ActivityModel(String activityType, String activityTypeImage, Timestamp activityDate, String activityDistance, String activityTime, String activityPace, String activityUser, String activityUserImage, List<Object> activityCoordinates, String activityID) {
        this.activityType = activityType;
        this.activityTypeImage = activityTypeImage;
        this.activityDate = activityDate;
        this.activityDistance = activityDistance;
        this.activityTime = activityTime;
        this.activityPace = activityPace;
        this.activityUser = activityUser;
        this.activityUserImage = activityUserImage;
        this.activityCoordinates = activityCoordinates;
        ActivityID = activityID;
    }

    public String getActivityType() {
        return activityType;
    }

    public String getActivityTypeImage() {
        return activityTypeImage;
    }

    public Timestamp getActivityDate() {
        return activityDate;
    }

    public String getActivityDistance() {
        return activityDistance;
    }

    public String getActivityTime() {
        return activityTime;
    }

    public String getActivityPace() {
        return activityPace;
    }

    public String getActivityUser() {
        return activityUser;
    }

    public String getActivityUserImage() {
        return activityUserImage;
    }
    public List<Object> getActivityCoordinates() {
        return activityCoordinates;
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

        return activityDate.equals(that.activityDate) &&
                activityType.equals(that.activityType) &&
                activityDistance.equals(that.activityDistance) &&
                activityTime.equals(that.activityTime);
    }

    @Override
    public int hashCode() {
        int result = activityType.hashCode();
        result = 31 * result + activityDate.hashCode();
        result = 31 * result + activityDistance.hashCode();
        result = 31 * result + activityTime.hashCode();
        return result;
    }

}
