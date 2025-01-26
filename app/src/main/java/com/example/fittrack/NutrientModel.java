package com.example.fittrack;

public class NutrientModel {
    private String nutrientName;
    private double nutrientValue;

    public NutrientModel(String nutrientName, double nutrientValue) {
        this.nutrientName = nutrientName;
        this.nutrientValue = nutrientValue;
    }

    public String getNutrientName() {
        return nutrientName;
    }

    public void setNutrientName(String nutrientName) {
        this.nutrientName = nutrientName;
    }

    public double getNutrientValue() {
        return nutrientValue;
    }

    public void setNutrientValue(double nutrientValue) {
        this.nutrientValue = nutrientValue;
    }
}
