package com.example.projek.Model;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;
import java.util.List;
import java.util.ArrayList;

public class Recipe implements Serializable {

    @SerializedName("id")
    private int id; // ID tetap ada
    @SerializedName("image")
    private String image;
    @SerializedName("imageType")
    private String imageType;
    @SerializedName("title")
    private String title;
    @SerializedName("readyInMinutes")
    private Integer readyInMinutes;
    @SerializedName("servings")
    private Integer servings;
    @SerializedName("sourceUrl")
    private String sourceUrl;
    @SerializedName("vegetarian")
    private Boolean vegetarian;
    @SerializedName("vegan")
    private Boolean vegan;
    @SerializedName("glutenFree")
    private Boolean glutenFree;
    @SerializedName("dairyFree")
    private Boolean dairyFree;
    @SerializedName("veryHealthy")
    private Boolean veryHealthy;
    @SerializedName("cheap")
    private Boolean cheap;
    @SerializedName("veryPopular")
    private Boolean veryPopular;
    @SerializedName("sustainable")
    private Boolean sustainable;
    @SerializedName("lowFodmap")
    private Boolean lowFodmap;
    @SerializedName("weightWatcherSmartPoints")
    private Integer weightWatcherSmartPoints;
    @SerializedName("gaps")
    private String gaps;
    @SerializedName("preparationMinutes")
    private Integer preparationMinutes;
    @SerializedName("cookingMinutes")
    private Integer cookingMinutes;
    @SerializedName("aggregateLikes")
    private Integer aggregateLikes;
    @SerializedName("healthScore")
    private Double healthScore;
    @SerializedName("creditsText")
    private String creditsText;
    @SerializedName("license")
    private String license;
    @SerializedName("sourceName")
    private String sourceName;
    @SerializedName("pricePerServing")
    private Double pricePerServing;
    @SerializedName("summary")
    private String summary;

    @SerializedName("nutrition")
    private Nutrition nutrition;

    @SerializedName(value = "extendedIngredients", alternate = {"ingredients"})
    private List<Ingredient> ingredients;

    @SerializedName("analyzedInstructions")
    private List<AnalyzedInstruction> analyzedInstructions;

    @SerializedName("dishTypes")
    private List<String> dishTypes;

    @SerializedName("cuisines")
    private List<String> cuisines;

    @SerializedName("occasions")
    private List<String> occasions;

    @SerializedName("caloricBreakdown")
    private CaloricBreakdown caloricBreakdown;


    // Constructor
    public Recipe(int id, String image, String imageType, String title, Integer readyInMinutes, Integer servings, String sourceUrl, Boolean vegetarian, Boolean vegan, Boolean glutenFree, Boolean dairyFree, Boolean veryHealthy, Boolean cheap, Boolean veryPopular, Boolean sustainable, Boolean lowFodmap, Integer weightWatcherSmartPoints, String gaps, Integer preparationMinutes, Integer cookingMinutes, Integer aggregateLikes, Double healthScore, String creditsText, String license, String sourceName, Double pricePerServing, String summary, Nutrition nutrition, List<Ingredient> ingredients, List<AnalyzedInstruction> analyzedInstructions, List<String> dishTypes, List<String> cuisines, List<String> occasions, CaloricBreakdown caloricBreakdown) {
        this.id = id;
        this.image = image;
        this.imageType = imageType;
        this.title = title;
        this.readyInMinutes = readyInMinutes;
        this.servings = servings;
        this.sourceUrl = sourceUrl;
        this.vegetarian = vegetarian;
        this.vegan = vegan;
        this.glutenFree = glutenFree;
        this.dairyFree = dairyFree;
        this.veryHealthy = veryHealthy;
        this.cheap = cheap;
        this.veryPopular = veryPopular;
        this.sustainable = sustainable;
        this.lowFodmap = lowFodmap;
        this.weightWatcherSmartPoints = weightWatcherSmartPoints;
        this.gaps = gaps;
        this.preparationMinutes = preparationMinutes;
        this.cookingMinutes = cookingMinutes;
        this.aggregateLikes = aggregateLikes;
        this.healthScore = healthScore;
        this.creditsText = creditsText;
        this.license = license;
        this.sourceName = sourceName;
        this.pricePerServing = pricePerServing;
        this.summary = summary;
        this.nutrition = nutrition;
        this.ingredients = ingredients;
        this.analyzedInstructions = analyzedInstructions;
        this.dishTypes = dishTypes;
        this.cuisines = cuisines;
        this.occasions = occasions;
        this.caloricBreakdown = caloricBreakdown;
    }


    // Getters (tetap sama)
    public int getId() { return id; }
    public String getImage() { return image; }
    public String getImageType() { return imageType; }
    public String getTitle() { return title; }
    public Integer getReadyInMinutes() { return readyInMinutes; }
    public Integer getServings() { return servings; }
    public String getSourceUrl() { return sourceUrl; }
    public Boolean getVegetarian() { return vegetarian; }
    public Boolean getVegan() { return vegan; }
    public Boolean getGlutenFree() { return glutenFree; }
    public Boolean getDairyFree() { return dairyFree; }
    public Boolean getVeryHealthy() { return veryHealthy; }
    public Boolean getCheap() { return cheap; }
    public Boolean getVeryPopular() { return veryPopular; }
    public Boolean getSustainable() { return sustainable; }
    public Boolean getLowFodmap() { return lowFodmap; }
    public Integer getWeightWatcherSmartPoints() { return weightWatcherSmartPoints; }
    public String getGaps() { return gaps; }
    public Integer getPreparationMinutes() { return preparationMinutes; }
    public Integer getCookingMinutes() { return cookingMinutes; }
    public Integer getAggregateLikes() { return aggregateLikes; }
    public Double getHealthScore() { return healthScore; }
    public String getCreditsText() { return creditsText; }
    public String getLicense() { return license; }
    public String getSourceName() { return sourceName; }
    public Double getPricePerServing() { return pricePerServing; }
    public String getSummary() { return summary; }
    public Nutrition getNutrition() { return nutrition; }
    public List<Ingredient> getIngredients() { return ingredients; }
    public List<AnalyzedInstruction> getAnalyzedInstructions() { return analyzedInstructions; }
    public List<String> getDishTypes() { return dishTypes; }
    public List<String> getCuisines() { return cuisines; }
    public List<String> getOccasions() { return occasions; }
    public CaloricBreakdown getCaloricBreakdown() { return caloricBreakdown; }

    // Setters (tetap sama)
    public void setId(int id) { this.id = id; }
    public void setImage(String image) { this.image = image; }
    public void setImageType(String imageType) { this.imageType = imageType; }
    public void setTitle(String title) { this.title = title; }
    public void setReadyInMinutes(Integer readyInMinutes) { this.readyInMinutes = readyInMinutes; }
    public void setServings(Integer servings) { this.servings = servings; }
    public void setSourceUrl(String sourceUrl) { this.sourceUrl = sourceUrl; }
    public void setVegetarian(Boolean vegetarian) { this.vegetarian = vegetarian; }
    public void setVegan(Boolean vegan) { this.vegan = vegan; }
    public void setGlutenFree(Boolean glutenFree) { this.glutenFree = glutenFree; }
    public void setDairyFree(Boolean dairyFree) { this.dairyFree = dairyFree; }
    public void setVeryHealthy(Boolean veryHealthy) { this.veryHealthy = veryHealthy; }
    public void setCheap(Boolean cheap) { this.cheap = cheap; }
    public void setVeryPopular(Boolean veryPopular) { this.veryPopular = veryPopular; }
    public void setSustainable(Boolean sustainable) { this.sustainable = sustainable; }
    public void setLowFodmap(Boolean lowFodmap) { this.lowFodmap = lowFodmap; }
    public void setWeightWatcherSmartPoints(Integer weightWatcherSmartPoints) { this.weightWatcherSmartPoints = weightWatcherSmartPoints; }
    public void setGaps(String gaps) { this.gaps = gaps; }
    public void setPreparationMinutes(Integer preparationMinutes) { this.preparationMinutes = preparationMinutes; }
    public void setCookingMinutes(Integer cookingMinutes) { this.cookingMinutes = cookingMinutes; }
    public void setAggregateLikes(Integer aggregateLikes) { this.aggregateLikes = aggregateLikes; }
    public void setHealthScore(Double healthScore) { this.healthScore = healthScore; }
    public void setCreditsText(String creditsText) { this.creditsText = creditsText; }
    public void setLicense(String license) { this.license = license; }
    public void setSourceName(String sourceName) { this.sourceName = sourceName; }
    public void setPricePerServing(Double pricePerServing) { this.pricePerServing = pricePerServing; }
    public void setSummary(String summary) { this.summary = summary; }
    public void setNutrition(Nutrition nutrition) { this.nutrition = nutrition; }
    public void setIngredients(List<Ingredient> ingredients) { this.ingredients = ingredients; }
    public void setAnalyzedInstructions(List<AnalyzedInstruction> analyzedInstructions) { this.analyzedInstructions = analyzedInstructions; }
    public void setDishTypes(List<String> dishTypes) { this.dishTypes = dishTypes; }
    public void setCuisines(List<String> cuisines) { this.cuisines = cuisines; }
    public void setOccasions(List<String> occasions) { this.occasions = occasions; }
    public void setCaloricBreakdown(CaloricBreakdown caloricBreakdown) { this.caloricBreakdown = caloricBreakdown; }

    public String getMealType() {
        StringBuilder categories = new StringBuilder();
        boolean firstCategory = true;

        if (vegetarian != null && vegetarian) {
            categories.append("Vegetarian");
            firstCategory = false;
        }
        if (vegan != null && vegan) {
            if (!firstCategory) categories.append(", ");
            categories.append("Vegan");
            firstCategory = false;
        }
        if (glutenFree != null && glutenFree) {
            if (!firstCategory) categories.append(", ");
            categories.append("Gluten-Free");
            firstCategory = false;
        }
        if (dairyFree != null && dairyFree) {
            if (!firstCategory) categories.append(", ");
            categories.append("Dairy-Free");
            firstCategory = false;
        }

        if (this.dishTypes != null && !this.dishTypes.isEmpty()) {
            for (String type : this.dishTypes) {
                if (!type.equalsIgnoreCase("main course") && !type.equalsIgnoreCase("main dish") && !type.equalsIgnoreCase("lunch") && !type.equalsIgnoreCase("dinner") && !type.equalsIgnoreCase("snack")) {
                    if (!firstCategory) categories.append(", ");
                    categories.append(type);
                    firstCategory = false;
                }
            }
        }

        if (this.cuisines != null && !this.cuisines.isEmpty()) {
            for (String cuisine : this.cuisines) {
                if (!firstCategory) categories.append(", ");
                categories.append(cuisine);
                firstCategory = false;
            }
        }

        if (this.occasions != null && !this.occasions.isEmpty()) {
            for (String occasion : this.occasions) {
                if (!firstCategory) categories.append(", ");
                categories.append(occasion);
                firstCategory = false;
            }
        }

        if (categories.length() == 0) {
            return "Tidak Diketahui";
        }

        return categories.toString();
    }
}