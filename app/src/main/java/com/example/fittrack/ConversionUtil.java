package com.example.fittrack;

import com.google.firebase.Timestamp;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class ConversionUtil {
    public ConversionUtil() {

    }

    public static String kmToMeters(double km) {
        double meters = km * 1000;
        DecimalFormat df = new DecimalFormat("0.00");
        return df.format(meters);
    }

    public static String capitaliseFoodName(String foodName) {
        if(foodName == null || foodName.isEmpty()) {
            return foodName;
        }

        String [] splitFoodName = foodName.toLowerCase().split("\\s+");
        StringBuilder formattedWord = new StringBuilder();
        for(String word : splitFoodName) {
            if(!word.isEmpty()) {
                formattedWord.append(Character.toUpperCase(word.charAt(0)))
                        .append(word.substring(1))
                        .append(" ");
            }
        }
        return formattedWord.toString().trim();
    }

    public static String dateFormatter(Timestamp timestamp) {
        Date date = timestamp.toDate();
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        String formattedDate = formatter.format(date);

        return formattedDate;
    }

    public static String longToTimeConversion(long longValue) {
        long milliseconds = longValue % 1000;
        longValue = longValue / 1000;
        long hours = longValue / 3600;
        long minutes = (longValue % 3600) / 60;
        long seconds = longValue % 60;

        if (hours > 0) {
            return String.format("%02d:%02d:%02d.%d", hours, minutes, seconds / 100);
        } else {
            return String.format("%02d:%02d.%d", minutes, seconds, milliseconds / 100);
        }
    }

    public static int convertLongtoSeconds(long milliseconds) {
        int seconds = (int) Math.round(milliseconds / 1000.0);
        return seconds;
    }

    public static int convertPacetoSeconds(String pace) {
        String [] paceParts = pace.split(":");
        int minutes = Integer.parseInt(paceParts[0]);
        int seconds = Integer.parseInt(paceParts[1]);
        return minutes * 60 + seconds;
    }

    public static int convertTimeToSeconds(String time) {
        String[] parts = time.split(":");
        int minutes = Integer.parseInt(parts[0]);
        int seconds = Integer.parseInt(parts[1]);
        return minutes * 60 + seconds;
    }

    public static double convertLongToMPH(long millisecondsperKM) {
        Double seconds = millisecondsperKM / 1000.0;
        double miles = 1 * 0.621371;
        double milesPerSecond = miles / seconds;
        double mph = milesPerSecond * 3600;

        return mph;
    }

    public static String convertSecondstoPace(int paceSeconds) {
        int minutes = paceSeconds / 60;
        int seconds = paceSeconds % 60;
        return String.format("%d:%02d", minutes, seconds);
    }

    public static String convertSecondsToTime(int inputSeconds) {
        int minutes = inputSeconds / 60;
        int seconds = inputSeconds % 60;
        return minutes + ":" + (seconds < 10 ? "0" : "") + seconds;
    }

    public static Timestamp StringtoTimeStamp(String DateOfBirth) {
        Timestamp convertedDOB = null;
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
            Date date = dateFormat.parse(DateOfBirth);
            convertedDOB = new Timestamp(date);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return convertedDOB;
    }

    public static String TimestamptoString(Timestamp time) {
        LocalDateTime date = LocalDateTime.ofInstant(Instant.ofEpochSecond(time.getSeconds()), ZoneId.systemDefault());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");

        return date.format(formatter);
    }

    public static String AltTimestamptoString(Timestamp timestamp) {
        if (timestamp == null) return "";

        Date date = timestamp.toDate();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        return sdf.format(date);
    }

    public static String convertToMinutesPerKM(float metresPerSecondSpeed) {
        if(metresPerSecondSpeed > 0) {
            float minutesPerKMPace = (1000 / metresPerSecondSpeed) / 60;

            int minutes = (int) minutesPerKMPace;
            int seconds = (int) ((minutesPerKMPace - minutes) * 60);

           return String.format("%d:%02d /km", minutes, seconds);
        } else {
            return "00:00 min/km";
        }
    }

    public static String convertMetersToKilometers(double meters) {
        double km = meters / 1000;
        return String.format("%.2f km", km);
    }

}
