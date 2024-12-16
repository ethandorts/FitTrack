package com.example.fittrack;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.PropertyName;

public class EventModel {
    @PropertyName("ActivityType")
    private String ActivityType;
    @PropertyName("DateTime")
    private String DateTime;
    @PropertyName("Description")
    private String Description;
    @PropertyName("EventName")
    private String EventName;
    @PropertyName("Completed")
    private String Completed;

    public EventModel(String activityType, String dateTime, String description, String eventName, String isCompleted) {
        ActivityType = activityType;
        DateTime = dateTime;
        Description = description;
        EventName = eventName;
        Completed = isCompleted;
    }

    public EventModel() {}

    public String getActivityType() {
        return ActivityType;
    }

    public void setActivityType(String activityType) {
        ActivityType = activityType;
    }

    public String getDateTime() {
        return DateTime;
    }

    public void setDateTime(String dateTime) {
        DateTime = dateTime;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public String getEventName() {
        return EventName;
    }

    public void setEventName(String eventName) {
        EventName = eventName;
    }

    public String getCompleted() {
        return Completed;
    }

    public void setCompleted(String completed) {
        Completed = completed;
    }
}
