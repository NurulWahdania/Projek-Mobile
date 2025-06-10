package com.example.projek;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projek.Adapter.RecipeHomeAdapter;
import com.example.projek.Helper.DatabaseHelper;
import com.example.projek.Model.Recipe;
import com.example.projek.Utils.AppExecutors;

import java.util.ArrayList;
import java.util.List;

public class SavedRecipesFragment extends Fragment {

    private RecyclerView rvSavedRecipes;
    private RecipeHomeAdapter adapter;
    private ProgressBar progressBar;
    private TextView tvEmptyMessage;

    private DatabaseHelper databaseHelper;
    private AppExecutors appExecutors;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_saved_recipes, container, false);

        rvSavedRecipes = view.findViewById(R.id.rv_saved_recipes);
        progressBar = view.findViewById(R.id.progress_bar_saved);
        tvEmptyMessage = view.findViewById(R.id.tv_empty_saved_recipes);

        databaseHelper = DatabaseHelper.getInstance(requireContext());
        appExecutors = AppExecutors.getInstance();

        setupRecyclerView();

        return view;
    }

    private void setupRecyclerView() {
        rvSavedRecipes.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new RecipeHomeAdapter(getContext());
        rvSavedRecipes.setAdapter(adapter);

        adapter.setOnItemClickListener(recipe -> {
            Intent intent = new Intent(getContext(), DetailRecipeActivity.class);
            intent.putExtra("recipeId", recipe.getId());
            intent.putExtra("isSavedRecipe", true);
            startActivity(intent);
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        loadSavedRecipes();
    }

    private void loadSavedRecipes() {
        progressBar.setVisibility(View.VISIBLE);
        tvEmptyMessage.setVisibility(View.GONE);
        rvSavedRecipes.setVisibility(View.GONE);

        appExecutors.diskIO().execute(() -> {
            try {
                // 1. Mencoba mengambil data resep dari database
                final List<Recipe> savedRecipes = databaseHelper.getAllSavedRecipes();

                // 2. Jika berhasil, pindah ke UI thread untuk menampilkan data
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        progressBar.setVisibility(View.GONE);
                        if (savedRecipes != null && !savedRecipes.isEmpty()) {
                            adapter.setRecipes(savedRecipes);
                            rvSavedRecipes.setVisibility(View.VISIBLE);
                            tvEmptyMessage.setVisibility(View.GONE);
                        } else {
                            adapter.setRecipes(new ArrayList<>());
                            rvSavedRecipes.setVisibility(View.GONE);
                            tvEmptyMessage.setText("Tidak ada resep yang disimpan.");
                            tvEmptyMessage.setVisibility(View.VISIBLE);
                        }
                    });
                }
            } catch (Exception e) {
                // 3. PENTING: Jika terjadi error saat mengambil data...
                Log.e("SavedRecipesFragment", "Error loading saved recipes from DB", e);

                // 4. ...tetap pindah ke UI thread untuk memberitahu pengguna dan menyembunyikan progress bar
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        progressBar.setVisibility(View.GONE);
                        rvSavedRecipes.setVisibility(View.GONE);
                        tvEmptyMessage.setText("Gagal memuat resep tersimpan.");
                        tvEmptyMessage.setVisibility(View.VISIBLE);
                        Toast.makeText(getContext(), "Terjadi kesalahan saat memuat data.", Toast.LENGTH_SHORT).show();
                    });
                }
            }
        });
    }
}
