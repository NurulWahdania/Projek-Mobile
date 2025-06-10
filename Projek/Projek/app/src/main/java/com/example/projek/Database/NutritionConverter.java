package com.example.projek.Database;

import androidx.room.TypeConverter;

import com.example.projek.Model.Nutrition;
import com.google.gson.Gson;

public class NutritionConverter {
    @TypeConverter
    public static Nutrition fromString(String value) {
        return new Gson().fromJson(value, Nutrition.class);
    }

    @TypeConverter
    public static String fromNutrition(Nutrition nutrition) {
        Gson gson = new Gson();
        return gson.toJson(nutrition);
    }
}