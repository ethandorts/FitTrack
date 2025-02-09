package com.example.fittrack;

public class ActivityStatsConversionUtil {
    public static double calculateAverageSpeed(double distance, int time) {
        if(time == 0) {
            return 0;
        }
        return (distance * 2.23694) / time;
    }

}
