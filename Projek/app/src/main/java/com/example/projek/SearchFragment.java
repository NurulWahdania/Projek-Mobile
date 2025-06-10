package com.example.projek;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projek.Adapter.SearchResultAdapter;
import com.example.projek.Model.Recipe;
import com.example.projek.Model.RecipeResults;
import com.example.projek.Network.ApiService;
import com.example.projek.Network.RetrofitClient;
import com.example.projek.Utils.AppExecutors;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchFragment extends Fragment {

    private EditText etSearchQuery, etSearchDiet;
    private Button btnPerformSearch;
    private RecyclerView rvSearchResults;
    private ProgressBar progressBar;
    private TextView tvErrorMessage;

    private SearchResultAdapter adapter;
    private ApiService apiService;
    private AppExecutors appExecutors;
    private Handler mainHandler = new Handler(Looper.getMainLooper());

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        etSearchQuery = view.findViewById(R.id.et_search_query);
        btnPerformSearch = view.findViewById(R.id.btn_perform_search);
        rvSearchResults = view.findViewById(R.id.rv_search_results);
        progressBar = view.findViewById(R.id.progress_bar_search);
        tvErrorMessage = view.findViewById(R.id.tv_search_error_message);
        etSearchDiet = view.findViewById(R.id.et_search_diet);


        apiService = RetrofitClient.getClient();
        appExecutors = AppExecutors.getInstance();

        rvSearchResults.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new SearchResultAdapter(requireContext());
        rvSearchResults.setAdapter(adapter);

        adapter.setOnItemClickListener(recipe -> {
            Intent intent = new Intent(requireContext(), DetailRecipeActivity.class);
            intent.putExtra("recipeId", recipe.getId());
            intent.putExtra("isSavedRecipe", false); // Resep dari API bukan resep yang disimpan
            startActivity(intent);
        });

        btnPerformSearch.setOnClickListener(v -> performSearch());

        etSearchQuery.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                performSearch();
                return true;
            }
            return false;
        });

        return view;
    }

    private void performSearch() {
        String query = etSearchQuery.getText().toString().trim();
        String diet = etSearchDiet.getText().toString().trim();

        if (query.isEmpty() && diet.isEmpty()) {
            Toast.makeText(requireContext(), "Masukkan kata kunci pencarian atau jenis diet.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!isNetworkAvailable()) {
            tvErrorMessage.setText("Tidak ada koneksi internet. Pencarian tidak dapat dilakukan.");
            tvErrorMessage.setVisibility(View.VISIBLE);
            rvSearchResults.setVisibility(View.GONE);
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        tvErrorMessage.setVisibility(View.GONE);
        rvSearchResults.setVisibility(View.GONE);

        appExecutors.networkIO().execute(() -> {
            Call<RecipeResults> call = apiService.searchRecipes(query, diet, "", 10);

            call.enqueue(new Callback<RecipeResults>() {
                @Override
                public void onResponse(@NonNull Call<RecipeResults> call, @NonNull Response<RecipeResults> response) {
                    mainHandler.post(() -> {
                        progressBar.setVisibility(View.GONE);
                        if (response.isSuccessful() && response.body() != null) {
                            List<Recipe> recipes = response.body().getRecipesList();

                            Log.d("SearchFragment", "Jumlah resep yang diterima: " + (recipes != null ? recipes.size() : "null"));
                            if (recipes != null && !recipes.isEmpty()) {
                                adapter.setRecipes(recipes);
                                rvSearchResults.setVisibility(View.VISIBLE);
                            } else {
                                tvErrorMessage.setText("Tidak ada resep yang ditemukan untuk pencarian Anda.");
                                tvErrorMessage.setVisibility(View.VISIBLE);
                            }
                        } else {
                            tvErrorMessage.setText("Gagal mencari resep: " + response.message() + " (Kode: " + response.code() + ")");
                            Log.e("SearchFragment", "Error response: " + response.code() + " - " + response.message());
                        }
                    });
                }

                @Override
                public void onFailure(@NonNull Call<RecipeResults> call, @NonNull Throwable t) {
                    mainHandler.post(() -> {
                        progressBar.setVisibility(View.GONE);
                        tvErrorMessage.setText("Terjadi kesalahan jaringan saat mencari: " + t.getMessage());
                        tvErrorMessage.setVisibility(View.VISIBLE);
                        Log.e("SearchFragment", "Network error: ", t);
                    });
                }
            });
        });
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) requireContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
            return activeNetworkInfo != null && activeNetworkInfo.isConnected();
        }
        return false;
    }
}