package com.example.fittrack;

public class MeetupModel {
    private String Title;
    private String User;
    private String Date;
    private String Location;
    private String Details;
    private String Status;

    public MeetupModel(String title, String user, String date, String location, String details, String status) {
        Title = title;
        User = user;
        Date = date;
        Location = location;
        Details = details;
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

    public String getDate() {
        return Date;
    }

    public void setDate(String date) {
        Date = date;
    }

    public String getLocation() {
        return Location;
    }

    public void setLocation(String location) {
        Location = location;
    }

    public String getDetails() {
        return Details;
    }

    public void setDetails(String details) {
        Details = details;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }
}
