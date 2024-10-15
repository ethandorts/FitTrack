package com.example.fittrack;

import com.google.firebase.firestore.PropertyName;

public class FoodModel {
    @PropertyName("foodName")
    private String foodName;
    @PropertyName("calories")
    private double calories;
    @PropertyName("mealType")
    private String mealType;
    @PropertyName("quantity")
    private int quantity;

    public FoodModel(String foodName, double calories, String mealType, int quantity) {
        this.foodName = foodName;
        this.calories = calories;
        this.mealType = mealType;
        this.quantity = quantity;
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

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
