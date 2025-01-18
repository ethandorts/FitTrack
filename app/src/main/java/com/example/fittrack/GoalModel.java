package com.example.fittrack;

import com.google.firebase.Timestamp;

public class GoalModel {
    private String goalType;
    private String status;
    private double currentProgress;
    private Timestamp startDate;
    private Timestamp endDate;
    private Timestamp lastUpdated;
    private int bestTime;
    private double targetTime;
    private double targetDistance;
    private double targetCalories;
}
