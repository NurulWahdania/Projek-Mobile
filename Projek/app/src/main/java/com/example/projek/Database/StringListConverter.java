package com.example.projek.Database; // Pastikan package ini sesuai dengan struktur Anda

import androidx.room.TypeConverter;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;

public class StringListConverter {
    @TypeConverter
    public static List<String> fromString(String value) {
        if (value == null) {
            return Collections.emptyList(); // Mengembalikan daftar kosong jika string null
        }
        // Menggunakan Gson untuk mengonversi string JSON menjadi List<String>
        Type listType = new TypeToken<List<String>>() {}.getType();
        return new Gson().fromJson(value, listType);
    }

    @TypeConverter
    public static String fromList(List<String> list) {
        if (list == null) {
            return null; // Mengembalikan null jika daftar null
        }
        // Menggunakan Gson untuk mengonversi List<String> menjadi string JSON
        Gson gson = new Gson();
        return gson.toJson(list);
    }
}