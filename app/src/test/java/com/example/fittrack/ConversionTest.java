package com.example.fittrack;

import static org.junit.Assert.assertEquals;

import com.google.firebase.Timestamp;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.Date;

@RunWith(JUnit4.class)
public class ConversionTest {
    ConversionUtil conversionUtil;

    @Test
    public void testFormatDate() {
        Timestamp timestamp = new Timestamp(1739011200L, 0);
        String date = ConversionUtil.dateFormatter(timestamp);
        assertEquals("08/02/2025", date);

        Timestamp timestamp2 = new Timestamp(1710460800L, 0);
        String date2 = ConversionUtil.dateFormatter(timestamp2);
        assertEquals("15/03/2024", date2);

        Timestamp timestamp3 = new Timestamp(1738713600L, 0);
        String date3 = ConversionUtil.dateFormatter(timestamp3);
        assertEquals("05/02/2025", date3);
    }

    @Test
    public void testCapitaliseFoodSingleWord() {
        assertEquals("Apple", ConversionUtil.capitaliseFoodName("apple"));
    }

    @Test
    public void testCapitaliseFoodMultipleWords() {
        assertEquals("Baked Beans", ConversionUtil.capitaliseFoodName("baked beans"));
    }

    @Test
    public void testCapitaliseFoodExtraSpaces() {
        assertEquals("Ham Sandwich", ConversionUtil.capitaliseFoodName("  ham   sandwich  "));
    }

    @Test
    public void testCapitaliseMixedCase() {
        assertEquals("Cheese Sandwich", ConversionUtil.capitaliseFoodName("cHEesE sandWICh"));
    }

    @Test
    public void testCapitaliseEmptyString() {
        assertEquals("", ConversionUtil.capitaliseFoodName(""));
    }

    @Test
    public void testConvertLongtoSeconds() {
        assertEquals(90, ConversionUtil.convertLongtoSeconds(90000));
        assertEquals(13200, ConversionUtil.convertLongtoSeconds(13200000));
        assertEquals(3670, ConversionUtil.convertLongtoSeconds(3670000));
        assertEquals(0, ConversionUtil.convertLongtoSeconds(0));
    }

    @Test
    public void testConvertPacetoSeconds() {
        assertEquals(360, ConversionUtil.convertPacetoSeconds("6:00"));
        assertEquals(75, ConversionUtil.convertPacetoSeconds("1:15"));
        assertEquals(630, ConversionUtil.convertPacetoSeconds("10:30"));
        assertEquals(0, ConversionUtil.convertPacetoSeconds("0:00"));
        assertEquals(90, ConversionUtil.convertPacetoSeconds("01:30"));
        assertEquals(45, ConversionUtil.convertPacetoSeconds("0:45"));
    }

    @Test(expected = NumberFormatException.class)
    public void testInvalidFormatforPace() {
        ConversionUtil.convertPacetoSeconds("invalid value");
    }

    @Test
    public void testConvertTimetoSeconds() {
        assertEquals(540, ConversionUtil.convertTimeToSeconds("9:00"));
        assertEquals(150, ConversionUtil.convertTimeToSeconds("2:30"));
        assertEquals(1451, ConversionUtil.convertTimeToSeconds("24:11"));
        assertEquals(0, ConversionUtil.convertTimeToSeconds("0:00"));
        assertEquals(1, ConversionUtil.convertTimeToSeconds("0:01"));
    }

    @Test
    public void testConvertLongToMPH() {
        assertEquals(2237.0, ConversionUtil.convertLongToMPH(1000), 0.1);     // 1 second per km
        assertEquals(223.7, ConversionUtil.convertLongToMPH(10000), 0.1);     // 10 seconds per km
        assertEquals(37.29, ConversionUtil.convertLongToMPH(60000), 0.01);
    }

    @Test
    public void testConvertSecondsToPace() {
        assertEquals("6:42", ConversionUtil.convertSecondstoPace(402));
        assertEquals("1:23", ConversionUtil.convertSecondstoPace(83));
        assertEquals("0:54", ConversionUtil.convertSecondstoPace(54));
    }

    @Test
    public void testConvertSecondsToTime() {
        assertEquals("9:00", ConversionUtil.convertSecondsToTime(540));
        assertEquals("2:30", ConversionUtil.convertSecondsToTime(150));
        assertEquals("0:21", ConversionUtil.convertSecondsToTime(21));
        assertEquals("0:00", ConversionUtil.convertSecondsToTime(0));
    }
}
