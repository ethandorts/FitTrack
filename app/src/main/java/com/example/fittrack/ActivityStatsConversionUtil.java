package com.example.fittrack;

public class ActivityStatsConversionUtil {
    public static double calculateAverageSpeed(double distance, int time) {
        if(time == 0) {
            return 0;
        }
        return Math.round(((distance * 2.23694) / time) * 100.0 / 100.0);
    }

}
