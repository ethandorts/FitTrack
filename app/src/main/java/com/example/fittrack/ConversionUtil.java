package com.example.fittrack;

import com.google.firebase.Timestamp;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ConversionUtil {
    public ConversionUtil() {

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
}
