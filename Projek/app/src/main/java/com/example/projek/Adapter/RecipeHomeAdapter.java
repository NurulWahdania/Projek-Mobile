package com.example.projek.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.projek.Model.Recipe;
import com.example.projek.R;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class RecipeHomeAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final Context context;
    private List<Recipe> recipes;
    private OnItemClickListener onItemClickListener;

    private static final int VIEW_TYPE_ITEM = 0;
    private static final int VIEW_TYPE_LOADING = 1;
    private boolean isLoadingAdded = false;

    public interface OnItemClickListener {
        void onItemClick(Recipe recipe);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    public RecipeHomeAdapter(Context context) {
        this.context = context;
        this.recipes = new ArrayList<>();
    }

    public void setRecipes(List<Recipe> recipes) {
        this.recipes = new ArrayList<>(recipes);
        notifyDataSetChanged();
    }

    public void addAll(List<Recipe> newRecipes) {
        int lastIndex = this.recipes.size();
        this.recipes.addAll(newRecipes);
        notifyItemRangeInserted(lastIndex, newRecipes.size());
    }

    // Metode privat untuk menambahkan item ke list
    private void add(Recipe recipe) {
        recipes.add(recipe);
        notifyItemInserted(recipes.size() - 1);
    }

    public void addLoadingFooter() {
        if (!isLoadingAdded) {
            isLoadingAdded = true;
            // PERBAIKAN: Gunakan 'add(null)' bukan 'add(new Recipe())'
            // untuk menambahkan placeholder.
            add(null);
        }
    }

    public void removeLoadingFooter() {
        if (isLoadingAdded && !recipes.isEmpty()) {
            isLoadingAdded = false;
            int position = recipes.size() - 1;
            Recipe item = recipes.get(position);
            // Cek apakah item terakhir adalah placeholder (null)
            if (item == null) {
                recipes.remove(position);
                notifyItemRemoved(position);
            }
        }
    }

    @Override
    public int getItemCount() {
        return recipes == null ? 0 : recipes.size();
    }

    @Override
    public int getItemViewType(int position) {
        return (position == recipes.size() - 1 && isLoadingAdded) ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        if (viewType == VIEW_TYPE_ITEM) {
            View view = inflater.inflate(R.layout.item_recipe_home, parent, false);
            return new RecipeViewHolder(view);
        } else { // VIEW_TYPE_LOADING
            View view = inflater.inflate(R.layout.item_loading, parent, false);
            return new LoadingViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        // Hanya bind data jika itu adalah RecipeViewHolder
        if (getItemViewType(position) == VIEW_TYPE_ITEM) {
            Recipe recipe = recipes.get(position);
            ((RecipeViewHolder) holder).bind(recipe);
        }
    }

    class RecipeViewHolder extends RecyclerView.ViewHolder {
        ImageView ivRecipeImage;
        TextView tvRecipeTitle, tvReadyInMinutes, tvMealType, tvServing;

        public RecipeViewHolder(@NonNull View itemView) {
            super(itemView);
            ivRecipeImage = itemView.findViewById(R.id.iv_recipe_image_home);
            tvRecipeTitle = itemView.findViewById(R.id.tv_recipe_title_home);
            tvReadyInMinutes = itemView.findViewById(R.id.tv_ready_in_minutes_home);
            tvMealType = itemView.findViewById(R.id.tv_meal_type_home);
            tvServing = itemView.findViewById(R.id.tv_serving_home);

            itemView.setOnClickListener(v -> {
                if (onItemClickListener != null) {
                    int pos = getAdapterPosition();
                    if (pos != RecyclerView.NO_POSITION) {
                        onItemClickListener.onItemClick(recipes.get(pos));
                    }
                }
            });
        }

        public void bind(Recipe recipe) {
            tvRecipeTitle.setText(recipe.getTitle());

            Integer minutes = recipe.getReadyInMinutes();
            tvReadyInMinutes.setText(String.format(Locale.getDefault(), "Waktu: %s menit", (minutes != null && minutes > 0) ? minutes.toString() : "-"));

            String mealType = recipe.getMealType();
            tvMealType.setText(String.format("Tipe: %s", (mealType != null && !mealType.isEmpty()) ? mealType : "Umum"));

            Integer servings = recipe.getServings();
            tvServing.setText(String.format(Locale.getDefault(), "Porsi: %s", (servings != null && servings > 0) ? servings.toString() : "-"));

            Glide.with(context)
                    .load(recipe.getImage())
                    .placeholder(R.drawable.ic_placeholder)
                    .error(R.drawable.ic_error)
                    .into(ivRecipeImage);
        }
    }

    private static class LoadingViewHolder extends RecyclerView.ViewHolder {
        public LoadingViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}