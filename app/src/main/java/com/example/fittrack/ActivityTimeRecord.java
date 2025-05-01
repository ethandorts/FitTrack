package com.example.fittrack;

import com.google.firebase.Timestamp;

public class ActivityTimeRecord {
    long time;
    String label;
    Timestamp date;

    public ActivityTimeRecord(long time, String label, Timestamp date) {
        this.time = time;
        this.label = label;
        this.date = date;
    }
}
