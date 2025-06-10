package com.example.projek.Network;

import com.example.projek.Model.Recipe;
import com.google.gson.annotations.SerializedName;
import java.util.List;

/**
 * PERBAIKAN: Kelas ini diubah dari generik menjadi spesifik untuk menangani
 * respons dari endpoint pencarian resep (seperti /complexSearch atau /random).
 * Ini akan memastikan ada metode .getResults() yang dibutuhkan.
 */
public class ApiResponse {

    // Menggunakan @SerializedName untuk mencocokkan kunci "results" dari JSON API
    // (untuk endpoint /complexSearch)
    @SerializedName("results")
    private List<Recipe> results;

    // Tambahkan juga @SerializedName untuk "recipes" sebagai fallback untuk endpoint /random
    @SerializedName("recipes")
    private List<Recipe> recipesList;

    // Metode ini akan mencoba mengambil dari 'results', jika tidak ada, akan mengambil dari 'recipes'
    public List<Recipe> getRecipes() {
        if (results != null) {
            return results;
        }
        return recipesList;
    }

    // Ubah nama getter agar konsisten dan sesuai dengan pemanggilan di HomeFragment
    public List<Recipe> getResults() {
        return getRecipes();
    }

    public void setResults(List<Recipe> results) {
        this.results = results;
    }

    public void setRecipesList(List<Recipe> recipesList) {
        this.recipesList = recipesList;
    }
}