package com.example.fittrack;

public class PersonalRecord {
    private String title;
    private String time;
    private String date;
    private int icon;

    public PersonalRecord(String title, String time, String date, int icon) {
        this.title = title;
        this.time = time;
        this.date = date;
        this.icon = icon;
    }

    public String getTitle() {
        return title;
    }

    public String getTime() {
        return time;
    }

    public String getDate() {
        return date;
    }

    public int getIcon() {
        return icon;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setIcon(int iconResId) {
        this.icon = iconResId;
    }
}
