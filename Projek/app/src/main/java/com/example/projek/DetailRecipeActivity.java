package com.example.projek;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import com.example.projek.Helper.DatabaseHelper;
import com.example.projek.Model.AnalyzedInstruction;
import com.example.projek.Model.Ingredient;
import com.example.projek.Model.Recipe;
import com.example.projek.Model.Step;
import com.example.projek.Network.ApiService;
import com.example.projek.Network.RetrofitClient;
import com.example.projek.Utils.AppExecutors;

import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetailRecipeActivity extends AppCompatActivity {

    private ImageView ivRecipeImage;
    private TextView tvRecipeTitle, tvReadyInMinutes, tvServings, tvCategory,
            tvDescription, tvIngredients, tvInstructions;
    private FloatingActionButton btnSaveRecipe, btnRemoveRecipe, btnShareRecipe;
    private ProgressBar progressBarDetail;
    private TextView tvDetailErrorMessage;
    private View contentLayout; // Layout utama untuk konten

    private Recipe currentRecipe;
    private DatabaseHelper databaseHelper;
    private ApiService apiService;
    private AppExecutors appExecutors;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_recipe);

        initViews();
        databaseHelper = DatabaseHelper.getInstance(this);
        apiService = RetrofitClient.getClient();
        appExecutors = AppExecutors.getInstance();

        int currentRecipeId = getIntent().getIntExtra("recipeId", -1);
        boolean currentIsSavedRecipe = getIntent().getBooleanExtra("isSavedRecipe", false);

        if (currentRecipeId != -1) {
            if (currentIsSavedRecipe) {
                loadRecipeDetailsFromDatabase(currentRecipeId);
            } else {
                loadRecipeDetailsFromApi(currentRecipeId);
            }
        } else {
            Toast.makeText(this, "ID Resep tidak valid.", Toast.LENGTH_SHORT).show();
            finish();
        }

        setupListeners();
    }

    private void initViews() {
        ivRecipeImage = findViewById(R.id.iv_recipe_image);
        tvRecipeTitle = findViewById(R.id.tv_recipe_title);
        tvReadyInMinutes = findViewById(R.id.tv_ready_in_minutes);
        tvServings = findViewById(R.id.tv_servings);
        tvCategory = findViewById(R.id.tv_category);
        tvDescription = findViewById(R.id.tv_description);
        tvIngredients = findViewById(R.id.tv_ingredients);
        tvInstructions = findViewById(R.id.tv_instructions);
        btnSaveRecipe = findViewById(R.id.btn_save_recipe);
        btnRemoveRecipe = findViewById(R.id.btn_remove_recipe);
        btnShareRecipe = findViewById(R.id.btn_share_recipe);
        progressBarDetail = findViewById(R.id.progress_bar_detail);
        tvDetailErrorMessage = findViewById(R.id.tv_detail_error_message);
        contentLayout = findViewById(R.id.content_layout_detail); // Pastikan ID ini ada di XML Anda
    }

    private void setupListeners() {
        btnSaveRecipe.setOnClickListener(v -> {
            if (currentRecipe != null) saveRecipe(currentRecipe);
        });
        btnRemoveRecipe.setOnClickListener(v -> {
            if (currentRecipe != null) showDeleteConfirmationDialog(currentRecipe);
        });
        btnShareRecipe.setOnClickListener(v -> shareRecipe());
    }

    private void showLoading(boolean isLoading) {
        progressBarDetail.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        if (contentLayout != null) {
            contentLayout.setVisibility(isLoading ? View.GONE : View.VISIBLE);
        }
    }

    private void showError(String message) {
        progressBarDetail.setVisibility(View.GONE);
        if (contentLayout != null) {
            contentLayout.setVisibility(View.GONE);
        }
        tvDetailErrorMessage.setText(message);
        tvDetailErrorMessage.setVisibility(View.VISIBLE);
    }

    private void loadRecipeDetailsFromApi(int recipeId) {
        showLoading(true);
        apiService.getRecipeDetails(recipeId).enqueue(new Callback<Recipe>() {
            @Override
            public void onResponse(@NonNull Call<Recipe> call, @NonNull Response<Recipe> response) {
                if (response.isSuccessful() && response.body() != null) {
                    processAndDisplayRecipe(response.body());
                } else {
                    showError("Gagal memuat dari API (Kode: " + response.code() + ")");
                }
            }
            @Override
            public void onFailure(@NonNull Call<Recipe> call, @NonNull Throwable t) {
                showError("Kesalahan jaringan: " + t.getMessage());
            }
        });
    }

    private void loadRecipeDetailsFromDatabase(int recipeId) {
        showLoading(true);
        appExecutors.diskIO().execute(() -> {
            try {
                // MEMANGGIL METODE YANG SUDAH DIPERBAIKI (TIDAK ADA .get())
                final Recipe loadedRecipe = databaseHelper.getRecipeById(recipeId);

                runOnUiThread(() -> {
                    if (loadedRecipe != null) {
                        processAndDisplayRecipe(loadedRecipe);
                    } else {
                        showError("Resep tidak ditemukan di database lokal.");
                    }
                });
            } catch (Exception e) {
                Log.e("DetailActivity", "Error memuat resep dari DB", e);
                runOnUiThread(() -> showError("Gagal memuat resep dari lokal."));
            }
        });
    }

    private void processAndDisplayRecipe(Recipe recipe) {
        currentRecipe = recipe;
        appExecutors.diskIO().execute(() -> {
            // PROSES BERAT (KONVERSI HTML, DLL) DIJALANKAN DI BACKGROUND
            final Spanned formattedSummary = (recipe.getSummary() != null && !recipe.getSummary().isEmpty())
                    ? Html.fromHtml(recipe.getSummary(), Html.FROM_HTML_MODE_COMPACT)
                    : null;
            final String ingredientsText = buildIngredientsString(recipe.getIngredients());
            final String instructionsText = buildInstructionsString(recipe.getAnalyzedInstructions());

            // KIRIM DATA YANG SUDAH SIAP TAMPIL KE UI THREAD
            runOnUiThread(() -> {
                displayProcessedRecipe(formattedSummary, ingredientsText, instructionsText);
                checkIfRecipeIsSaved(recipe.getId());
                showLoading(false);
            });
        });
    }

    private void displayProcessedRecipe(Spanned summary, String ingredients, String instructions) {
        if (currentRecipe == null) return;
        Glide.with(this).load(currentRecipe.getImage()).placeholder(R.drawable.ic_placeholder).into(ivRecipeImage);
        tvRecipeTitle.setText(currentRecipe.getTitle());

        if (currentRecipe.getReadyInMinutes() != null) {
            tvReadyInMinutes.setText(String.format(Locale.getDefault(), "Waktu: %d menit", currentRecipe.getReadyInMinutes()));
        } else {
            tvReadyInMinutes.setText("Waktu: -");
        }
        if (currentRecipe.getServings() != null) {
            tvServings.setText(String.format(Locale.getDefault(), "Porsi: %d", currentRecipe.getServings()));
        } else {
            tvServings.setText("Porsi: -");
        }
        if (currentRecipe.getDishTypes() != null && !currentRecipe.getDishTypes().isEmpty()) {
            tvCategory.setText(String.format("Kategori: %s", currentRecipe.getDishTypes().get(0)));
        } else {
            tvCategory.setText("Kategori: -");
        }
        if (summary != null) {
            tvDescription.setText(summary);
        } else {
            tvDescription.setText("Tidak ada deskripsi tersedia.");
        }
        tvIngredients.setText(ingredients);
        tvInstructions.setText(instructions);
    }

    private String buildIngredientsString(List<Ingredient> ingredients) {
        if (ingredients == null || ingredients.isEmpty()) {
            return "Tidak ada bahan-bahan tersedia.";
        }
        StringBuilder builder = new StringBuilder();
        for (Ingredient ingredient : ingredients) {
            builder.append("• ");
            if (ingredient.getAmount() != null) {
                double amount = ingredient.getAmount();
                if (amount == (long) amount) builder.append(String.format(Locale.US, "%d", (long) amount));
                else builder.append(String.format(Locale.US, "%.2f", amount));
                builder.append(" ");
            }
            if (ingredient.getUnit() != null && !ingredient.getUnit().isEmpty()) {
                builder.append(ingredient.getUnit()).append(" ");
            }
            if (ingredient.getName() != null) {
                builder.append(ingredient.getName());
            }
            builder.append("\n");
        }
        return builder.toString().trim();
    }

    private String buildInstructionsString(List<AnalyzedInstruction> instructions) {
        if (instructions == null || instructions.isEmpty()) {
            return "Tidak ada langkah-langkah tersedia.";
        }
        StringBuilder builder = new StringBuilder();
        for (AnalyzedInstruction instruction : instructions) {
            if (instruction.getSteps() != null) {
                for (Step step : instruction.getSteps()) {
                    builder.append(step.getNumber()).append(". ").append(step.getStep()).append("\n\n");
                }
            }
        }
        return builder.toString().trim();
    }

    private void checkIfRecipeIsSaved(int recipeId) {
        appExecutors.diskIO().execute(() -> {
            final boolean isSaved = databaseHelper.isRecipeSaved(recipeId);
            runOnUiThread(() -> {
                btnSaveRecipe.setVisibility(isSaved ? View.GONE : View.VISIBLE);
                btnRemoveRecipe.setVisibility(isSaved ? View.VISIBLE : View.GONE);
            });
        });
    }

    private void saveRecipe(Recipe recipe) {
        appExecutors.diskIO().execute(() -> {
            databaseHelper.insertRecipe(recipe);
            runOnUiThread(() -> {
                Toast.makeText(this, "Resep disimpan!", Toast.LENGTH_SHORT).show();
                checkIfRecipeIsSaved(recipe.getId());
            });
        });
    }

    private void showDeleteConfirmationDialog(Recipe recipe) {
        new AlertDialog.Builder(this)
                .setTitle("Hapus Resep")
                .setMessage("Apakah Anda yakin ingin menghapus resep ini?")
                .setPositiveButton("Ya", (dialog, which) -> deleteRecipe(recipe.getId()))
                .setNegativeButton("Tidak", null)
                .show();
    }

    private void deleteRecipe(int recipeId) {
        appExecutors.diskIO().execute(() -> {
            final boolean success = databaseHelper.deleteRecipe(recipeId);
            runOnUiThread(() -> {
                if (success) {
                    Toast.makeText(this, "Resep dihapus!", Toast.LENGTH_SHORT).show();
                    checkIfRecipeIsSaved(recipeId);
                } else {
                    Toast.makeText(this, "Gagal menghapus resep.", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    private void shareRecipe() {
        if (currentRecipe != null && currentRecipe.getSourceUrl() != null && !currentRecipe.getSourceUrl().isEmpty()) {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Lihat Resep Lezat: " + currentRecipe.getTitle());
            shareIntent.putExtra(Intent.EXTRA_TEXT, "Saya menemukan resep menarik ini: " + currentRecipe.getTitle() + "\nLihat selengkapnya di: " + currentRecipe.getSourceUrl());
            startActivity(Intent.createChooser(shareIntent, "Bagikan Resep"));
        } else {
            Toast.makeText(this, "URL sumber tidak tersedia.", Toast.LENGTH_SHORT).show();
        }
    }
}