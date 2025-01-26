package com.example.fittrack;

import com.google.firebase.firestore.PropertyName;

public class FoodModel {
    @PropertyName("foodName")
    private String foodName;
    @PropertyName("calories")
    private double calories;
    @PropertyName("mealType")
    private String mealType;
    @PropertyName("servingSize")
    private double servingSize;
    @PropertyName("servingQuantity")
    private double servingQuantity;
    @PropertyName("fat")
    private double fat;
    @PropertyName("saturated_fat")
    private double saturated_fat;
    @PropertyName("protein")
    private double protein;
    @PropertyName("sodium")
    private double sodium;
    @PropertyName("potassium")
    private double potassium;
    @PropertyName("carbs")
    private double carbs;
    @PropertyName("fiber")
    private double fiber;
    @PropertyName("sugar")
    private double sugar;
    private boolean isDetailsShown;

    public FoodModel(String foodName, double calories, String mealType, double servingSize, double servingQuantity, double fat, double saturated_fat, double protein, double sodium, double potassium, double carbs, double fiber, double sugar, boolean isDetailsShown) {
        this.foodName = foodName;
        this.calories = calories;
        this.mealType = mealType;
        this.servingSize = servingSize;
        this.servingQuantity = servingQuantity;
        this.fat = fat;
        this.saturated_fat = saturated_fat;
        this.protein = protein;
        this.sodium = sodium;
        this.potassium = potassium;
        this.carbs = carbs;
        this.fiber = fiber;
        this.sugar = sugar;
        this.isDetailsShown = isDetailsShown;
    }

    public FoodModel () {}

    public String getFoodName() {
        return foodName;
    }

    public void setFoodName(String foodName) {
        this.foodName = foodName;
    }

    public double getCalories() {
        return calories;
    }

    public void setCalories(double calories) {
        this.calories = calories;
    }

    public String getMealType() {
        return mealType;
    }

    public void setMealType(String mealType) {
        this.mealType = mealType;
    }

    public double getFat() {
        return fat;
    }

    public void setFat(double fat) {
        this.fat = fat;
    }

    public double getSaturated_fat() {
        return saturated_fat;
    }

    public void setSaturated_fat(double saturated_fat) {
        this.saturated_fat = saturated_fat;
    }

    public double getProtein() {
        return protein;
    }

    public void setProtein(double protein) {
        this.protein = protein;
    }

    public double getSodium() {
        return sodium;
    }

    public void setSodium(double sodium) {
        this.sodium = sodium;
    }

    public double getPotassium() {
        return potassium;
    }

    public void setPotassium(double potassium) {
        this.potassium = potassium;
    }

    public double getCarbs() {
        return carbs;
    }

    public void setCarbs(double carbs) {
        this.carbs = carbs;
    }

    public double getFiber() {
        return fiber;
    }

    public void setFiber(double fiber) {
        this.fiber = fiber;
    }

    public double getSugar() {
        return sugar;
    }

    public void setSugar(double sugar) {
        this.sugar = sugar;
    }

    public double getServingSize() {
        return servingSize;
    }

    public void setServingSize(double servingSize) {
        this.servingSize = servingSize;
    }

    public double getServingQuantity() {
        return servingQuantity;
    }

    public void setServingQuantity(double servingQuantity) {
        this.servingQuantity = servingQuantity;
    }

    public boolean isDetailsShown() {
        return isDetailsShown;
    }

    public void setDetailsShown(boolean detailsShown) {
        isDetailsShown = detailsShown;
    }
}
