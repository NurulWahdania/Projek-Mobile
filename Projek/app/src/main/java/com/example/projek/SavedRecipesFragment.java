package com.example.projek;

import android.content.Intent;
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
import android.widget.TextView;
import android.widget.Toast;

import com.example.projek.Adapter.RecipeHomeAdapter;
import com.example.projek.Helper.DatabaseHelper;
import com.example.projek.Model.Recipe;

import java.util.List;

public class SavedRecipesFragment extends Fragment {

    private RecyclerView rvSavedRecipes;
    private TextView tvNoSavedRecipes;
    private RecipeHomeAdapter adapter;
    private DatabaseHelper databaseHelper;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_saved_recipes, container, false);

        rvSavedRecipes = view.findViewById(R.id.rv_saved_recipes);
        tvNoSavedRecipes = view.findViewById(R.id.tv_no_saved_recipes);

        databaseHelper = new DatabaseHelper(requireContext());

        rvSavedRecipes.setLayoutManager(new LinearLayoutManager(requireContext()));
        // --- MODIFIKASI BARIS INI ---
        adapter = new RecipeHomeAdapter(getContext()); // <--- UBAH DI SINI
        // --- AKHIR MODIFIKASI ---
        rvSavedRecipes.setAdapter(adapter);

        adapter.setOnItemClickListener(recipe -> {
            Log.d("SavedFragment", "Mengklik resep untuk DetailActivity - ID: " + recipe.getId());
            Intent intent = new Intent(requireContext(), DetailRecipeActivity.class);
            intent.putExtra("recipeId", recipe.getId());
            startActivity(intent);
        });

        loadSavedRecipes();

        return view;
    }

    private void loadSavedRecipes() {
        databaseHelper.getAllSavedRecipes().observe(getViewLifecycleOwner(), recipes -> {
            Log.d("SavedFragment", "Jumlah resep tersimpan dimuat dari DB: " + (recipes != null ? recipes.size() : "null"));
            if (recipes != null && !recipes.isEmpty()) {
                for (Recipe r : recipes) {
                    Log.d("SavedFragment", "Resep dari DB - ID: " + r.getId() + ", Judul: " + r.getTitle());
                }
                adapter.setRecipes(recipes);
                rvSavedRecipes.setVisibility(View.VISIBLE);
                tvNoSavedRecipes.setVisibility(View.GONE);
            } else {
                adapter.setRecipes(recipes);
                rvSavedRecipes.setVisibility(View.GONE);
                tvNoSavedRecipes.setVisibility(View.VISIBLE);
            }
        });
    }
}