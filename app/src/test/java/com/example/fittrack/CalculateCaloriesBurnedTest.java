package com.example.fittrack;

import static org.junit.Assert.assertEquals;

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
}
