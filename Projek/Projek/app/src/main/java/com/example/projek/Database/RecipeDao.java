package com.example.projek.Database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.projek.Model.Recipe;

import java.util.List;

@Dao
public interface RecipeDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Recipe recipe);

    @Delete
    void delete(Recipe recipe);

    @Query("SELECT * FROM saved_recipes ORDER BY title ASC")
    LiveData<List<Recipe>> getAllSavedRecipes();

    @Query("SELECT EXISTS(SELECT 1 FROM saved_recipes WHERE id = :recipeId LIMIT 1)")
    LiveData<Boolean> isRecipeSaved(int recipeId);
}
