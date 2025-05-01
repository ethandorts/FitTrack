package com.example.fittrack;

import com.google.firebase.Timestamp;

public class ActivityTimeRecord {
    private long time;
    private String label;
    private Timestamp date;

    public ActivityTimeRecord(long time, String label, Timestamp date) {
        this.time = time;
        this.label = label;
        this.date = date;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public Timestamp getDate() {
        return date;
    }

    public void setDate(Timestamp date) {
        this.date = date;
    }
}
