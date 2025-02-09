package com.example.fittrack;

import java.util.Collections;
import java.util.List;

public class ElevationCalculator {
    public ElevationCalculator() {

    }

    public double calculateElevationGain(List<Double> elevationValues) {
        if(elevationValues == null || elevationValues.size() < 2) {
            return 0.0;
        }
        double elevationGain = 0;
        for(int i = 1; i <elevationValues.size(); i++) {
            double elevationChange = elevationValues.get(i) - elevationValues.get(i - 1);
            if(elevationChange > 0) {
                elevationGain += elevationChange;
            }
        }
        return Math.round(elevationGain * 100.0) / 100.0;
    }

    public double calculateElevationLoss(List<Double> elevationValues) {
        if(elevationValues == null || elevationValues.size() < 2) {
            return 0.0;
        }

        double elevationLoss = 0;
        for(int i = 1; i < elevationValues.size(); i++) {
            double elevationChange = elevationValues.get(i) - elevationValues.get(i - 1);
            if(elevationChange < 0) {
                elevationLoss += Math.abs(elevationChange);
            }
        }
        return Math.round(elevationLoss * 100.0) / 100.0;
    }

    public double getMinElevation(List<Double> elevationValues) {
        return Collections.min(elevationValues);
    }

    public double getMaxElevation(List<Double> elevationDouble) {
        return Collections.max(elevationDouble);
    }

}
