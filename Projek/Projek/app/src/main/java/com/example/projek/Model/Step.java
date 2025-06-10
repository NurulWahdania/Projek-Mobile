package com.example.projek.Model;

import androidx.room.TypeConverters;

import com.example.projek.Database.EquipmentListConverter; // Perhatikan perubahan package
import com.example.projek.Database.IngredientListConverter; // Perhatikan perubahan package
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class Step implements Serializable {
    @SerializedName("number")
    private Integer number;
    @SerializedName("step")
    private String step;
    @SerializedName("ingredients")
    @TypeConverters(IngredientListConverter.class)
    private List<Ingredient> ingredients;
    @SerializedName("equipment")
    @TypeConverters(EquipmentListConverter.class)
    private List<Equipment> equipment;

    // Constructor, Getters, and Setters
    public Step(Integer number, String step, List<Ingredient> ingredients, List<Equipment> equipment) {
        this.number = number;
        this.step = step;
        this.ingredients = ingredients;
        this.equipment = equipment;
    }

    public Integer getNumber() {
        return number;
    }

    public void setNumber(Integer number) {
        this.number = number;
    }

    public String getStep() {
        return step;
    }

    public void setStep(String step) {
        this.step = step;
    }

    public List<Ingredient> getIngredients() {
        return ingredients;
    }

    public void setIngredients(List<Ingredient> ingredients) {
        this.ingredients = ingredients;
    }

    public List<Equipment> getEquipment() {
        return equipment;
    }

    public void setEquipment(List<Equipment> equipment) {
        this.equipment = equipment;
    }
}