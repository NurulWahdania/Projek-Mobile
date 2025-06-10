package com.example.projek.Model; // Pastikan package ini sesuai

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class CaloricBreakdown implements Serializable {
    @SerializedName("percentProtein")
    private Double percentProtein;
    @SerializedName("percentFat")
    private Double percentFat;
    @SerializedName("percentCarbs")
    private Double percentCarbs;

    // Constructor
    public CaloricBreakdown(Double percentProtein, Double percentFat, Double percentCarbs) {
        this.percentProtein = percentProtein;
        this.percentFat = percentFat;
        this.percentCarbs = percentCarbs;
    }

    // Getters
    public Double getPercentProtein() {
        return percentProtein;
    }

    public Double getPercentFat() {
        return percentFat;
    }

    public Double getPercentCarbs() {
        return percentCarbs;
    }

    // Setters
    public void setPercentProtein(Double percentProtein) {
        this.percentProtein = percentProtein;
    }

    public void setPercentFat(Double percentFat) {
        this.percentFat = percentFat;
    }

    public void setPercentCarbs(Double percentCarbs) {
        this.percentCarbs = percentCarbs;
    }
}