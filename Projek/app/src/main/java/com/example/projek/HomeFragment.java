package com.example.projek;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.projek.Adapter.RecipeHomeAdapter;
import com.example.projek.Model.Recipe;
import com.example.projek.Model.RecipeResults;
import com.example.projek.Network.ApiService;
import com.example.projek.Network.RetrofitClient;
import com.example.projek.Utils.AppExecutors;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment {

    private RecyclerView rvRecipes;
    private RecipeHomeAdapter adapter;
    private ProgressBar progressBar;
    private TextView tvErrorMessage;
    private Button btnRefresh;
    private ApiService apiService;
    private AppExecutors appExecutors;
    private Handler mainHandler = new Handler(Looper.getMainLooper());

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        rvRecipes = view.findViewById(R.id.rv_recipes_home);
        progressBar = view.findViewById(R.id.progress_bar);
        tvErrorMessage = view.findViewById(R.id.tv_error_message);
        btnRefresh = view.findViewById(R.id.btn_refresh);

        apiService = RetrofitClient.getClient();
        appExecutors = AppExecutors.getInstance();

        rvRecipes.setLayoutManager(new LinearLayoutManager(getContext()));
        // --- MODIFIKASI BARIS INI ---
        adapter = new RecipeHomeAdapter(getContext()); // <--- UBAH DI SINI
        // --- AKHIR MODIFIKASI ---
        rvRecipes.setAdapter(adapter);

        adapter.setOnItemClickListener(recipe -> {
            Intent intent = new Intent(getContext(), DetailRecipeActivity.class);
            intent.putExtra("recipeId", recipe.getId());
            startActivity(intent);
        });

        btnRefresh.setOnClickListener(v -> loadRandomRecipes());

        loadRandomRecipes();

        return view;
    }

    private void loadRandomRecipes() {
        if (!isNetworkAvailable()) {
            showErrorState("Tidak ada koneksi internet. Resep tidak dapat dimuat secara offline.");
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        tvErrorMessage.setVisibility(View.GONE);
        btnRefresh.setVisibility(View.GONE);
        rvRecipes.setVisibility(View.GONE);

        appExecutors.networkIO().execute(() -> {
            Call<RecipeResults> call = apiService.getRandomRecipes(10, "main course");

            call.enqueue(new Callback<RecipeResults>() {
                @Override
                public void onResponse(@NonNull Call<RecipeResults> call, @NonNull Response<RecipeResults> response) {
                    mainHandler.post(() -> {
                        progressBar.setVisibility(View.GONE);
                        if (response.isSuccessful() && response.body() != null) {
                            // Perubahan di sini: Menggunakan getRecipesList()
                            List<Recipe> recipes = response.body().getRecipesList();

                            Log.d("HomeFragment", "Jumlah resep yang diterima: " + (recipes != null ? recipes.size() : "null"));
                            if (recipes != null && !recipes.isEmpty()) {
                                adapter.setRecipes(recipes);
                                rvRecipes.setVisibility(View.VISIBLE);
                            } else {
                                showErrorState("Tidak ada resep yang ditemukan.");
                            }
                        } else {
                            showErrorState("Gagal memuat resep: " + response.message() + " (Kode: " + response.code() + ")");
                            Log.e("HomeFragment", "Error response: " + response.code() + " - " + response.message());
                        }
                    });
                }

                @Override
                public void onFailure(@NonNull Call<RecipeResults> call, @NonNull Throwable t) {
                    mainHandler.post(() -> {
                        progressBar.setVisibility(View.GONE);
                        showErrorState("Terjadi kesalahan jaringan: " + t.getMessage());
                        Log.e("HomeFragment", "Network error: ", t);
                    });
                }
            });
        });
    }

    private void showErrorState(String message) {
        rvRecipes.setVisibility(View.GONE);
        tvErrorMessage.setText(message);
        tvErrorMessage.setVisibility(View.VISIBLE);
        btnRefresh.setVisibility(View.VISIBLE);
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
            return activeNetworkInfo != null && activeNetworkInfo.isConnected();
        }
        return false;
    }
}