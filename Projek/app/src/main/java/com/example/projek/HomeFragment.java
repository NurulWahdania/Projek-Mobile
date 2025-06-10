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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.example.projek.Adapter.RecipeHomeAdapter;
import com.example.projek.Network.ApiResponse;
import com.example.projek.Model.Recipe;
import com.example.projek.Network.ApiService;
import com.example.projek.Network.RetrofitClient;
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
    private LinearLayoutManager layoutManager;

    private boolean isLoading = false;
    private boolean isLastPage = false;
    private int currentOffset = 0;
    private final int PAGE_SIZE = 10;
    private static final String QUERY_TAG = "main course";

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        rvRecipes = view.findViewById(R.id.rv_recipes_home);
        progressBar = view.findViewById(R.id.progress_bar);
        tvErrorMessage = view.findViewById(R.id.tv_error_message);
        btnRefresh = view.findViewById(R.id.btn_refresh);

        apiService = RetrofitClient.getClient();
        setupRecyclerView();

        btnRefresh.setOnClickListener(v -> loadFirstPage());
        loadFirstPage();

        return view;
    }

    private void setupRecyclerView() {
        layoutManager = new LinearLayoutManager(getContext());
        rvRecipes.setLayoutManager(layoutManager);
        adapter = new RecipeHomeAdapter(getContext());
        rvRecipes.setAdapter(adapter);

        adapter.setOnItemClickListener(recipe -> {
            if (recipe != null) {
                Intent intent = new Intent(getContext(), DetailRecipeActivity.class);
                intent.putExtra("recipeId", recipe.getId());
                startActivity(intent);
            }
        });

        rvRecipes.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int visibleItemCount = layoutManager.getChildCount();
                int totalItemCount = layoutManager.getItemCount();
                int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();

                if (!isLoading && !isLastPage) {
                    if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount
                            && firstVisibleItemPosition >= 0
                            && totalItemCount >= PAGE_SIZE) {
                        loadNextPage();
                    }
                }
            }
        });
    }

    private void loadFirstPage() {
        Log.d("HomeFragment", "Memuat halaman pertama...");
        if (!isNetworkAvailable()) {
            showErrorState("Tidak ada koneksi internet.");
            return;
        }
        showLoading(true);
        currentOffset = 0;
        isLastPage = false;

        callApi().enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponse> call, @NonNull Response<ApiResponse> response) {
                showLoading(false);
                if (response.isSuccessful() && response.body() != null) {
                    List<Recipe> recipes = response.body().getResults();
                    if (recipes == null || recipes.isEmpty()) {
                        showErrorState("Tidak ada resep yang ditemukan.");
                        return;
                    }
                    adapter.setRecipes(recipes);
                    if (recipes.size() >= PAGE_SIZE) {
                        adapter.addLoadingFooter();
                    } else {
                        isLastPage = true;
                    }
                } else {
                    showErrorState("Gagal memuat resep: " + response.message());
                }
            }
            @Override
            public void onFailure(@NonNull Call<ApiResponse> call, @NonNull Throwable t) {
                showLoading(false);
                showErrorState("Kesalahan Jaringan: " + t.getMessage());
            }
        });
    }

    private void loadNextPage() {
        Log.d("HomeFragment", "Memuat halaman berikutnya... Offset: " + currentOffset);
        isLoading = true;
        currentOffset += PAGE_SIZE;

        callApi().enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponse> call, @NonNull Response<ApiResponse> response) {
                adapter.removeLoadingFooter();
                isLoading = false;

                if (response.isSuccessful() && response.body() != null) {
                    List<Recipe> newRecipes = response.body().getResults();
                    adapter.addAll(newRecipes);
                    if (newRecipes.size() >= PAGE_SIZE) {
                        adapter.addLoadingFooter();
                    } else {
                        isLastPage = true;
                    }
                }
            }
            @Override
            public void onFailure(@NonNull Call<ApiResponse> call, @NonNull Throwable t) {
                isLoading = false;
                Log.e("HomeFragment", "Gagal load more: ", t);
            }
        });
    }

    private Call<ApiResponse> callApi() {
        // Pemanggilan ini sekarang sudah sesuai dengan definisi di ApiService
        return apiService.searchRecipes(QUERY_TAG, PAGE_SIZE, currentOffset);
    }

    private void showLoading(boolean show) {
        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        if (show) {
            rvRecipes.setVisibility(View.GONE);
            tvErrorMessage.setVisibility(View.GONE);
            btnRefresh.setVisibility(View.GONE);
        } else {
            rvRecipes.setVisibility(View.VISIBLE);
        }
    }

    private void showErrorState(String message) {
        progressBar.setVisibility(View.GONE);
        rvRecipes.setVisibility(View.GONE);
        tvErrorMessage.setText(message);
        tvErrorMessage.setVisibility(View.VISIBLE);
        btnRefresh.setVisibility(View.VISIBLE);
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager cm = (ConnectivityManager) requireContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }
}