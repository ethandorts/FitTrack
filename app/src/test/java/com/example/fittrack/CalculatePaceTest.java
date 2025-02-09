package com.example.fittrack;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class CalculatePaceTest {
    @Test
    public void calculateActivitySpeed() {
        double speed = ActivityStatsConversionUtil.calculateAverageSpeed(3456, 1490);
        assertEquals(5.19, speed, 0.01);

        double speed2 = ActivityStatsConversionUtil.calculateAverageSpeed(10000, 3600);
        assertEquals(6.21, speed2, 0.01);

        double speed3 = ActivityStatsConversionUtil.calculateAverageSpeed(500, 300);
        assertEquals(3.73, speed3, 0.01);

        double speed4 = ActivityStatsConversionUtil.calculateAverageSpeed(0, 1500);
        assertEquals(0.0, speed4, 0.01);

        double speed5 = ActivityStatsConversionUtil.calculateAverageSpeed(2000, 100);
        assertEquals(44.74, speed5, 0.01);

        double speed6 = ActivityStatsConversionUtil.calculateAverageSpeed(1, 3600);
        assertEquals(0.00062, speed6, 0.00001);
    }

    @Test
    public void testCalculateActivitySpeedZeroDistance() {
        double speed = ActivityStatsConversionUtil.calculateAverageSpeed(0, 1490);
        assertEquals(0.0, speed, 0.01);
    }

    @Test
    public void testCalculateActivitySpeed_ZeroDistanceAndTime() {
        double speed = ActivityStatsConversionUtil.calculateAverageSpeed(0, 0);
        assertEquals(0.0, speed, 0.01);
    }
}
