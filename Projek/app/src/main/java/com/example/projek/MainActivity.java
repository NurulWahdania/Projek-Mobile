package com.example.projek;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.projek.Utils.ThemeHelper; // Perhatikan import package utils Anda
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    private NavController navController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ThemeHelper.applySavedTheme(this); // Terapkan tema yang disimpan saat aplikasi dimulai
        setContentView(R.layout.activity_main);

        // Setup Navigation Component dengan NavHostFragment dan BottomNavigationView
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment_main);
        if (navHostFragment != null) {
            navController = navHostFragment.getNavController();
            BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
            NavigationUI.setupWithNavController(bottomNavigationView, navController);
        }

        // Tombol Ganti Tema (Bisa disembunyikan atau dipindahkan ke Fragment Pengaturan jika ada)
        Button btnToggleTheme = findViewById(R.id.btn_toggle_theme);
        // Anda bisa mengatur visibilitasnya di sini, contoh:
        // btnToggleTheme.setVisibility(View.VISIBLE);
        btnToggleTheme.setOnClickListener(v -> {
            ThemeHelper.toggleTheme(this);
            recreate(); // Rekonstruksi activity untuk menerapkan tema baru secara instan
        });
    }

    // Metode ini diperlukan untuk mendukung tombol Back/Up di Toolbar jika Anda menggunakannya
    @Override
    public boolean onSupportNavigateUp() {
        return navController.navigateUp() || super.onSupportNavigateUp();
    }
}