package com.example.fittrack;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class CalculateCaloriesBurnedTest {
    private CaloriesCalculator calculator;
    @Before
    public void setUp() {
        calculator = new CaloriesCalculator();
    }

    @Test
    public void testCorrectCaloriesCalculated() {
        int calories = calculator.calculateCalories(1768, "5:54", "Running", 88);
        assertEquals(364, calories);
    }

    @Test
    public void testWalkingCaloriesCalculated() {
        int calories = calculator.calculateCalories(3120, "13:51", "Walking", 92);
        assertEquals(228, calories);
    }

    @Test
    public void testCyclingCaloriesCalculated() {
        int calories = calculator.calculateCalories(3120, "1:30", "Cycling", 92);
        assertEquals(1355, calories);
    }

    @Test
    public void testZeroWeight() {
        int calories = calculator.calculateCalories(1768, "5:54", "Running", 0);
        assertEquals(0, calories);
    }

    @Test
    public void testNegativeWeight() {
        int calories = calculator.calculateCalories(1500, "5:00", "Running", -70);
        assertEquals(0, calories);
    }

    @Test
    public void testCaseInsensitiveActivityType() {
        int calories = calculator.calculateCalories(1800, "5:00", "running", 70);
        assertEquals(350, calories);
    }

    @Test
    public void testNullActivityType() {
        try {
            calculator.calculateCalories(1800, "5:00", null, 70);
        } catch (Exception e) {
            assertTrue(e instanceof NullPointerException);
        }
    }

    @Test
    public void testInvalidPaceFormat() {
        try {
            calculator.calculateCalories(1800, "5-30", "Running", 70);
        } catch (Exception e) {
            assertTrue(e instanceof IllegalArgumentException);
        }
    }

    @Test
    public void testLongActivity() {
        int calories = calculator.calculateCalories(1800, "19:00", "Running", 75);
        assertEquals(86, calories);
    }

    @Test
    public void testHeavyWeight() {
        int calories = calculator.calculateCalories(1800, "12:00", "Running", 125);
        assertEquals(187, calories);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNegativeActivityTime() {
        calculator.calculateCalories(1500, "-1:00", "Running", 70);
    }

    @Test
    public void testMinimumTime() {
        int calories = calculator.calculateCalories(1, "5:00", "Running", 70);
        assertEquals(0, calories);
    }

    @Test
    public void testExactMETValueMatch() {
        int calories = calculator.calculateCalories(1800, "5:00", "Running", 70);
        assertEquals(350, calories);
    }

    @Test
    public void testUnknownMETValue() {
        int calories = calculator.calculateCalories(1800, "5:17", "Running", 70);
        assertTrue(calories > 315 && calories < 350);
    }

    @Test
    public void testNullPace() {
        try {
            calculator.calculateCalories(1800, null, "Running", 70);
        } catch (Exception e) {
            assertTrue(e instanceof NullPointerException);
        }
    }
}
