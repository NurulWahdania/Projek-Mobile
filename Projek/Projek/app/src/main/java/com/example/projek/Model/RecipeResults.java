package com.example.projek.Model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class RecipeResults {
    // Field untuk menangani respons dari /complexSearch dan beberapa kasus /random
    @SerializedName("results")
    private List<Recipe> results;

    // Field untuk menangani respons dari /random yang mungkin menggunakan "recipes"
    @SerializedName("recipes")
    private List<Recipe> recipes; // <--- TAMBAHKAN FIELD INI

    @SerializedName("offset")
    private Integer offset;
    @SerializedName("number")
    private Integer number;
    @SerializedName("totalResults")
    private Integer totalResults;

    // Getter yang akan mengembalikan daftar resep yang tidak null/kosong
    public List<Recipe> getRecipesList() { // <--- BUAT GETTER BARU INI
        if (results != null && !results.isEmpty()) {
            return results;
        } else if (recipes != null && !recipes.isEmpty()) {
            return recipes;
        }
        return null; // Atau Collections.emptyList();
    }

    // Biarkan getter/setter lama untuk kompatibilitas jika masih ada yang memanggilnya
    public List<Recipe> getResults() {
        return results;
    }
    public void setResults(List<Recipe> results) {
        this.results = results;
    }

    public List<Recipe> getRecipes() { // <--- Tambahkan getter ini jika Anda membuat field 'recipes'
        return recipes;
    }
    public void setRecipes(List<Recipe> recipes) {
        this.recipes = recipes;
    }

    public Integer getOffset() {
        return offset;
    }
    public void setOffset(Integer offset) {
        this.offset = offset;
    }
    public Integer getNumber() {
        return number;
    }
    public void setNumber(Integer number) {
        this.number = number;
    }
    public Integer getTotalResults() {
        return totalResults;
    }
    public void setTotalResults(Integer totalResults) {
        this.totalResults = totalResults;
    }
}