package com.example.fittrack;

import com.google.firebase.Timestamp;

public class MeetupModel {
    private String MeetupID;
    private String Title;
    private String User;
    private Timestamp Date;
    private String Location;
    private String Description;
    private String Status;

    public MeetupModel(String meetupID, String title, String user, Timestamp date, String location, String description, String status) {
        MeetupID = meetupID;
        Title = title;
        User = user;
        Date = date;
        Location = location;
        Description = description;
        Status = status;
    }

    public MeetupModel() {

    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public String getUser() {
        return User;
    }

    public void setUser(String user) {
        User = user;
    }

    public Timestamp getDate() {
        return Date;
    }

    public void setDate(Timestamp date) {
        Date = date;
    }

    public String getLocation() {
        return Location;
    }

    public void setLocation(String location) {
        Location = location;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }

    public String getMeetupID() {
        return MeetupID;
    }

    public void setMeetupID(String meetupID) {
        this.MeetupID = meetupID;
    }
}
