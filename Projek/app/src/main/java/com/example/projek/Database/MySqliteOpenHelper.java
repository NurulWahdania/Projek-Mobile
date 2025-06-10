package com.example.projek.Database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class MySqliteOpenHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "recipe_database.db";
    private static final int DATABASE_VERSION = 1; // Pastikan ini tetap 1 jika Anda belum pernah merilis versi sebelumnya. Jika sudah, naikkan versinya.

    // Nama tabel
    public static final String TABLE_SAVED_RECIPES = "saved_recipes";
    public static final String TABLE_CONSUMED_FOOD = "consumed_food";
    public static final String TABLE_USER_PROFILE = "user_profile";

    // Kolom tabel saved_recipes
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_IMAGE = "image";
    public static final String COLUMN_IMAGE_TYPE = "imageType";
    public static final String COLUMN_TITLE = "title";
    public static final String COLUMN_READY_IN_MINUTES = "readyInMinutes";
    public static final String COLUMN_SERVINGS = "servings";
    public static final String COLUMN_SOURCE_URL = "sourceUrl";
    public static final String COLUMN_VEGETARIAN = "vegetarian";
    public static final String COLUMN_VEGAN = "vegan";
    public static final String COLUMN_GLUTEN_FREE = "glutenFree";
    public static final String COLUMN_DAIRY_FREE = "dairyFree";
    public static final String COLUMN_VERY_HEALTHY = "veryHealthy";
    public static final String COLUMN_CHEAP = "cheap";
    public static final String COLUMN_VERY_POPULAR = "veryPopular";
    public static final String COLUMN_SUSTAINABLE = "sustainable";
    public static final String COLUMN_LOW_FODMAP = "lowFodmap";
    public static final String COLUMN_WEIGHT_WATCHER_SMART_POINTS = "weightWatcherSmartPoints";
    public static final String COLUMN_GAPS = "gaps";
    public static final String COLUMN_PREPARATION_MINUTES = "preparationMinutes";
    public static final String COLUMN_COOKING_MINUTES = "cookingMinutes";
    public static final String COLUMN_AGGREGATE_LIKES = "aggregateLikes";
    public static final String COLUMN_HEALTH_SCORE = "healthScore";
    public static final String COLUMN_CREDITS_TEXT = "creditsText";
    public static final String COLUMN_LICENSE = "license";
    public static final String COLUMN_SOURCE_NAME = "sourceName";
    public static final String COLUMN_PRICE_PER_SERVING = "pricePerServing";
    public static final String COLUMN_SUMMARY = "summary";
    public static final String COLUMN_NUTRITION_JSON = "nutritionJson";
    public static final String COLUMN_INGREDIENTS_JSON = "ingredientsJson";
    public static final String COLUMN_ANALYZED_INSTRUCTIONS_JSON = "analyzedInstructionsJson";
    public static final String COLUMN_DISH_TYPES_JSON = "dishTypesJson";
    public static final String COLUMN_CUISINES_JSON = "cuisinesJson";
    public static final String COLUMN_OCCASIONS_JSON = "occasionsJson";
    public static final String COLUMN_CALORIC_BREAKDOWN_JSON = "caloricBreakdownJson";

    // Kolom tabel consumed_food
    public static final String COLUMN_CONSUMED_ID = "consumedId";
    public static final String COLUMN_CONSUMED_RECIPE_ID = "recipeId";
    public static final String COLUMN_CONSUMED_TITLE = "title";
    public static final String COLUMN_CONSUMED_CALORIES = "calories";
    public static final String COLUMN_CONSUMED_DATE = "date";
    public static final String COLUMN_CONSUMED_QUANTITY = "quantity";
    public static final String COLUMN_CONSUMED_SERVING_SIZE = "servingSize";

    // Kolom tabel user_profile
    public static final String COLUMN_PROFILE_ID = "profileId";
    public static final String COLUMN_PROFILE_NAME = "name";
    public static final String COLUMN_PROFILE_AGE = "age";
    public static final String COLUMN_PROFILE_GENDER = "gender";
    public static final String COLUMN_PROFILE_WEIGHT = "weight";
    public static final String COLUMN_PROFILE_HEIGHT = "height";
    public static final String COLUMN_PROFILE_ACTIVITY_LEVEL = "activityLevel";
    public static final String COLUMN_PROFILE_TARGET_CALORIES = "targetCalories";


    // SQL untuk membuat tabel saved_recipes
    private static final String CREATE_TABLE_SAVED_RECIPES = "CREATE TABLE "
            + TABLE_SAVED_RECIPES + "("
            + COLUMN_ID + " INTEGER PRIMARY KEY,"
            + COLUMN_IMAGE + " TEXT,"
            + COLUMN_IMAGE_TYPE + " TEXT,"
            + COLUMN_TITLE + " TEXT NOT NULL,"
            + COLUMN_READY_IN_MINUTES + " INTEGER,"
            + COLUMN_SERVINGS + " INTEGER,"
            + COLUMN_SOURCE_URL + " TEXT,"
            + COLUMN_VEGETARIAN + " INTEGER,"
            + COLUMN_VEGAN + " INTEGER,"
            + COLUMN_GLUTEN_FREE + " INTEGER,"
            + COLUMN_DAIRY_FREE + " INTEGER,"
            + COLUMN_VERY_HEALTHY + " INTEGER,"
            + COLUMN_CHEAP + " INTEGER,"
            + COLUMN_VERY_POPULAR + " INTEGER,"
            + COLUMN_SUSTAINABLE + " INTEGER,"
            + COLUMN_LOW_FODMAP + " INTEGER,"
            + COLUMN_WEIGHT_WATCHER_SMART_POINTS + " INTEGER,"
            + COLUMN_GAPS + " TEXT,"
            + COLUMN_PREPARATION_MINUTES + " INTEGER,"
            + COLUMN_COOKING_MINUTES + " INTEGER,"
            + COLUMN_AGGREGATE_LIKES + " INTEGER,"
            + COLUMN_HEALTH_SCORE + " REAL,"
            + COLUMN_CREDITS_TEXT + " TEXT,"
            + COLUMN_LICENSE + " TEXT,"
            + COLUMN_SOURCE_NAME + " TEXT,"
            + COLUMN_PRICE_PER_SERVING + " REAL,"
            + COLUMN_SUMMARY + " TEXT,"
            + COLUMN_NUTRITION_JSON + " TEXT,"
            + COLUMN_INGREDIENTS_JSON + " TEXT,"
            + COLUMN_ANALYZED_INSTRUCTIONS_JSON + " TEXT,"
            + COLUMN_DISH_TYPES_JSON + " TEXT,"
            + COLUMN_CUISINES_JSON + " TEXT,"
            + COLUMN_OCCASIONS_JSON + " TEXT,"
            + COLUMN_CALORIC_BREAKDOWN_JSON + " TEXT" + ");";

    // SQL untuk membuat tabel consumed_food
    private static final String CREATE_TABLE_CONSUMED_FOOD = "CREATE TABLE "
            + TABLE_CONSUMED_FOOD + "("
            + COLUMN_CONSUMED_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + COLUMN_CONSUMED_RECIPE_ID + " INTEGER NOT NULL,"
            + COLUMN_CONSUMED_TITLE + " TEXT NOT NULL,"
            + COLUMN_CONSUMED_CALORIES + " REAL NOT NULL,"
            + COLUMN_CONSUMED_DATE + " TEXT NOT NULL,"
            + COLUMN_CONSUMED_QUANTITY + " REAL,"
            + COLUMN_CONSUMED_SERVING_SIZE + " TEXT,"
            + "FOREIGN KEY(" + COLUMN_CONSUMED_RECIPE_ID + ") REFERENCES "
            + TABLE_SAVED_RECIPES + "(" + COLUMN_ID + ") ON DELETE CASCADE" + ");";

    // SQL untuk membuat tabel user_profile
    private static final String CREATE_TABLE_USER_PROFILE = "CREATE TABLE "
            + TABLE_USER_PROFILE + "("
            + COLUMN_PROFILE_ID + " INTEGER PRIMARY KEY,"
            + COLUMN_PROFILE_NAME + " TEXT,"
            + COLUMN_PROFILE_AGE + " INTEGER,"
            + COLUMN_PROFILE_GENDER + " TEXT,"
            + COLUMN_PROFILE_WEIGHT + " REAL,"
            + COLUMN_PROFILE_HEIGHT + " REAL,"
            + COLUMN_PROFILE_ACTIVITY_LEVEL + " TEXT,"
            + COLUMN_PROFILE_TARGET_CALORIES + " REAL" + ");";


    public MySqliteOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL(CREATE_TABLE_SAVED_RECIPES);
        database.execSQL(CREATE_TABLE_CONSUMED_FOOD);
        database.execSQL(CREATE_TABLE_USER_PROFILE);
        Log.d("MySqliteOpenHelper", "All tables created in " + DATABASE_NAME);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(MySqliteOpenHelper.class.getName(),
                "Upgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data.");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CONSUMED_FOOD);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER_PROFILE);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SAVED_RECIPES);
        onCreate(db);
    }
}