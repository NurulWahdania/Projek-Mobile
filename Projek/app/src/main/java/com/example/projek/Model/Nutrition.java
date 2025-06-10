package com.example.projek.Model; // Pastikan package ini sesuai dengan lokasi file Anda


import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

// Import kelas model Property dan Flavonoid yang seharusnya ada di package Model Anda


public class Nutrition implements Serializable {
    @SerializedName("nutrients")
    private List<Nutrient> nutrients;

    @SerializedName("properties")
    private List<Property> properties; // <--- Tipe data List<Property> yang BENAR

    @SerializedName("flavonoids")
    private List<Flavonoid> flavonoids; // <--- Tipe data List<Flavonoid> yang BENAR

    // Constructor
    public Nutrition(List<Nutrient> nutrients, List<Property> properties, List<Flavonoid> flavonoids) {
        this.nutrients = nutrients;
        this.properties = properties;
        this.flavonoids = flavonoids;
    }

    // Getters and Setters untuk semua field
    public List<Nutrient> getNutrients() {
        return nutrients;
    }

    public void setNutrients(List<Nutrient> nutrients) {
        this.nutrients = nutrients;
    }

    public List<Property> getProperties() {
        return properties;
    }

    public void setProperties(List<Property> properties) {
        this.properties = properties;
    }

    public List<Flavonoid> getFlavonoids() {
        return flavonoids;
    }

    public void setFlavonoids(List<Flavonoid> flavonoids) {
        this.flavonoids = flavonoids;
    }
}