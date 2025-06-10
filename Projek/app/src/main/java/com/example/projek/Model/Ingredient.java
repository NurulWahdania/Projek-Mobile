package com.example.projek.Model;


import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class Ingredient implements Serializable {
    @SerializedName("id")
    private Integer id;
    @SerializedName("name")
    private String name;
    @SerializedName("amount")
    private Double amount;
    @SerializedName("unit")
    private String unit;
    @SerializedName("nutrients")
    private List<Nutrient> nutrients;

    // Constructor, Getters, and Setters
    public Ingredient(Integer id, String name, Double amount, String unit, List<Nutrient> nutrients) {
        this.id = id;
        this.name = name;
        this.amount = amount;
        this.unit = unit;
        this.nutrients = nutrients;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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

    public List<Nutrient> getNutrients() {
        return nutrients;
    }

    public void setNutrients(List<Nutrient> nutrients) {
        this.nutrients = nutrients;
    }
}