package com.example.projek.Model;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class Flavonoid implements Serializable {
    @SerializedName("name")
    private String name;
    @SerializedName("amount")
    private Double amount;
    @SerializedName("unit")
    private String unit;

    // Constructor, Getters, Setters
    public Flavonoid(String name, Double amount, String unit) {
        this.name = name;
        this.amount = amount;
        this.unit = unit;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public Double getAmount() { return amount; }
    public void setAmount(Double amount) { this.amount = amount; } // <--- Ini yang benar
    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }
}