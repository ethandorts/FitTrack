package com.example.fittrack;

import java.util.HashMap;
import java.util.Map;

public class CaloriesCalculator {
    private static final Map<String, Double> Run_MET_Values = new HashMap<>();
    private static final Map<String, Double> Walking_MET_Values = new HashMap<>();
    private static final Map<String, Double> Cycling_MET_Values = new HashMap<>();

    // MET values based on research from Compendium of Physical Activities.
    // Quantifying Physical Activity Energy Expenditure.

    static {
        Run_MET_Values.put("3:00", 23.0);
        Run_MET_Values.put("3:15", 19.8);
        Run_MET_Values.put("3:45", 16.0);
        Run_MET_Values.put("4:10", 12.8);
        Run_MET_Values.put("4:20", 11.8);
        Run_MET_Values.put("4:40", 11.0);
        Run_MET_Values.put("5:00", 10.0);
        Run_MET_Values.put("5:30", 9.0);
        Run_MET_Values.put("6:00", 8.3);
        Run_MET_Values.put("6:30", 7.0);
        Run_MET_Values.put("7:00", 6.0);
        Run_MET_Values.put("7:30", 5.3);
        Run_MET_Values.put("8:00", 5.0);
        Run_MET_Values.put("9:00", 4.3);
        Run_MET_Values.put("10:00", 3.8);
        Run_MET_Values.put("11:00", 3.5);
        Run_MET_Values.put("12:00", 3.0);
        Run_MET_Values.put("13:00", 2.8);
        Run_MET_Values.put("14:00", 2.5);
        Run_MET_Values.put("15:00", 2.3);

        Walking_MET_Values.put("7:00", 6.3);
        Walking_MET_Values.put("7:30", 6.0);
        Walking_MET_Values.put("8:00", 5.8);
        Walking_MET_Values.put("8:30", 5.5);
        Walking_MET_Values.put("9:00", 5.3);
        Walking_MET_Values.put("9:30", 5.0);
        Walking_MET_Values.put("10:00", 4.8);
        Walking_MET_Values.put("10:30", 4.5);
        Walking_MET_Values.put("11:00", 4.3);
        Walking_MET_Values.put("11:30", 4.0);
        Walking_MET_Values.put("12:00", 3.8);
        Walking_MET_Values.put("12:30", 3.5);
        Walking_MET_Values.put("13:00", 3.3);
        Walking_MET_Values.put("13:30", 3.0);
        Walking_MET_Values.put("14:00", 2.8);
        Walking_MET_Values.put("14:30", 2.5);
        Walking_MET_Values.put("15:00", 2.3);
        Walking_MET_Values.put("15:30", 2.2);
        Walking_MET_Values.put("16:00", 2.0);
        Walking_MET_Values.put("16:30", 1.9);
        Walking_MET_Values.put("17:00", 1.8);
        Walking_MET_Values.put("17:30", 1.7);
        Walking_MET_Values.put("18:00", 1.6);
        Walking_MET_Values.put("18:30", 1.5);
        Walking_MET_Values.put("19:00", 1.4);
        Walking_MET_Values.put("19:30", 1.3);
        Walking_MET_Values.put("20:00", 1.2);

        Cycling_MET_Values.put("0:30", 20.0);
        Cycling_MET_Values.put("1:00", 18.0);
        Cycling_MET_Values.put("1:30", 17.0);
        Cycling_MET_Values.put("2:00", 16.5);
        Cycling_MET_Values.put("2:30", 16.2);
        Cycling_MET_Values.put("3:00", 16.0);
        Cycling_MET_Values.put("3:30", 14.0);
        Cycling_MET_Values.put("4:00", 12.0);
        Cycling_MET_Values.put("4:30", 10.0);
        Cycling_MET_Values.put("5:00", 8.5);
        Cycling_MET_Values.put("5:30", 7.5);
        Cycling_MET_Values.put("6:00", 6.8);
        Cycling_MET_Values.put("6:30", 6.0);
        Cycling_MET_Values.put("7:00", 5.5);
        Cycling_MET_Values.put("7:30", 5.0);
        Cycling_MET_Values.put("8:00", 4.8);
        Cycling_MET_Values.put("8:30", 4.5);
        Cycling_MET_Values.put("9:00", 4.3);
        Cycling_MET_Values.put("9:30", 4.0);
        Cycling_MET_Values.put("10:00", 3.8);
    }

    public int calculateCalories(double seconds, String pace, String activityType, int weight) {
        if(weight < 0) {
            weight = 0;
        }

        if(seconds < 0) {
            throw new IllegalArgumentException("Activity duration must be more than 0");
        }

        Map<String, Double> MET_Map = new HashMap<>();
        switch(activityType) {
            case "Running":
                MET_Map = Run_MET_Values;
                break;
            case "Walking":
                MET_Map = Walking_MET_Values;
                break;
            case "Cycling":
                MET_Map = Cycling_MET_Values;
                break;
            default:
                MET_Map = Run_MET_Values;
        }

        double MET_VALUE = getInterpolatedMET(pace, MET_Map);
        return (int) ((seconds * MET_VALUE * weight) / 3600);
    }

    private static int secondsConversion(String pace) {
        String[] pace_split = pace.split(":");
        if (pace_split.length != 2) {
            throw new IllegalArgumentException("Invalid format for pace value. Use hh:mm!");
        }
        int minutes = Integer.parseInt(pace_split[0]);
        int seconds = Integer.parseInt(pace_split[1]);
        if (minutes < 0 || seconds < 0 || seconds >= 60) {
            throw new IllegalArgumentException("Negative value found for pace!");
        }
        return (minutes * 60) + seconds;
    }

    private double getInterpolatedMET(String pace, Map<String, Double> MET_Map) {
        Double exactMET = MET_Map.get(pace);
        if (exactMET != null) {
            return exactMET;
        }

        int seconds = secondsConversion(pace);

        String lowerBoundary = null;
        String higherBoundary = null;
        int lowerSeconds = Integer.MIN_VALUE;
        int higherSeconds = Integer.MAX_VALUE;

        for (String paceValue : MET_Map.keySet()) {
            int paceValueSeconds = secondsConversion(paceValue);

            if (paceValueSeconds < seconds && paceValueSeconds > lowerSeconds) {
                lowerSeconds = paceValueSeconds;
                lowerBoundary = paceValue;
            }

            if (paceValueSeconds > seconds && paceValueSeconds < higherSeconds) {
                higherSeconds = paceValueSeconds;
                higherBoundary = paceValue;
            }
        }

        if (lowerBoundary == null && higherBoundary != null) {
            return MET_Map.get(higherBoundary);
        }

        if (higherBoundary == null && lowerBoundary != null) {
            return MET_Map.get(lowerBoundary);
        }
        if (lowerBoundary == null || higherBoundary == null) {
            throw new IllegalArgumentException("No MET value found for the given pace: " + pace);
        }

        double MET_LOW = MET_Map.get(lowerBoundary);
        double MET_HIGH = MET_Map.get(higherBoundary);

        return MET_LOW + ((double) (seconds - lowerSeconds) / (higherSeconds - lowerSeconds)) * (MET_HIGH - MET_LOW);
    }


}
