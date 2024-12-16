package com.example.fittrack;

import java.util.HashMap;
import java.util.Map;

public class CaloriesCalculator {
    private static final Map<String, Double> Run_MET_Values = new HashMap<>();
    private static final Map<String, Double> Walking_MET_Values = new HashMap<>();
    private static final Map<String, Double> Cycling_MET_Values = new HashMap<>();

    static {
        Run_MET_Values.put("3:00", 19.0);
        Run_MET_Values.put("3:30", 16.0);
        Run_MET_Values.put("4:00", 15.0);
        Run_MET_Values.put("4:30", 13.5);
        Run_MET_Values.put("5:00", 12.8);
        Run_MET_Values.put("5:30", 12.0);
        Run_MET_Values.put("6:00", 11.5);
        Run_MET_Values.put("6:30", 11.0);
        Run_MET_Values.put("7:00", 10.5);
        Run_MET_Values.put("7:30", 9.8);
        Run_MET_Values.put("8:00", 9.0);
        Run_MET_Values.put("9:00", 8.3);
        Run_MET_Values.put("10:00", 6.0);

        Walking_MET_Values.put("6:00", 7.0);
        Walking_MET_Values.put("7:00", 6.3);
        Walking_MET_Values.put("8:00", 5.0);
        Walking_MET_Values.put("9:00", 4.3);
        Walking_MET_Values.put("10:00", 3.8);
        Walking_MET_Values.put("12:00", 3.3);
        Walking_MET_Values.put("13:00", 2.8);
        Walking_MET_Values.put("15:00", 2.0);

        Cycling_MET_Values.put("20:00", 4.0);
        Cycling_MET_Values.put("15:00", 6.8);
        Cycling_MET_Values.put("10:00", 8.0);
        Cycling_MET_Values.put("5:00", 10.0);
    }
    public int calculateCalories(double time, String runPace, int weight) {
        String pace = roundToNearestHalfMinute(runPace);
        double MET = getMETforRunningPace(pace);
        return (int) (time * MET * weight) / 12000;
    }

    private double getMETforRunningPace(String roundedPace) {

        if(secondsConversion(roundedPace) < secondsConversion("3:00")) {
            return Run_MET_Values.getOrDefault("3:00", 19.0);
        }

        if(secondsConversion(roundedPace) > secondsConversion("10:00")) {
            return Run_MET_Values.getOrDefault("10:00", 6.0);
        }
        return Run_MET_Values.getOrDefault(roundedPace, 9.8);
    }

    public static String roundToNearestHalfMinute(String pace) {
        String[] pace_split = pace.split(":");
        int minutes = Integer.parseInt(pace_split[0]);
        int seconds = Integer.parseInt(pace_split[1]);
        double minutesSeconds = (minutes + seconds) / 60;
        double roundedResult = Math.round(minutesSeconds * 2) / 2.0;

        int roundedMinutes = (int) roundedResult;
        int roundedSeconds = (int) ((roundedResult - roundedMinutes) * 60);

        return String.format("%d:%02d", roundedMinutes, roundedSeconds);
    }

    private static int secondsConversion(String pace) {
        String[] pace_split = pace.split(":");
        int minutes = Integer.parseInt(pace_split[0]);
        int seconds = Integer.parseInt(pace_split[1]);
        return (minutes * 60) + seconds;
    }
}
