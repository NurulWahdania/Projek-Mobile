package com.example.projek.Database;

import androidx.room.TypeConverter;

import com.example.projek.Model.Ingredient;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;

public class IngredientListConverter {
    @TypeConverter
    public static List<Ingredient> fromString(String value) {
        if (value == null) {
            return Collections.emptyList();
        }
        Type listType = new TypeToken<List<Ingredient>>() {}.getType();
        return new Gson().fromJson(value, listType);
    }

    @TypeConverter
    public static String fromList(List<Ingredient> list) {
        Gson gson = new Gson();
        return gson.toJson(list);
    }
}
