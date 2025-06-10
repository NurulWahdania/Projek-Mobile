package com.example.projek.Model; // Pastikan package ini sesuai dengan lokasi file Anda

import androidx.room.TypeConverters;

// Import TypeConverters dari package Database Anda
import com.example.projek.Database.NutrientListConverter;
import com.example.projek.Database.PropertyListConverter; // Ini import TypeConverter untuk List<Property>
import com.example.projek.Database.FlavonoidListConverter; // Ini import TypeConverter untuk List<Flavonoid>

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

// Import kelas model Property dan Flavonoid yang seharusnya ada di package Model Anda
import com.example.projek.Model.Property; // <--- PASTIKAN INI ADALAH KELAS MODEL 'Property' ANDA
import com.example.projek.Model.Flavonoid; // <--- PASTIKAN INI ADALAH KELAS MODEL 'Flavonoid' ANDA

public class Nutrition implements Serializable {
    @SerializedName("nutrients")
    @TypeConverters(NutrientListConverter.class)
    private List<Nutrient> nutrients;

    @SerializedName("properties")
    @TypeConverters(PropertyListConverter.class)
    private List<Property> properties; // <--- Tipe data List<Property> yang BENAR

    @SerializedName("flavonoids")
    @TypeConverters(FlavonoidListConverter.class)
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