package com.example.projek.Model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Nutrient implements Serializable {
    @SerializedName("name")
    private String name;
    @SerializedName("amount")
    private Double amount;
    @SerializedName("unit")
    private String unit;
    @SerializedName("percentOfDailyNeeds")
    private Double percentOfDailyNeeds;

    // Constructor, Getters, and Setters
    public Nutrient(String name, Double amount, String unit, Double percentOfDailyNeeds) {
        this.name = name;
        this.amount = amount;
        this.unit = unit;
        this.percentOfDailyNeeds = percentOfDailyNeeds;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public Double getPercentOfDailyNeeds() {
        return percentOfDailyNeeds;
    }

    public void setPercentOfDailyNeeds(Double percentOfDailyNeeds) {
        this.percentOfDailyNeeds = percentOfDailyNeeds;
    }
}