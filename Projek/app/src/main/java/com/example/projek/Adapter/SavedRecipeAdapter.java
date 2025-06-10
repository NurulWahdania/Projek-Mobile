package com.example.projek.Adapter;

// Jika Anda ingin mengimplementasikan adapter terpisah untuk saved recipes,
// kodenya akan mirip dengan RecipeHomeAdapter.
// Jika tidak, Anda bisa menghapus file ini.
// Contoh minimal jika ingin tetap ada:

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.projek.R;
import com.example.projek.Model.Recipe;

import java.util.ArrayList;
import java.util.List;

public class SavedRecipeAdapter extends RecyclerView.Adapter<SavedRecipeAdapter.SavedRecipeViewHolder> {

    private final Context context;
    private List<Recipe> recipes = new ArrayList<>();
    private OnItemClickListener onItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(Recipe recipe);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    public SavedRecipeAdapter(Context context) {
        this.context = context;
    }

    public void setRecipes(List<Recipe> recipes) {
        this.recipes = recipes;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public SavedRecipeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_recipe_home, parent, false); // Asumsi menggunakan layout yang sama
        return new SavedRecipeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SavedRecipeViewHolder holder, int position) {
        Recipe recipe = recipes.get(position);
        holder.bind(recipe);
    }

    @Override
    public int getItemCount() {
        return recipes.size();
    }

    class SavedRecipeViewHolder extends RecyclerView.ViewHolder {
        ImageView ivRecipeImage;
        TextView tvRecipeTitle;
        TextView tvReadyInMinutes;
        TextView tvMealType;
        TextView tvServing;

        public SavedRecipeViewHolder(@NonNull View itemView) {
            super(itemView);
            ivRecipeImage = itemView.findViewById(R.id.iv_recipe_image_home);
            tvRecipeTitle = itemView.findViewById(R.id.tv_recipe_title_home);
            tvReadyInMinutes = itemView.findViewById(R.id.tv_ready_in_minutes_home);
            tvMealType = itemView.findViewById(R.id.tv_meal_type_home);
            tvServing = itemView.findViewById(R.id.tv_serving_home);

            itemView.setOnClickListener(v -> {
                if (onItemClickListener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        onItemClickListener.onItemClick(recipes.get(position));
                    }
                }
            });
        }

        public void bind(Recipe recipe) {
            if (recipe.getImage() != null && !recipe.getImage().isEmpty()) {
                Glide.with(context)
                        .load(recipe.getImage())
                        .placeholder(R.drawable.ic_placeholder)
                        .error(R.drawable.ic_error)
                        .into(ivRecipeImage);
            } else {
                ivRecipeImage.setImageResource(R.drawable.ic_placeholder);
            }

            tvRecipeTitle.setText(recipe.getTitle());
            tvReadyInMinutes.setText("Waktu: " + (recipe.getReadyInMinutes() != null && recipe.getReadyInMinutes() > 0 ? recipe.getReadyInMinutes() : "-") + " menit");
            tvReadyInMinutes.setVisibility(View.VISIBLE);
            String mealType = recipe.getMealType();
            tvMealType.setText("Tipe: " + (mealType != null && !mealType.isEmpty() && !mealType.equalsIgnoreCase("Tidak Diketahui") ? mealType : "Tidak Diketahui"));
            tvMealType.setVisibility(View.VISIBLE);
            tvServing.setText("Porsi: " + (recipe.getServings() != null && recipe.getServings() > 0 ? recipe.getServings() : "-"));
            tvServing.setVisibility(View.VISIBLE);
        }
    }
}
