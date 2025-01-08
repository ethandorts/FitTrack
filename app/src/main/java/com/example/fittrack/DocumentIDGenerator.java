package com.example.fittrack;

import java.security.SecureRandom;
import java.util.Random;

public class DocumentIDGenerator {

    public static String GenerateActivityID() {
        String allowedCharacters = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ123456789";
        SecureRandom random = new SecureRandom();
        StringBuilder generatedActivityID = new StringBuilder();
        for(int i =0; i < 16; i++) {
            int number = random.nextInt(allowedCharacters.length());
            generatedActivityID.append(allowedCharacters.charAt(number));
        }
        return generatedActivityID.toString();
    }
}
