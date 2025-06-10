package com.example.projek.Helper;

import android.content.Context;
import androidx.lifecycle.LiveData;

import com.example.projek.Database.AppDatabase;
import com.example.projek.Database.RecipeDao;
import com.example.projek.Model.Recipe;
import com.example.projek.Utils.AppExecutors;

import java.util.List;

public class DatabaseHelper {

    private final RecipeDao recipeDao;
    private final AppExecutors appExecutors;

    public DatabaseHelper(Context context) {
        AppDatabase db = AppDatabase.getDatabase(context);
        recipeDao = db.recipeDao();
        appExecutors = AppExecutors.getInstance();
    }

    public void insertRecipe(Recipe recipe) {
        appExecutors.diskIO().execute(() -> recipeDao.insert(recipe));
    }

    public void deleteRecipe(Recipe recipe) {
        appExecutors.diskIO().execute(() -> recipeDao.delete(recipe));
    }

    public LiveData<List<Recipe>> getAllSavedRecipes() {
        return recipeDao.getAllSavedRecipes();
    }

    public LiveData<Boolean> isRecipeSaved(int recipeId) {
        return recipeDao.isRecipeSaved(recipeId);
    }
}