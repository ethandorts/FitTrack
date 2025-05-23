package com.example.fittrack;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import android.content.Context;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;

import java.util.Arrays;
import java.util.List;

@RunWith(JUnit4.class)
public class CalculateElevationTest {
    private ElevationCalculator elevationCalculator;
    private List<Double> elevationValues = Arrays.asList(190.3, 25.3, 152.4, 295.6, 76.2, 210.7,
            145.0, 10.5, 275.8, 48.7, 120.8, 260.4,
            95.4, 225.9, 240.1, 175.6);
    @Before
    public void setUp() {
        elevationCalculator = new ElevationCalculator();
    }

    @Test
    public void testCorrectMaximumElevation() {
        double maxValue = elevationCalculator.getMaxElevation(elevationValues);
        assertEquals(295.6, maxValue, 0.001);
    }

    @Test
    public void testCorrectMinimumElevation() {
        double minValue = elevationCalculator.getMinElevation(elevationValues);
        assertEquals(10.5, minValue, 0.001);
    }

    @Test
    public void testCorrectElevationLoss() {
        double elevationLoss = elevationCalculator.calculateElevationLoss(elevationValues);
        assertEquals(1041.2, elevationLoss, 0.001);
    }

    @Test
    public void testAllAscendingElevations() {
        List<Double> ascendingElevations = Arrays.asList(100.0, 150.0, 200.0, 250.0, 300.0);
        double gain = elevationCalculator.calculateElevationGain(ascendingElevations);
        double loss = elevationCalculator.calculateElevationLoss(ascendingElevations);

        assertEquals(200.0, gain, 0.001);
        assertEquals(0.0, loss, 0.001);
    }

    @Test
    public void testAllDescendingElevations() {
        List<Double> descendingElevations = Arrays.asList(300.0, 250.0, 200.0, 150.0, 100.0);
        double gain = elevationCalculator.calculateElevationGain(descendingElevations);
        double loss = elevationCalculator.calculateElevationLoss(descendingElevations);

        assertEquals(0.0, gain, 0.001);
        assertEquals(200.0, loss, 0.001);
    }

    @Test
    public void testEmptyElevationList() {
        List<Double> emptyList = Arrays.asList();

        assertEquals(0.0, elevationCalculator.getMaxElevation(emptyList), 0.001);
        assertEquals(0.0, elevationCalculator.getMinElevation(emptyList), 0.001);
        assertEquals(0.0, elevationCalculator.calculateElevationGain(emptyList), 0.001);
        assertEquals(0.0, elevationCalculator.calculateElevationLoss(emptyList), 0.001);
    }

    @Test
    public void testNullElevationList() {
        assertEquals(0.0, elevationCalculator.calculateElevationGain(null), 0.001);
        assertEquals(0.0, elevationCalculator.calculateElevationLoss(null), 0.001);
        assertEquals(0.0, elevationCalculator.getMaxElevation(null), 0.001);
        assertEquals(0.0, elevationCalculator.getMinElevation(null), 0.001);
    }

    @Test
    public void testNoElevationChange() {
        List<Double> constantElevations = Arrays.asList(100.0, 100.0, 100.0, 100.0);
        double gain = elevationCalculator.calculateElevationGain(constantElevations);
        double loss = elevationCalculator.calculateElevationLoss(constantElevations);

        assertEquals(0.0, gain, 0.001);
        assertEquals(0.0, loss, 0.001);
    }

    @Test
    public void testSingleValueElevation() {
        List<Double> singleElevation = Arrays.asList(150.0);
        assertEquals(150.0, elevationCalculator.getMaxElevation(singleElevation), 0.001);
        assertEquals(150.0, elevationCalculator.getMinElevation(singleElevation), 0.001);
        assertEquals(0.0, elevationCalculator.calculateElevationGain(singleElevation), 0.001);
        assertEquals(0.0, elevationCalculator.calculateElevationLoss(singleElevation), 0.001);
    }

    @Test
    public void testOscillatingElevations() {
        List<Double> elevations = Arrays.asList(100.0, 150.0, 120.0, 180.0, 160.0);
        double gain = elevationCalculator.calculateElevationGain(elevations);
        double loss = elevationCalculator.calculateElevationLoss(elevations);

        assertEquals(110.0, gain, 0.001);
        assertEquals(50.0, loss, 0.001);
    }

    @Test
    public void testHighPrecisionElevations() {
        List<Double> elevations = Arrays.asList(100.12345, 100.54321, 100.98765);
        double gain = elevationCalculator.calculateElevationGain(elevations);

        assertEquals(0.8642, gain, 0.01);
    }


}
