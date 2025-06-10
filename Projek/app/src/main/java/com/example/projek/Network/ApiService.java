package com.example.projek.Network;

import com.example.projek.Model.Recipe;
import com.example.projek.Model.RecipeResults;
import com.example.projek.Utils.Constants; // Import Constants untuk API_KEY dan API_HOST

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {

    @Headers({
            "x-rapidapi-key: " + Constants.API_KEY, // Mengambil API_KEY dari Constants
            "x-rapidapi-host: " + Constants.API_HOST // Mengambil API_HOST dari Constants
    })
    @GET("recipes/random")
    Call<RecipeResults> getRandomRecipes(@Query("number") int number, @Query("tags") String tags);

    @Headers({
            "x-rapidapi-key: " + Constants.API_KEY,
            "x-rapidapi-host: " + Constants.API_HOST
    })
    @GET("recipes/{id}/information")
    Call<Recipe> getRecipeDetails(@Path("id") int id);

    @Headers({
            "x-rapidapi-key: " + Constants.API_KEY,
            "x-rapidapi-host: " + Constants.API_HOST
    })
    @GET("recipes/complexSearch")
    Call<RecipeResults> searchRecipes(
            @Query("query") String query,
            @Query("diet") String diet, // Bisa kosong
            @Query("intolerances") String intolerances, // Bisa kosong
            @Query("number") int number // Jumlah hasil
    );

    @Headers({
            "x-rapidapi-key: " + Constants.API_KEY,
            "x-rapidapi-host: " + Constants.API_HOST
    })
    @GET("recipes/complexSearch")
    Call<ApiResponse> searchRecipes(
            @Query("query") String query,
            @Query("number") int number,
            @Query("offset") int offset
    );

}