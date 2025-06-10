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
import com.example.projek.R; // R.java akan di-generate di package root
import com.example.projek.Model.Recipe; // Perhatikan import package model Anda

import java.util.ArrayList;
import java.util.List;

public class RecipeHomeAdapter extends RecyclerView.Adapter<RecipeHomeAdapter.RecipeViewHolder> {

    // --- BAGIAN INI DIHAPUS KARENA TIDAK LAGI DIPERLUKAN ---
    // public static final int MODE_HOME_SAVED = 0;
    // public static final int MODE_SEARCH = 1;
    // private int displayMode;
    // --- AKHIR BAGIAN DIHAPUS ---

    private final Context context;
    private List<Recipe> recipes = new ArrayList<>();
    private OnItemClickListener onItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(Recipe recipe);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    // --- MODIFIKASI KONSTRUKTOR (KEMBALIKAN KE VERSI AWAL) ---
    // Konstruktor ini sekarang hanya menerima Context
    public RecipeHomeAdapter(Context context) {
        this.context = context;
        // this.displayMode tidak lagi diperlukan
    }
    // --- AKHIR MODIFIKASI KONSTRUKTOR ---


    public void setRecipes(List<Recipe> recipes) {
        this.recipes = recipes;
        notifyDataSetChanged(); // Memberi tahu adapter bahwa data telah berubah
    }

    @NonNull
    @Override
    public RecipeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_recipe_home, parent, false);
        return new RecipeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecipeViewHolder holder, int position) {
        Recipe recipe = recipes.get(position);
        holder.bind(recipe);
    }

    @Override
    public int getItemCount() {
        return recipes.size();
    }

    class RecipeViewHolder extends RecyclerView.ViewHolder {
        ImageView ivRecipeImage;
        TextView tvRecipeTitle;
        TextView tvReadyInMinutes;
        TextView tvMealType;
        TextView tvServing;

        public RecipeViewHolder(@NonNull View itemView) {
            super(itemView);
            ivRecipeImage = itemView.findViewById(R.id.iv_recipe_image_home);
            tvRecipeTitle = itemView.findViewById(R.id.tv_recipe_title_home);
            tvReadyInMinutes = itemView.findViewById(R.id.tv_ready_in_minutes_home);
            tvMealType = itemView.findViewById(R.id.tv_meal_type_home);
            tvServing = itemView.findViewById(R.id.tv_serving_home);

            // Set OnClickListener untuk setiap item RecyclerView
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
            // Memuat gambar menggunakan Glide
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

            // --- LOGIKA TAMPILAN UNTUK HOME DAN SAVED (KEMBALIKAN KE VERSI AWAL) ---
            // Waktu Pembuatan (Tampilkan "-" jika null/0)
            tvReadyInMinutes.setText("Waktu: " + (recipe.getReadyInMinutes() != null && recipe.getReadyInMinutes() > 0 ? recipe.getReadyInMinutes() : "-") + " menit");
            tvReadyInMinutes.setVisibility(View.VISIBLE); // Selalu terlihat di Home/Saved

            // Tipe Makanan (Tampilkan "Tidak Diketahui" jika null/kosong)
            String mealType = recipe.getMealType();
            tvMealType.setText("Tipe: " + (mealType != null && !mealType.isEmpty() && !mealType.equalsIgnoreCase("Tidak Diketahui") ? mealType : "Tidak Diketahui"));
            tvMealType.setVisibility(View.VISIBLE); // Selalu terlihat di Home/Saved

            // Porsi (Tampilkan "-" jika null/0)
            tvServing.setText("Porsi: " + (recipe.getServings() != null && recipe.getServings() > 0 ? recipe.getServings() : "-"));
            tvServing.setVisibility(View.VISIBLE); // Selalu terlihat di Home/Saved
            // --- AKHIR LOGIKA TAMPILAN ---
        }
    }
}