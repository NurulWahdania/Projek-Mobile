package com.example.projek.Model;

import java.io.Serializable;

public class ConsumedFoodItem implements Serializable {

    private int consumedId;
    private int recipeId;
    private String title;
    private double calories;
    private String date;
    private double quantity;
    private String servingSize;

    public ConsumedFoodItem(int consumedId, int recipeId, String title, double calories, String date, double quantity, String servingSize) {
        this.consumedId = consumedId;
        this.recipeId = recipeId;
        this.title = title;
        this.calories = calories;
        this.date = date;
        this.quantity = quantity;
        this.servingSize = servingSize;
    }

    public ConsumedFoodItem(int recipeId, String title, double calories, String date, double quantity, String servingSize) {
        this.recipeId = recipeId;
        this.title = title;
        this.calories = calories;
        this.date = date;
        this.quantity = quantity;
        this.servingSize = servingSize;
    }

    public int getConsumedId() { return consumedId; }
    public void setConsumedId(int consumedId) { this.consumedId = consumedId; }

    public int getRecipeId() { return recipeId; }
    public void setRecipeId(int recipeId) { this.recipeId = recipeId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public double getCalories() { return calories; }
    public void setCalories(double calories) { this.calories = calories; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public double getQuantity() { return quantity; }
    public void setQuantity(double quantity) { this.quantity = quantity; }

    public String getServingSize() { return servingSize; }
    public void setServingSize(String servingSize) { this.servingSize = servingSize; }
}