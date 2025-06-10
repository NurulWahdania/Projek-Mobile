package com.example.projek.Helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.projek.Database.MySqliteOpenHelper;
import com.example.projek.Model.AnalyzedInstruction;
import com.example.projek.Model.CaloricBreakdown;
import com.example.projek.Model.ConsumedFoodItem;
import com.example.projek.Model.Ingredient;
import com.example.projek.Model.Nutrition;
import com.example.projek.Model.Recipe;
import com.example.projek.Model.UserProfile;
import com.example.projek.Utils.AppExecutors;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.ExecutorService;

public class DatabaseHelper {

    // ... (Bagian atas dari Singleton instance hingga closeDatabaseConnection tidak berubah) ...
    private static DatabaseHelper sInstance;
    private static final Object LOCK = new Object();

    private MySqliteOpenHelper dbHelper;
    private SQLiteDatabase database;
    private Gson gson;
    private final AppExecutors appExecutors;
    private final Context applicationContext;

    private DatabaseHelper(Context context) {
        this.applicationContext = context.getApplicationContext();
        dbHelper = new MySqliteOpenHelper(this.applicationContext);
        gson = new Gson();
        appExecutors = AppExecutors.getInstance();
    }

    public static DatabaseHelper getInstance(Context context) {
        if (sInstance == null) {
            synchronized (LOCK) {
                if (sInstance == null) {
                    sInstance = new DatabaseHelper(context);
                }
            }
        }
        return sInstance;
    }

    private SQLiteDatabase getDatabaseConnection() throws SQLException {
        if (database == null || !database.isOpen()) {
            Log.d("DatabaseHelper", "Attempting to open database connection.");
            try {
                database = dbHelper.getWritableDatabase();
                Log.d("DatabaseHelper", "Database connection obtained successfully.");
            } catch (SQLException e) {
                Log.e("DatabaseHelper", "Failed to get writable database: " + e.getMessage(), e);
                database = null;
                throw e;
            }
        } else {
            Log.d("DatabaseHelper", "Database connection already open and valid.");
        }
        return database;
    }

    public void closeDatabaseConnection() {
        if (database != null && database.isOpen()) {
            database.close();
            database = null;
            Log.d("DatabaseHelper", "Database connection closed.");
        }
    }


    // --- Metode CRUD untuk Recipe ---
    // ... (Semua metode CRUD untuk Recipe dari insertRecipe hingga cursorToRecipe tidak berubah) ...
    public long insertRecipe(Recipe recipe) {
        ContentValues values = new ContentValues();
        values.put(MySqliteOpenHelper.COLUMN_ID, recipe.getId());
        values.put(MySqliteOpenHelper.COLUMN_IMAGE, recipe.getImage());
        values.put(MySqliteOpenHelper.COLUMN_IMAGE_TYPE, recipe.getImageType());
        values.put(MySqliteOpenHelper.COLUMN_TITLE, recipe.getTitle());
        values.put(MySqliteOpenHelper.COLUMN_READY_IN_MINUTES, recipe.getReadyInMinutes());
        values.put(MySqliteOpenHelper.COLUMN_SERVINGS, recipe.getServings());
        values.put(MySqliteOpenHelper.COLUMN_SOURCE_URL, recipe.getSourceUrl());
        values.put(MySqliteOpenHelper.COLUMN_VEGETARIAN, recipe.getVegetarian() != null && recipe.getVegetarian() ? 1 : 0);
        values.put(MySqliteOpenHelper.COLUMN_VEGAN, recipe.getVegan() != null && recipe.getVegan() ? 1 : 0);
        values.put(MySqliteOpenHelper.COLUMN_GLUTEN_FREE, recipe.getGlutenFree() != null && recipe.getGlutenFree() ? 1 : 0);
        values.put(MySqliteOpenHelper.COLUMN_DAIRY_FREE, recipe.getDairyFree() != null && recipe.getDairyFree() ? 1 : 0);
        values.put(MySqliteOpenHelper.COLUMN_VERY_HEALTHY, recipe.getVeryHealthy() != null && recipe.getVeryHealthy() ? 1 : 0);
        values.put(MySqliteOpenHelper.COLUMN_CHEAP, recipe.getCheap() != null && recipe.getCheap() ? 1 : 0);
        values.put(MySqliteOpenHelper.COLUMN_VERY_POPULAR, recipe.getVeryPopular() != null && recipe.getVeryPopular() ? 1 : 0);
        values.put(MySqliteOpenHelper.COLUMN_SUSTAINABLE, recipe.getSustainable() != null && recipe.getSustainable() ? 1 : 0);
        values.put(MySqliteOpenHelper.COLUMN_LOW_FODMAP, recipe.getLowFodmap() != null && recipe.getLowFodmap() ? 1 : 0);
        values.put(MySqliteOpenHelper.COLUMN_WEIGHT_WATCHER_SMART_POINTS, recipe.getWeightWatcherSmartPoints());
        values.put(MySqliteOpenHelper.COLUMN_GAPS, recipe.getGaps());
        values.put(MySqliteOpenHelper.COLUMN_PREPARATION_MINUTES, recipe.getPreparationMinutes());
        values.put(MySqliteOpenHelper.COLUMN_COOKING_MINUTES, recipe.getCookingMinutes());
        values.put(MySqliteOpenHelper.COLUMN_AGGREGATE_LIKES, recipe.getAggregateLikes());
        values.put(MySqliteOpenHelper.COLUMN_HEALTH_SCORE, recipe.getHealthScore());
        values.put(MySqliteOpenHelper.COLUMN_CREDITS_TEXT, recipe.getCreditsText());
        values.put(MySqliteOpenHelper.COLUMN_LICENSE, recipe.getLicense());
        values.put(MySqliteOpenHelper.COLUMN_SOURCE_NAME, recipe.getSourceName());
        values.put(MySqliteOpenHelper.COLUMN_PRICE_PER_SERVING, recipe.getPricePerServing());
        values.put(MySqliteOpenHelper.COLUMN_SUMMARY, recipe.getSummary());

        values.put(MySqliteOpenHelper.COLUMN_NUTRITION_JSON, gson.toJson(recipe.getNutrition()));
        values.put(MySqliteOpenHelper.COLUMN_INGREDIENTS_JSON, gson.toJson(recipe.getIngredients()));
        values.put(MySqliteOpenHelper.COLUMN_ANALYZED_INSTRUCTIONS_JSON, gson.toJson(recipe.getAnalyzedInstructions()));
        values.put(MySqliteOpenHelper.COLUMN_DISH_TYPES_JSON, gson.toJson(recipe.getDishTypes()));
        values.put(MySqliteOpenHelper.COLUMN_CUISINES_JSON, gson.toJson(recipe.getCuisines()));
        values.put(MySqliteOpenHelper.COLUMN_OCCASIONS_JSON, gson.toJson(recipe.getOccasions()));
        values.put(MySqliteOpenHelper.COLUMN_CALORIC_BREAKDOWN_JSON, gson.toJson(recipe.getCaloricBreakdown()));

        long insertId = -1;
        try {
            SQLiteDatabase db = getDatabaseConnection(); // Panggil metode ini
            insertId = db.insertWithOnConflict(MySqliteOpenHelper.TABLE_SAVED_RECIPES, null, values, SQLiteDatabase.CONFLICT_REPLACE);
            Log.d("DatabaseHelper", "Recipe inserted/updated with id: " + insertId);
        } catch (android.database.SQLException e) {
            Log.e("DatabaseHelper", "SQL Error inserting/updating Recipe: " + e.getMessage(), e);
        } catch (Exception e) {
            Log.e("DatabaseHelper", "Generic Error inserting/updating Recipe: " + e.getMessage(), e);
        }
        return insertId;
    }

    public boolean deleteRecipe(int recipeId) {
        int rowsAffected = 0;
        try {
            SQLiteDatabase db = getDatabaseConnection();
            rowsAffected = db.delete(MySqliteOpenHelper.TABLE_SAVED_RECIPES,
                    MySqliteOpenHelper.COLUMN_ID + " = " + recipeId, null);
            if (rowsAffected > 0) {
                Log.d("DatabaseHelper", "Recipe deleted: " + recipeId);
            } else {
                Log.d("DatabaseHelper", "Recipe not found for deletion: " + recipeId);
            }
        } catch (android.database.SQLException e) {
            Log.e("DatabaseHelper", "SQL Error deleting Recipe: " + e.getMessage(), e);
        } catch (Exception e) {
            Log.e("DatabaseHelper", "Generic Error deleting Recipe: " + e.getMessage(), e);
        }
        return rowsAffected > 0;
    }

    public List<Recipe> getAllSavedRecipes() {
        List<Recipe> recipes = new ArrayList<>();
        Cursor cursor = null;
        try {
            SQLiteDatabase db = getDatabaseConnection();
            cursor = db.query(
                    MySqliteOpenHelper.TABLE_SAVED_RECIPES,
                    null, null, null, null, null, null
            );

            if (cursor != null) {
                cursor.moveToFirst();
                while (!cursor.isAfterLast()) {
                    Recipe recipe = cursorToRecipe(cursor);
                    recipes.add(recipe);
                    cursor.moveToNext();
                }
            }
            Log.d("DatabaseHelper", "Retrieved " + recipes.size() + " recipes.");
        } catch (android.database.SQLException e) {
            Log.e("DatabaseHelper", "SQL Error getting all saved recipes: " + e.getMessage(), e);
        } catch (Exception e) {
            Log.e("DatabaseHelper", "Generic Error getting all saved recipes: " + e.getMessage(), e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return recipes;
    }

    public boolean isRecipeSaved(int recipeId) {
        Cursor cursor = null;
        boolean isSaved = false;
        try {
            SQLiteDatabase db = getDatabaseConnection();
            cursor = db.query(MySqliteOpenHelper.TABLE_SAVED_RECIPES,
                    new String[]{MySqliteOpenHelper.COLUMN_ID},
                    MySqliteOpenHelper.COLUMN_ID + " = ?",
                    new String[]{String.valueOf(recipeId)},
                    null, null, null);

            if (cursor != null) {
                isSaved = cursor.getCount() > 0;
            }
        } catch (android.database.SQLException e) {
            Log.e("DatabaseHelper", "SQL Error checking if recipe is saved: " + e.getMessage(), e);
        } catch (Exception e) {
            Log.e("DatabaseHelper", "Generic Error checking if recipe is saved: " + e.getMessage(), e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return isSaved;
    }

    public Recipe getRecipeById(int recipeId) {
        Recipe recipe = null;
        Cursor cursor = null;
        Log.d("DatabaseHelper", "Executing getRecipeById for ID: " + recipeId);
        try {
            SQLiteDatabase db = getDatabaseConnection();
            cursor = db.query(MySqliteOpenHelper.TABLE_SAVED_RECIPES,
                    null,
                    MySqliteOpenHelper.COLUMN_ID + " = ?", // Gunakan '?' untuk keamanan
                    new String[]{String.valueOf(recipeId)},
                    null, null, null);

            if (cursor != null && cursor.moveToFirst()) {
                recipe = cursorToRecipe(cursor);
                Log.d("DatabaseHelper", "Recipe found by ID: " + recipeId);
            } else {
                Log.d("DatabaseHelper", "Recipe not found by ID: " + recipeId);
            }
        } catch (Exception e) {
            Log.e("DatabaseHelper", "Error in getRecipeById", e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return recipe;
    }

    private Recipe cursorToRecipe(Cursor cursor) {
        int idColumn = cursor.getColumnIndex(MySqliteOpenHelper.COLUMN_ID);
        int imageColumn = cursor.getColumnIndex(MySqliteOpenHelper.COLUMN_IMAGE);
        int imageTypeColumn = cursor.getColumnIndex(MySqliteOpenHelper.COLUMN_IMAGE_TYPE);
        int titleColumn = cursor.getColumnIndex(MySqliteOpenHelper.COLUMN_TITLE);
        int readyInMinutesColumn = cursor.getColumnIndex(MySqliteOpenHelper.COLUMN_READY_IN_MINUTES);
        int servingsColumn = cursor.getColumnIndex(MySqliteOpenHelper.COLUMN_SERVINGS);
        int sourceUrlColumn = cursor.getColumnIndex(MySqliteOpenHelper.COLUMN_SOURCE_URL);
        int vegetarianColumn = cursor.getColumnIndex(MySqliteOpenHelper.COLUMN_VEGETARIAN);
        int veganColumn = cursor.getColumnIndex(MySqliteOpenHelper.COLUMN_VEGAN);
        int glutenFreeColumn = cursor.getColumnIndex(MySqliteOpenHelper.COLUMN_GLUTEN_FREE);
        int dairyFreeColumn = cursor.getColumnIndex(MySqliteOpenHelper.COLUMN_DAIRY_FREE);
        int veryHealthyColumn = cursor.getColumnIndex(MySqliteOpenHelper.COLUMN_VERY_HEALTHY);
        int cheapColumn = cursor.getColumnIndex(MySqliteOpenHelper.COLUMN_CHEAP);
        int veryPopularColumn = cursor.getColumnIndex(MySqliteOpenHelper.COLUMN_VERY_POPULAR);
        int sustainableColumn = cursor.getColumnIndex(MySqliteOpenHelper.COLUMN_SUSTAINABLE);
        int lowFodmapColumn = cursor.getColumnIndex(MySqliteOpenHelper.COLUMN_LOW_FODMAP);
        int weightWatcherSmartPointsColumn = cursor.getColumnIndex(MySqliteOpenHelper.COLUMN_WEIGHT_WATCHER_SMART_POINTS);
        int gapsColumn = cursor.getColumnIndex(MySqliteOpenHelper.COLUMN_GAPS);
        int preparationMinutesColumn = cursor.getColumnIndex(MySqliteOpenHelper.COLUMN_PREPARATION_MINUTES);
        int cookingMinutesColumn = cursor.getColumnIndex(MySqliteOpenHelper.COLUMN_COOKING_MINUTES);
        int aggregateLikesColumn = cursor.getColumnIndex(MySqliteOpenHelper.COLUMN_AGGREGATE_LIKES);
        int healthScoreColumn = cursor.getColumnIndex(MySqliteOpenHelper.COLUMN_HEALTH_SCORE);
        int creditsTextColumn = cursor.getColumnIndex(MySqliteOpenHelper.COLUMN_CREDITS_TEXT);
        int licenseColumn = cursor.getColumnIndex(MySqliteOpenHelper.COLUMN_LICENSE);
        int sourceNameColumn = cursor.getColumnIndex(MySqliteOpenHelper.COLUMN_SOURCE_NAME);
        int pricePerServingColumn = cursor.getColumnIndex(MySqliteOpenHelper.COLUMN_PRICE_PER_SERVING);
        int summaryColumn = cursor.getColumnIndex(MySqliteOpenHelper.COLUMN_SUMMARY);
        int nutritionJsonColumn = cursor.getColumnIndex(MySqliteOpenHelper.COLUMN_NUTRITION_JSON);
        int ingredientsJsonColumn = cursor.getColumnIndex(MySqliteOpenHelper.COLUMN_INGREDIENTS_JSON);
        int analyzedInstructionsJsonColumn = cursor.getColumnIndex(MySqliteOpenHelper.COLUMN_ANALYZED_INSTRUCTIONS_JSON);
        int dishTypesJsonColumn = cursor.getColumnIndex(MySqliteOpenHelper.COLUMN_DISH_TYPES_JSON);
        int cuisinesJsonColumn = cursor.getColumnIndex(MySqliteOpenHelper.COLUMN_CUISINES_JSON);
        int occasionsJsonColumn = cursor.getColumnIndex(MySqliteOpenHelper.COLUMN_OCCASIONS_JSON);
        int caloricBreakdownJsonColumn = cursor.getColumnIndex(MySqliteOpenHelper.COLUMN_CALORIC_BREAKDOWN_JSON);

        int id = (idColumn != -1) ? cursor.getInt(idColumn) : 0;
        String image = (imageColumn != -1) ? cursor.getString(imageColumn) : null;
        String imageType = (imageTypeColumn != -1) ? cursor.getString(imageTypeColumn) : null;
        String title = (titleColumn != -1) ? cursor.getString(titleColumn) : null;
        Integer readyInMinutes = (readyInMinutesColumn != -1 && !cursor.isNull(readyInMinutesColumn)) ? cursor.getInt(readyInMinutesColumn) : null;
        Integer servings = (servingsColumn != -1 && !cursor.isNull(servingsColumn)) ? cursor.getInt(servingsColumn) : null;
        String sourceUrl = (sourceUrlColumn != -1) ? cursor.getString(sourceUrlColumn) : null;

        Boolean vegetarian = (vegetarianColumn != -1 && !cursor.isNull(vegetarianColumn)) ? (cursor.getInt(vegetarianColumn) == 1) : null;
        Boolean vegan = (veganColumn != -1 && !cursor.isNull(veganColumn)) ? (cursor.getInt(veganColumn) == 1) : null;
        Boolean glutenFree = (glutenFreeColumn != -1 && !cursor.isNull(glutenFreeColumn)) ? (cursor.getInt(glutenFreeColumn) == 1) : null;
        Boolean dairyFree = (dairyFreeColumn != -1 && !cursor.isNull(dairyFreeColumn)) ? (cursor.getInt(dairyFreeColumn) == 1) : null;
        Boolean veryHealthy = (veryHealthyColumn != -1 && !cursor.isNull(veryHealthyColumn)) ? (cursor.getInt(veryHealthyColumn) == 1) : null;
        Boolean cheap = (cheapColumn != -1 && !cursor.isNull(cheapColumn)) ? (cursor.getInt(cheapColumn) == 1) : null;
        Boolean veryPopular = (veryPopularColumn != -1 && !cursor.isNull(veryPopularColumn)) ? (cursor.getInt(veryPopularColumn) == 1) : null;
        Boolean sustainable = (sustainableColumn != -1 && !cursor.isNull(sustainableColumn)) ? (cursor.getInt(sustainableColumn) == 1) : null;
        Boolean lowFodmap = (lowFodmapColumn != -1 && !cursor.isNull(lowFodmapColumn)) ? (cursor.getInt(lowFodmapColumn) == 1) : null;

        Integer weightWatcherSmartPoints = (weightWatcherSmartPointsColumn != -1 && !cursor.isNull(weightWatcherSmartPointsColumn)) ? cursor.getInt(weightWatcherSmartPointsColumn) : null;
        String gaps = (gapsColumn != -1) ? cursor.getString(gapsColumn) : null;
        Integer preparationMinutes = (preparationMinutesColumn != -1 && !cursor.isNull(preparationMinutesColumn)) ? cursor.getInt(preparationMinutesColumn) : null;
        Integer cookingMinutes = (cookingMinutesColumn != -1 && !cursor.isNull(cookingMinutesColumn)) ? cursor.getInt(cookingMinutesColumn) : null;
        Integer aggregateLikes = (aggregateLikesColumn != -1 && !cursor.isNull(aggregateLikesColumn)) ? cursor.getInt(aggregateLikesColumn) : null;
        Double healthScore = (healthScoreColumn != -1 && !cursor.isNull(healthScoreColumn)) ? cursor.getDouble(healthScoreColumn) : null;
        String creditsText = (creditsTextColumn != -1) ? cursor.getString(creditsTextColumn) : null;
        String license = (licenseColumn != -1) ? cursor.getString(licenseColumn) : null;
        String sourceName = (sourceNameColumn != -1) ? cursor.getString(sourceNameColumn) : null;
        Double pricePerServing = (pricePerServingColumn != -1 && !cursor.isNull(pricePerServingColumn)) ? cursor.getDouble(pricePerServingColumn) : null;
        String summary = (summaryColumn != -1) ? cursor.getString(summaryColumn) : null;

        Nutrition nutrition = (nutritionJsonColumn != -1 && !cursor.isNull(nutritionJsonColumn)) ? gson.fromJson(cursor.getString(nutritionJsonColumn), Nutrition.class) : null;

        Type ingredientListType = new TypeToken<List<Ingredient>>(){}.getType();
        List<Ingredient> ingredients = (ingredientsJsonColumn != -1 && !cursor.isNull(ingredientsJsonColumn)) ? gson.fromJson(cursor.getString(ingredientsJsonColumn), ingredientListType) : new ArrayList<>();

        Type analyzedInstructionListType = new TypeToken<List<AnalyzedInstruction>>(){}.getType();
        List<AnalyzedInstruction> analyzedInstructions = (analyzedInstructionsJsonColumn != -1 && !cursor.isNull(analyzedInstructionsJsonColumn)) ? gson.fromJson(cursor.getString(analyzedInstructionsJsonColumn), analyzedInstructionListType) : new ArrayList<>();

        Type stringListType = new TypeToken<List<String>>(){}.getType();
        List<String> dishTypes = (dishTypesJsonColumn != -1 && !cursor.isNull(dishTypesJsonColumn)) ? gson.fromJson(cursor.getString(dishTypesJsonColumn), stringListType) : new ArrayList<>();
        List<String> cuisines = (cuisinesJsonColumn != -1 && !cursor.isNull(cuisinesJsonColumn)) ? gson.fromJson(cursor.getString(cuisinesJsonColumn), stringListType) : new ArrayList<>();
        List<String> occasions = (occasionsJsonColumn != -1 && !cursor.isNull(occasionsJsonColumn)) ? gson.fromJson(cursor.getString(occasionsJsonColumn), stringListType) : new ArrayList<>();

        CaloricBreakdown caloricBreakdown = (caloricBreakdownJsonColumn != -1 && !cursor.isNull(caloricBreakdownJsonColumn)) ? gson.fromJson(cursor.getString(caloricBreakdownJsonColumn), CaloricBreakdown.class) : null;


        return new Recipe(id, image, imageType, title, readyInMinutes, servings, sourceUrl,
                vegetarian, vegan, glutenFree, dairyFree, veryHealthy, cheap, veryPopular, sustainable,
                lowFodmap, weightWatcherSmartPoints, gaps, preparationMinutes, cookingMinutes,
                aggregateLikes, healthScore, creditsText, license, sourceName, pricePerServing,
                summary, nutrition, ingredients, analyzedInstructions, dishTypes, cuisines, occasions,
                caloricBreakdown);
    }



    // --- Metode CRUD untuk ConsumedFoodItem ---
    // ... (Metode CRUD untuk ConsumedFoodItem dari insertConsumedFood hingga cursorToConsumedFoodItem tidak berubah) ...
    public long insertConsumedFood(ConsumedFoodItem foodItem) {
        ContentValues values = new ContentValues();
        values.put(MySqliteOpenHelper.COLUMN_CONSUMED_RECIPE_ID, foodItem.getRecipeId());
        values.put(MySqliteOpenHelper.COLUMN_CONSUMED_TITLE, foodItem.getTitle());
        values.put(MySqliteOpenHelper.COLUMN_CONSUMED_CALORIES, foodItem.getCalories());
        values.put(MySqliteOpenHelper.COLUMN_CONSUMED_DATE, foodItem.getDate());
        values.put(MySqliteOpenHelper.COLUMN_CONSUMED_QUANTITY, foodItem.getQuantity());
        values.put(MySqliteOpenHelper.COLUMN_CONSUMED_SERVING_SIZE, foodItem.getServingSize());

        long insertId = -1;
        Log.d("DatabaseHelper", "Attempting to insert ConsumedFoodItem into DB. Values: " + values.toString());
        try {
            SQLiteDatabase db = getDatabaseConnection(); // Panggil metode baru yang mengembalikan koneksi
            insertId = db.insert(MySqliteOpenHelper.TABLE_CONSUMED_FOOD, null, values);
            Log.d("DatabaseHelper", "ConsumedFoodItem added with id: " + insertId);
        } catch (android.database.SQLException e) { // Gunakan android.database.SQLException
            Log.e("DatabaseHelper", "SQL Error inserting ConsumedFoodItem: " + e.getMessage(), e);
        } catch (Exception e) {
            Log.e("DatabaseHelper", "Generic Error inserting ConsumedFoodItem: " + e.getMessage(), e);
        }
        return insertId;
    }

    public int updateConsumedFood(ConsumedFoodItem foodItem) {
        ContentValues values = new ContentValues();
        values.put(MySqliteOpenHelper.COLUMN_CONSUMED_RECIPE_ID, foodItem.getRecipeId());
        values.put(MySqliteOpenHelper.COLUMN_CONSUMED_TITLE, foodItem.getTitle());
        values.put(MySqliteOpenHelper.COLUMN_CONSUMED_CALORIES, foodItem.getCalories());
        values.put(MySqliteOpenHelper.COLUMN_CONSUMED_DATE, foodItem.getDate());
        values.put(MySqliteOpenHelper.COLUMN_CONSUMED_QUANTITY, foodItem.getQuantity());
        values.put(MySqliteOpenHelper.COLUMN_CONSUMED_SERVING_SIZE, foodItem.getServingSize());

        int rowsAffected = 0;
        try {
            SQLiteDatabase db = getDatabaseConnection();
            rowsAffected = db.update(MySqliteOpenHelper.TABLE_CONSUMED_FOOD, values,
                    MySqliteOpenHelper.COLUMN_CONSUMED_ID + " = ?",
                    new String[]{String.valueOf(foodItem.getConsumedId())});
            Log.d("DatabaseHelper", "ConsumedFoodItem updated: " + foodItem.getConsumedId() + ", rows: " + rowsAffected);
        } catch (android.database.SQLException e) {
            Log.e("DatabaseHelper", "SQL Error updating ConsumedFoodItem: " + e.getMessage(), e);
        } catch (Exception e) {
            Log.e("DatabaseHelper", "Generic Error updating ConsumedFoodItem: " + e.getMessage(), e);
        }
        return rowsAffected;
    }

    public int deleteConsumedFood(ConsumedFoodItem foodItem) {
        int rowsAffected = 0;
        try {
            SQLiteDatabase db = getDatabaseConnection();
            rowsAffected = db.delete(MySqliteOpenHelper.TABLE_CONSUMED_FOOD,
                    MySqliteOpenHelper.COLUMN_CONSUMED_ID + " = ?",
                    new String[]{String.valueOf(foodItem.getConsumedId())});
            Log.d("DatabaseHelper", "Deleted " + rowsAffected + " consumed food items.");
        } catch (android.database.SQLException e) {
            Log.e("DatabaseHelper", "SQL Error deleting ConsumedFoodItem: " + e.getMessage(), e);
        } catch (Exception e) {
            Log.e("DatabaseHelper", "Generic Error deleting ConsumedFoodItem: " + e.getMessage(), e);
        }
        return rowsAffected;
    }

    public List<ConsumedFoodItem> getConsumedFoodForDate(String date) {
        List<ConsumedFoodItem> consumedFoods = new ArrayList<>();
        Cursor cursor = null;
        Log.d("DatabaseHelper", "Attempting to getConsumedFoodForDate for date: " + date);
        try {
            SQLiteDatabase db = getDatabaseConnection();
            cursor = db.query(
                    MySqliteOpenHelper.TABLE_CONSUMED_FOOD,
                    null,
                    MySqliteOpenHelper.COLUMN_CONSUMED_DATE + " = ?",
                    new String[]{date},
                    null, null, null
            );

            if (cursor != null) {
                Log.d("DatabaseHelper", "Cursor returned for getConsumedFoodForDate. Rows: " + cursor.getCount());
                cursor.moveToFirst();
                while (!cursor.isAfterLast()) {
                    consumedFoods.add(cursorToConsumedFoodItem(cursor));
                    cursor.moveToNext();
                }
            }
            Log.d("DatabaseHelper", "Retrieved " + consumedFoods.size() + " consumed food items for date " + date);
        } catch (android.database.SQLException e) {
            Log.e("DatabaseHelper", "SQL Error getting consumed food for date: " + e.getMessage(), e);
        } catch (Exception e) {
            Log.e("DatabaseHelper", "Generic Error getting consumed food for date: " + e.getMessage(), e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return consumedFoods;
    }

    public double getTotalCaloriesForDate(String date) {
        double totalCalories = 0.0;
        Cursor cursor = null;
        Log.d("DatabaseHelper", "Attempting to getTotalCaloriesForDate for date: " + date);
        try {
            SQLiteDatabase db = getDatabaseConnection();
            cursor = db.rawQuery(
                    "SELECT SUM(" + MySqliteOpenHelper.COLUMN_CONSUMED_CALORIES + " * " + MySqliteOpenHelper.COLUMN_CONSUMED_QUANTITY + ") FROM " +
                            MySqliteOpenHelper.TABLE_CONSUMED_FOOD +
                            " WHERE " + MySqliteOpenHelper.COLUMN_CONSUMED_DATE + " = ?",
                    new String[]{date}
            );

            if (cursor != null && cursor.moveToFirst()) {
                totalCalories = cursor.getDouble(0);
            }
            Log.d("DatabaseHelper", "Total calories for date " + date + ": " + totalCalories);
        } catch (android.database.SQLException e) {
            Log.e("DatabaseHelper", "SQL Error getting total calories for date: " + e.getMessage(), e);
        } catch (Exception e) {
            Log.e("DatabaseHelper", "Generic Error getting total calories for date: " + e.getMessage(), e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return totalCalories;
    }

    private ConsumedFoodItem cursorToConsumedFoodItem(Cursor cursor) {
        Log.d("DatabaseHelper", "Inside cursorToConsumedFoodItem. Attempting to parse row.");
        try {
            int idColumn = cursor.getColumnIndex(MySqliteOpenHelper.COLUMN_CONSUMED_ID);
            int recipeIdColumn = cursor.getColumnIndex(MySqliteOpenHelper.COLUMN_CONSUMED_RECIPE_ID);
            int titleColumn = cursor.getColumnIndex(MySqliteOpenHelper.COLUMN_CONSUMED_TITLE);
            int caloriesColumn = cursor.getColumnIndex(MySqliteOpenHelper.COLUMN_CONSUMED_CALORIES);
            int dateColumn = cursor.getColumnIndex(MySqliteOpenHelper.COLUMN_CONSUMED_DATE);
            int quantityColumn = cursor.getColumnIndex(MySqliteOpenHelper.COLUMN_CONSUMED_QUANTITY);
            int servingSizeColumn = cursor.getColumnIndex(MySqliteOpenHelper.COLUMN_CONSUMED_SERVING_SIZE);

            int id = (idColumn != -1) ? cursor.getInt(idColumn) : 0;
            int recipeId = (recipeIdColumn != -1) ? cursor.getInt(recipeIdColumn) : 0;
            String title = (titleColumn != -1) ? cursor.getString(titleColumn) : null;
            double calories = (caloriesColumn != -1) ? cursor.getDouble(caloriesColumn) : 0.0;
            String date = (dateColumn != -1) ? cursor.getString(dateColumn) : null;
            double quantity = (quantityColumn != -1 && !cursor.isNull(quantityColumn)) ? cursor.getDouble(quantityColumn) : 0.0;
            String servingSize = (servingSizeColumn != -1) ? cursor.getString(servingSizeColumn) : null;

            Log.d("DatabaseHelper", "Parsed ConsumedFoodItem: ID=" + id + ", Title=" + title + ", Calories=" + calories);
            return new ConsumedFoodItem(id, recipeId, title, calories, date, quantity, servingSize);
        } catch (Exception e) {
            Log.e("DatabaseHelper", "Error in cursorToConsumedFoodItem: " + e.getMessage(), e);
            return new ConsumedFoodItem(0, 0, "Error Parsing", 0.0, "N/A", 0.0, "N/A");
        }
    }



    // =================================================================================
    // ================== PERUBAHAN UTAMA UNTUK MENGATASI DEADLOCK =====================
    // =================================================================================

    // --- Metode CRUD untuk UserProfile ---

    public long insertUserProfile(UserProfile userProfile) {
        ContentValues values = new ContentValues();
        values.put(MySqliteOpenHelper.COLUMN_PROFILE_ID, 1); // Selalu gunakan ID 1 untuk profil
        values.put(MySqliteOpenHelper.COLUMN_PROFILE_NAME, userProfile.getName());
        values.put(MySqliteOpenHelper.COLUMN_PROFILE_AGE, userProfile.getAge());
        values.put(MySqliteOpenHelper.COLUMN_PROFILE_GENDER, userProfile.getGender());
        values.put(MySqliteOpenHelper.COLUMN_PROFILE_WEIGHT, userProfile.getWeight());
        values.put(MySqliteOpenHelper.COLUMN_PROFILE_HEIGHT, userProfile.getHeight());
        values.put(MySqliteOpenHelper.COLUMN_PROFILE_ACTIVITY_LEVEL, userProfile.getActivityLevel());
        values.put(MySqliteOpenHelper.COLUMN_PROFILE_TARGET_CALORIES, userProfile.getTargetCalories());

        long insertId = -1;
        try {
            SQLiteDatabase db = getDatabaseConnection();
            // Gunakan CONFLICT_REPLACE agar jika profil sudah ada, datanya akan di-update
            insertId = db.insertWithOnConflict(MySqliteOpenHelper.TABLE_USER_PROFILE, null, values, SQLiteDatabase.CONFLICT_REPLACE);
            Log.d("DatabaseHelper", "UserProfile inserted/updated with id: " + insertId);
        } catch (android.database.SQLException e) {
            Log.e("DatabaseHelper", "SQL Error inserting/updating UserProfile: " + e.getMessage(), e);
        } catch (Exception e) {
            Log.e("DatabaseHelper", "Generic Error inserting/updating UserProfile: " + e.getMessage(), e);
        }
        return insertId;
    }

    /**
     * PERBAIKAN: Metode ini disempurnakan untuk secara spesifik mengambil profil dengan ID=1.
     * Ini adalah satu-satunya metode yang dibutuhkan untuk mengambil profil pengguna.
     * Metode ini aman untuk dipanggil dari background thread.
     */
    public UserProfile getUserProfile() {
        UserProfile userProfile = null;
        Cursor cursor = null;
        Log.d("DatabaseHelper", "Executing getUserProfile synchronously.");
        try {
            SQLiteDatabase db = getDatabaseConnection();
            cursor = db.query(
                    MySqliteOpenHelper.TABLE_USER_PROFILE,
                    null,
                    MySqliteOpenHelper.COLUMN_PROFILE_ID + " = ?", // Ambil profil dengan ID 1
                    new String[]{"1"},
                    null, null, null
            );

            if (cursor != null && cursor.moveToFirst()) {
                userProfile = cursorToUserProfile(cursor);
                Log.d("DatabaseHelper", "UserProfile retrieved synchronously.");
            } else {
                Log.d("DatabaseHelper", "No UserProfile with ID=1 found.");
            }
        } catch (android.database.SQLException e) {
            Log.e("DatabaseHelper", "SQL Error getting UserProfile: " + e.getMessage(), e);
        } catch (Exception e) {
            Log.e("DatabaseHelper", "Generic Error getting UserProfile: " + e.getMessage(), e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return userProfile;
    }

    /**
     * DIHAPUS: Metode ini adalah penyebab deadlock.
     * Ia membuat task baru di dalam task yang sudah berjalan di executor yang sama.
     * Kita tidak membutuhkannya karena getUserProfile() sudah cukup.
     */
    /*
    public Future<UserProfile> getSyncUserProfile() {
        Callable<UserProfile> callable = () -> {
            UserProfile userProfile = null;
            Cursor cursor = null;
            try {
                SQLiteDatabase db = getDatabaseConnection();
                cursor = db.query(
                        MySqliteOpenHelper.TABLE_USER_PROFILE,
                        null,
                        null,
                        null,
                        null, null, null
                );

                if (cursor != null && cursor.moveToFirst()) {
                    userProfile = cursorToUserProfile(cursor);
                    Log.d("DatabaseHelper", "UserProfile retrieved synchronously.");
                } else {
                    Log.d("DatabaseHelper", "No UserProfile found synchronously.");
                }
            } catch (android.database.SQLException e) {
                Log.e("DatabaseHelper", "SQL Error getting UserProfile synchronously: " + e.getMessage(), e);
                throw e;
            } catch (Exception e) {
                Log.e("DatabaseHelper", "Generic Error getting UserProfile synchronously: " + e.getMessage(), e);
                throw e;
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
            }
            return userProfile;
        };
        return ((ExecutorService) appExecutors.diskIO()).submit(callable);
    }
    */


    public int deleteUserProfile() {
        int rowsAffected = 0;
        try {
            SQLiteDatabase db = getDatabaseConnection();
            // Hapus semua profil (seharusnya hanya ada satu)
            rowsAffected = db.delete(MySqliteOpenHelper.TABLE_USER_PROFILE, null, null);
            Log.d("DatabaseHelper", "Deleted " + rowsAffected + " user profiles.");
        } catch (android.database.SQLException e) {
            Log.e("DatabaseHelper", "SQL Error deleting UserProfile: " + e.getMessage(), e);
        } catch (Exception e) {
            Log.e("DatabaseHelper", "Generic Error deleting UserProfile: " + e.getMessage(), e);
        }
        return rowsAffected;
    }

    private UserProfile cursorToUserProfile(Cursor cursor) {
        int nameColumn = cursor.getColumnIndex(MySqliteOpenHelper.COLUMN_PROFILE_NAME);
        int ageColumn = cursor.getColumnIndex(MySqliteOpenHelper.COLUMN_PROFILE_AGE);
        int genderColumn = cursor.getColumnIndex(MySqliteOpenHelper.COLUMN_PROFILE_GENDER);
        int weightColumn = cursor.getColumnIndex(MySqliteOpenHelper.COLUMN_PROFILE_WEIGHT);
        int heightColumn = cursor.getColumnIndex(MySqliteOpenHelper.COLUMN_PROFILE_HEIGHT);
        int activityLevelColumn = cursor.getColumnIndex(MySqliteOpenHelper.COLUMN_PROFILE_ACTIVITY_LEVEL);
        int targetCaloriesColumn = cursor.getColumnIndex(MySqliteOpenHelper.COLUMN_PROFILE_TARGET_CALORIES);

        String name = (nameColumn != -1) ? cursor.getString(nameColumn) : null;
        Integer age = (ageColumn != -1 && !cursor.isNull(ageColumn)) ? cursor.getInt(ageColumn) : null;
        String gender = (genderColumn != -1) ? cursor.getString(genderColumn) : null;
        Double weight = (weightColumn != -1 && !cursor.isNull(weightColumn)) ? cursor.getDouble(weightColumn) : null;
        Double height = (heightColumn != -1 && !cursor.isNull(heightColumn)) ? cursor.getDouble(heightColumn) : null;
        String activityLevel = (activityLevelColumn != -1) ? cursor.getString(activityLevelColumn) : null;
        Double targetCalories = (targetCaloriesColumn != -1 && !cursor.isNull(targetCaloriesColumn)) ? cursor.getDouble(targetCaloriesColumn) : null;

        return new UserProfile(name, age, gender, weight, height, activityLevel, targetCalories);
    }
}