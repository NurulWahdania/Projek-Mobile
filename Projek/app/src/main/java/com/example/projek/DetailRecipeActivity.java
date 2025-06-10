package com.example.projek;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.LinearLayout; // Import ini sudah benar
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.projek.Helper.DatabaseHelper; // Perhatikan import package Helper Anda
import com.example.projek.Model.AnalyzedInstruction; // Perhatikan import package Model Anda
import com.example.projek.Model.Ingredient; // Perhatikan import package Model Anda
import com.example.projek.Model.Nutrient; // Perhatikan import package Model Anda
import com.example.projek.Model.Recipe; // Perhatikan import package Model Anda
import com.example.projek.Model.Step; // Perhatikan import package Model Anda
import com.example.projek.Network.ApiService; // Perhatikan import package Network Anda
import com.example.projek.Network.RetrofitClient; // Perhatikan import package Network Anda
import com.example.projek.Utils.AppExecutors; // Perhatikan import package Utils Anda

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetailRecipeActivity extends AppCompatActivity {

    private ImageView ivRecipeImage;
    private TextView tvRecipeTitle, tvReadyInMinutes, tvServings, tvCategory,
            tvDescription, tvIngredients, tvNutritionInfo, tvInstructions;
    private Button btnSaveRecipe, btnRemoveRecipe;
    private ProgressBar progressBarDetail;
    private TextView tvDetailErrorMessage;
    // Jika Anda punya ID di root LinearLayout ScrollView, Anda bisa uncomment baris ini
    // private LinearLayout detailContentLayout;

    private Recipe currentRecipe;
    private DatabaseHelper databaseHelper;
    private ApiService apiService;
    private AppExecutors appExecutors;
    private Handler mainHandler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_recipe);

        initViews();
        databaseHelper = new DatabaseHelper(this);
        apiService = RetrofitClient.getClient();
        appExecutors = AppExecutors.getInstance();

        // Dapatkan ID resep dari Intent yang dikirim HomeFragment/SearchFragment
        int recipeId = getIntent().getIntExtra("recipeId", -1); // <--- SET BREAKPOINT DI SINI
        Log.d("DetailActivity", "DetailActivity menerima recipeId: " + recipeId); // LOG INI SUDAH ADA

        if (recipeId != -1) {
            loadRecipeDetails(recipeId); // Panggil metode untuk memuat detail resep
        } else {
            Toast.makeText(this, "ID Resep tidak valid.", Toast.LENGTH_SHORT).show();
            finish(); // Tutup activity jika ID resep tidak ada
        }

        // Listener untuk tombol Simpan dan Hapus
        // Perhatikan: currentRecipe baru akan terisi setelah loadRecipeDetails berhasil
        btnSaveRecipe.setOnClickListener(v -> {
            if (currentRecipe != null) {
                saveRecipe(currentRecipe);
            } else {
                Toast.makeText(DetailRecipeActivity.this, "Data resep belum dimuat sepenuhnya.", Toast.LENGTH_SHORT).show();
            }
        });
        btnRemoveRecipe.setOnClickListener(v -> {
            if (currentRecipe != null) {
                showDeleteConfirmationDialog(currentRecipe);
            } else {
                Toast.makeText(DetailRecipeActivity.this, "Data resep belum dimuat sepenuhnya.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initViews() {
        ivRecipeImage = findViewById(R.id.iv_recipe_image);
        tvRecipeTitle = findViewById(R.id.tv_recipe_title);
        tvReadyInMinutes = findViewById(R.id.tv_ready_in_minutes);
        tvServings = findViewById(R.id.tv_servings);
        tvCategory = findViewById(R.id.tv_category);
        tvDescription = findViewById(R.id.tv_description);
        tvIngredients = findViewById(R.id.tv_ingredients);
        tvNutritionInfo = findViewById(R.id.tv_nutrition_info);
        tvInstructions = findViewById(R.id.tv_instructions);
        btnSaveRecipe = findViewById(R.id.btn_save_recipe);
        btnRemoveRecipe = findViewById(R.id.btn_remove_recipe);
        progressBarDetail = findViewById(R.id.progress_bar_detail);
        tvDetailErrorMessage = findViewById(R.id.tv_detail_error_message);
        // Jika Anda ingin mengontrol visibilitas seluruh layout konten detail,
        // dan Anda sudah memberikan android:id="@+id/detail_content_layout"
        // pada LinearLayout root di ScrollView Anda, Anda bisa uncomment ini:
        // detailContentLayout = findViewById(R.id.detail_content_layout);
    }

    private void loadRecipeDetails(int recipeId) {
        // Tampilkan loading state
        progressBarDetail.setVisibility(View.VISIBLE);
        tvDetailErrorMessage.setVisibility(View.GONE);
        // Sembunyikan semua elemen UI resep saat loading
        setRecipeDetailsVisibility(View.GONE); // Metode baru untuk menyembunyikan/menampilkan UI

        appExecutors.networkIO().execute(() -> {
            Call<Recipe> call = apiService.getRecipeDetails(recipeId); // Panggil API detail resep

            call.enqueue(new Callback<Recipe>() {
                @Override
                public void onResponse(@NonNull Call<Recipe> call, @NonNull Response<Recipe> response) {
                    mainHandler.post(() -> {
                        progressBarDetail.setVisibility(View.GONE);
                        if (response.isSuccessful() && response.body() != null) {
                            currentRecipe = response.body(); // BARIS INI BENAR HANYA DI SINI
                            displayRecipeDetails(currentRecipe); // Tampilkan detail
                            checkIfRecipeIsSaved(currentRecipe.getId()); // Cek status simpan
                            setRecipeDetailsVisibility(View.VISIBLE); // Tampilkan elemen UI resep
                        } else {
                            tvDetailErrorMessage.setText("Gagal memuat detail resep: " + response.message() + " (Kode: " + response.code() + ")");
                            tvDetailErrorMessage.setVisibility(View.VISIBLE);
                            Log.e("DetailRecipeActivity", "Error response: " + response.code() + " - " + response.message());
                            setRecipeDetailsVisibility(View.GONE); // Pastikan UI disembunyikan
                        }
                    });
                }

                @Override
                public void onFailure(@NonNull Call<Recipe> call, @NonNull Throwable t) {
                    mainHandler.post(() -> {
                        progressBarDetail.setVisibility(View.GONE);
                        tvDetailErrorMessage.setText("Terjadi kesalahan jaringan saat memuat detail: " + t.getMessage());
                        tvDetailErrorMessage.setVisibility(View.VISIBLE);
                        Log.e("DetailRecipeActivity", "Network error: ", t);
                        setRecipeDetailsVisibility(View.GONE); // Pastikan UI disembunyikan
                    });
                }
            });
        });
    }

    // Metode baru untuk mengontrol visibilitas elemen UI resep
    private void setRecipeDetailsVisibility(int visibility) {
        ivRecipeImage.setVisibility(visibility);
        tvRecipeTitle.setVisibility(visibility);
        tvReadyInMinutes.setVisibility(visibility);
        tvServings.setVisibility(visibility);
        tvCategory.setVisibility(visibility);
        tvDescription.setVisibility(visibility);
        tvIngredients.setVisibility(visibility);
        tvNutritionInfo.setVisibility(visibility);
        tvInstructions.setVisibility(visibility);
        btnSaveRecipe.setVisibility(visibility);
        // btnRemoveRecipe visibility dikontrol oleh checkIfRecipeIsSaved
        // if (detailContentLayout != null) detailContentLayout.setVisibility(visibility);
        // Anda juga perlu mengatur visibilitas label TextView jika ada
        findViewById(R.id.tv_description_label).setVisibility(visibility);
        findViewById(R.id.tv_ingredients_label).setVisibility(visibility);
        findViewById(R.id.tv_nutrition_label).setVisibility(visibility);
        findViewById(R.id.tv_instructions_label).setVisibility(visibility);
    }


    private void displayRecipeDetails(Recipe recipe) {
        if (recipe.getImage() != null && !recipe.getImage().isEmpty()) {
            Glide.with(this).load(recipe.getImage())
                    .placeholder(R.drawable.ic_placeholder)
                    .error(R.drawable.ic_error)
                    .into(ivRecipeImage);
        } else {
            ivRecipeImage.setImageResource(R.drawable.ic_placeholder);
        }

        tvRecipeTitle.setText(recipe.getTitle());
        tvReadyInMinutes.setText("Waktu Pembuatan: " + (recipe.getReadyInMinutes() != null ? recipe.getReadyInMinutes() : "-") + " menit");
        tvServings.setText("Porsi: " + (recipe.getServings() != null ? recipe.getServings() : "-"));
        tvCategory.setText("Kategori: " + recipe.getMealType());

        if (recipe.getSummary() != null && !recipe.getSummary().isEmpty()) {
            tvDescription.setText(Html.fromHtml(recipe.getSummary(), Html.FROM_HTML_MODE_COMPACT));
        } else {
            tvDescription.setText("Tidak ada deskripsi tersedia.");
        }

        StringBuilder ingredientsBuilder = new StringBuilder();
        if (recipe.getIngredients() != null && !recipe.getIngredients().isEmpty()) {
            for (Ingredient ingredient : recipe.getIngredients()) {
                ingredientsBuilder.append("• ")
                        .append(ingredient.getAmount() != null ? String.format("%.2f", ingredient.getAmount()) : "")
                        .append(" ")
                        .append(ingredient.getUnit() != null ? ingredient.getUnit() : "")
                        .append(" ")
                        .append(ingredient.getName() != null ? ingredient.getName() : "")
                        .append("\n");
            }
            tvIngredients.setText(ingredientsBuilder.toString().trim());
        } else {
            tvIngredients.setText("Tidak ada bahan-bahan tersedia.");
        }

        StringBuilder nutritionBuilder = new StringBuilder();
        // Lakukan debugging di sini! Pastikan recipe.getNutrition() tidak null dan getNutrients() tidak null/kosong
        Log.d("DetailActivity", "Recipe Nutrition: " + (recipe.getNutrition() != null ? "Ada" : "Tidak ada"));
        if (recipe.getNutrition() != null && recipe.getNutrition().getNutrients() != null && !recipe.getNutrition().getNutrients().isEmpty()) {
            Log.d("DetailActivity", "Jumlah Nutrisi: " + recipe.getNutrition().getNutrients().size());
            for (Nutrient nutrient : recipe.getNutrition().getNutrients()) {
                nutritionBuilder.append(nutrient.getName()).append(": ")
                        .append(nutrient.getAmount() != null ? String.format("%.2f", nutrient.getAmount()) : "")
                        .append(" ").append(nutrient.getUnit() != null ? nutrient.getUnit() : "")
                        .append(" (")
                        .append(nutrient.getPercentOfDailyNeeds() != null ? String.format("%.2f", nutrient.getPercentOfDailyNeeds()) : "")
                        .append("% of daily needs)\n");
            }
            tvNutritionInfo.setText(nutritionBuilder.toString().trim());
        } else {
            tvNutritionInfo.setText("Tidak ada informasi nutrisi tersedia.");
            Log.d("DetailActivity", "Informasi nutrisi kosong atau null.");
        }

        StringBuilder instructionsBuilder = new StringBuilder();
        if (recipe.getAnalyzedInstructions() != null && !recipe.getAnalyzedInstructions().isEmpty()) {
            for (AnalyzedInstruction instruction : recipe.getAnalyzedInstructions()) {
                if (instruction.getSteps() != null) {
                    for (Step step : instruction.getSteps()) {
                        instructionsBuilder.append(step.getNumber()).append(". ")
                                .append(step.getStep()).append("\n");
                    }
                }
            }
            tvInstructions.setText(instructionsBuilder.toString().trim());
        } else {
            tvInstructions.setText("Tidak ada langkah-langkah pembuatan tersedia.");
        }
    }

    private void checkIfRecipeIsSaved(int recipeId) {
        databaseHelper.isRecipeSaved(recipeId).observe(this, isSaved -> {
            if (isSaved != null && isSaved) {
                btnSaveRecipe.setVisibility(View.GONE);
                btnRemoveRecipe.setVisibility(View.VISIBLE);
            } else {
                btnSaveRecipe.setVisibility(View.VISIBLE);
                btnRemoveRecipe.setVisibility(View.GONE);
            }
        });
    }

    private void saveRecipe(Recipe recipe) {
        AppExecutors.getInstance().diskIO().execute(() -> {
            databaseHelper.insertRecipe(recipe);
            runOnUiThread(() -> {
                Toast.makeText(DetailRecipeActivity.this, "Resep disimpan!", Toast.LENGTH_SHORT).show();
                checkIfRecipeIsSaved(recipe.getId());
            });
        });
    }

    private void showDeleteConfirmationDialog(Recipe recipe) {
        new AlertDialog.Builder(this)
                .setTitle("Hapus Resep")
                .setMessage("Apakah Anda yakin ingin menghapus resep ini dari daftar tersimpan?")
                .setPositiveButton("Ya", (dialog, which) -> deleteRecipe(recipe))
                .setNegativeButton("Tidak", null)
                .show();
    }

    private void deleteRecipe(Recipe recipe) {
        AppExecutors.getInstance().diskIO().execute(() -> {
            databaseHelper.deleteRecipe(recipe);
            runOnUiThread(() -> {
                Toast.makeText(DetailRecipeActivity.this, "Resep dihapus!", Toast.LENGTH_SHORT).show();
                checkIfRecipeIsSaved(recipe.getId());
            });
        });
    }
}