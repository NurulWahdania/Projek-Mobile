package com.example.projek.Adapter; // Pastikan package ini sesuai

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

public class SearchResultAdapter extends RecyclerView.Adapter<SearchResultAdapter.SearchViewHolder> {

    private final Context context;
    private List<Recipe> recipes = new ArrayList<>();
    private OnItemClickListener onItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(Recipe recipe);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    public SearchResultAdapter(Context context) {
        this.context = context;
    }

    public void setRecipes(List<Recipe> recipes) {
        this.recipes = recipes;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public SearchViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate layout item_recipe_search.xml yang khusus untuk pencarian
        View view = LayoutInflater.from(context).inflate(R.layout.item_recipe_search, parent, false);
        return new SearchViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchViewHolder holder, int position) {
        Recipe recipe = recipes.get(position);
        holder.bind(recipe);
    }

    @Override
    public int getItemCount() {
        return recipes.size();
    }

    // ViewHolder khusus untuk tampilan pencarian
    class SearchViewHolder extends RecyclerView.ViewHolder {
        ImageView ivRecipeImage;
        TextView tvRecipeTitle;
        // Tidak ada TextView untuk waktu, tipe, porsi di sini karena mereka tidak ada di item_recipe_search.xml

        public SearchViewHolder(@NonNull View itemView) {
            super(itemView);
            ivRecipeImage = itemView.findViewById(R.id.iv_recipe_image_home); // Menggunakan ID yang sama dari item_recipe_home
            tvRecipeTitle = itemView.findViewById(R.id.tv_recipe_title_home); // Menggunakan ID yang sama dari item_recipe_home

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
            // Hanya memuat gambar dan judul
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

            // Tidak perlu ada logika untuk menyembunyikan elemen lain karena layoutnya memang tidak punya
        }
    }
}