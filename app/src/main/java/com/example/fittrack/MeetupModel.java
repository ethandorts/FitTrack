package com.example.fittrack;

import com.google.firebase.Timestamp;

import java.util.ArrayList;
import java.util.List;

public class MeetupModel {
    private String MeetupID;
    private String Title;
    private String User;
    private Timestamp Date;
    private String Location;
    private String Description;
    private String Status;
    private ArrayList<String> Accepted;
    private ArrayList<String> Rejected;

    public MeetupModel(String meetupID, String title, String user, Timestamp date, String location, String description, String status, ArrayList<String> accepted, ArrayList<String> rejected) {
        MeetupID = meetupID;
        Title = title;
        User = user;
        Date = date;
        Location = location;
        Description = description;
        Status = status;
        Accepted = accepted;
        Rejected = rejected;
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

    public ArrayList<String> getAccepted() {
        return Accepted;
    }

    public void setAccepted(ArrayList<String> accepted) {
        Accepted = accepted;
    }

    public void setRejected(ArrayList<String> rejected) {
        Rejected = rejected;
    }

    public ArrayList<String> getRejected() {
        return Rejected;
    }
}
