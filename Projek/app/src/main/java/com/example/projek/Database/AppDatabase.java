package com.example.projek.Database; // Pastikan package ini sesuai

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import android.content.Context;

import com.example.projek.Model.Recipe; // Perhatikan import Model Anda
import com.example.projek.Database.RecipeDao; // Perhatikan import Database.RecipeDao Anda

// Import semua TypeConverters yang sudah ada
import com.example.projek.Database.InstructionListConverter;
import com.example.projek.Database.IngredientListConverter;
import com.example.projek.Database.NutritionConverter;
import com.example.projek.Database.NutrientListConverter;
import com.example.projek.Database.EquipmentListConverter;
import com.example.projek.Database.StepListConverter;
// Import untuk TypeConverters yang baru (Property dan Flavonoid)
import com.example.projek.Database.PropertyListConverter;
import com.example.projek.Database.FlavonoidListConverter;
// <--- TAMBAHKAN IMPORT INI UNTUK STRINGLISTCONVERTER
import com.example.projek.Database.StringListConverter; // <--- TAMBAHKAN BARIS INI


@Database(entities = {Recipe.class}, version = 3, exportSchema = false) // <--- PENTING: NAIKKAN VERSI DATABASE!
@TypeConverters({InstructionListConverter.class, IngredientListConverter.class,
        NutritionConverter.class, NutrientListConverter.class,
        EquipmentListConverter.class, StepListConverter.class,
        PropertyListConverter.class, FlavonoidListConverter.class,
        StringListConverter.class}) // <--- TAMBAHKAN StringListConverter.class DI SINI
public abstract class AppDatabase extends RoomDatabase {
    public abstract RecipeDao recipeDao();

    private static volatile AppDatabase INSTANCE;

    public static AppDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    AppDatabase.class, "recipe_database")
                            .fallbackToDestructiveMigration() // Hanya untuk pengembangan, hapus ini di produksi
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}