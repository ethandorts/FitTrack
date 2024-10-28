package com.example.fittrack;

public class Interpolator {
    public Interpolator() {

    }

    public int interpolatorCalculator(int time1, int distance1, int time2, int distance2, int KmMilestone) {
        int time = time1 + ((KmMilestone - distance1) * (time2 - time1)) / (distance2 - distance1);
        return time;
    }
}
