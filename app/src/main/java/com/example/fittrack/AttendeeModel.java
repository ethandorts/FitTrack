package com.example.fittrack;


import android.net.Uri;

public class AttendeeModel {
    private String attendee;

    public AttendeeModel(String attendee) {
        this.attendee = attendee;
    }

    public String getAttendee() {
        return attendee;
    }

    public void setAttendee(String attendee) {
        this.attendee = attendee;
    }
}

