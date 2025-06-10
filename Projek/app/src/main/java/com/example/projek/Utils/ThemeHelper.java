package com.example.projek.Utils;

import android.content.Context;
import android.content.SharedPreferences;
import androidx.appcompat.app.AppCompatDelegate;

public class ThemeHelper {

    public static void applySavedTheme(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(Constants.PREFS_NAME, Context.MODE_PRIVATE);
        // Default ke mengikuti sistem jika belum ada tema yang disimpan
        int savedTheme = prefs.getInt(Constants.KEY_THEME, AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
        AppCompatDelegate.setDefaultNightMode(savedTheme);
    }

    public static void toggleTheme(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(Constants.PREFS_NAME, Context.MODE_PRIVATE);
        int currentTheme = prefs.getInt(Constants.KEY_THEME, AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);

        int newTheme;
        if (currentTheme == AppCompatDelegate.MODE_NIGHT_YES) { // Jika sedang gelap
            newTheme = AppCompatDelegate.MODE_NIGHT_NO; // Ganti ke terang
        } else { // Jika sedang terang atau mengikuti sistem
            newTheme = AppCompatDelegate.MODE_NIGHT_YES; // Ganti ke gelap
        }

        prefs.edit().putInt(Constants.KEY_THEME, newTheme).apply();
        AppCompatDelegate.setDefaultNightMode(newTheme);
    }
}