package com.example.projek.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projek.R;
import com.example.projek.Model.ConsumedFoodItem;

import java.util.ArrayList;
import java.util.List;

public class ConsumedFoodAdapter extends RecyclerView.Adapter<ConsumedFoodAdapter.ConsumedFoodViewHolder> {

    private final Context context;
    private List<ConsumedFoodItem> foodItems = new ArrayList<>();
    private OnItemDeleteListener onItemDeleteListener;
    private OnItemEditListener onItemEditListener;

    public interface OnItemDeleteListener {
        void onItemDelete(ConsumedFoodItem foodItem);
    }

    public interface OnItemEditListener {
        void onItemEdit(ConsumedFoodItem foodItem);
    }

    public void setOnItemDeleteListener(OnItemDeleteListener listener) {
        this.onItemDeleteListener = listener;
    }

    public void setOnItemEditListener(OnItemEditListener listener) {
        this.onItemEditListener = listener;
    }

    public ConsumedFoodAdapter(Context context) {
        this.context = context;
    }

    public void setFoodItems(List<ConsumedFoodItem> foodItems) {
        this.foodItems = foodItems;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ConsumedFoodViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_consumed_food, parent, false);
        return new ConsumedFoodViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ConsumedFoodViewHolder holder, int position) {
        ConsumedFoodItem foodItem = foodItems.get(position);
        holder.bind(foodItem);
    }

    @Override
    public int getItemCount() {
        return foodItems.size();
    }

    class ConsumedFoodViewHolder extends RecyclerView.ViewHolder {
        TextView tvFoodNameItem;
        TextView tvFoodCaloriesItem;
        ImageButton btnDeleteFoodItem;
        LinearLayout llConsumedFoodItem;

        public ConsumedFoodViewHolder(@NonNull View itemView) {
            super(itemView);
            tvFoodNameItem = itemView.findViewById(R.id.tv_food_name_item);
            tvFoodCaloriesItem = itemView.findViewById(R.id.tv_food_calories_item);
            btnDeleteFoodItem = itemView.findViewById(R.id.btn_delete_food_item);
            llConsumedFoodItem = itemView.findViewById(R.id.ll_consumed_food_item);

            btnDeleteFoodItem.setOnClickListener(v -> {
                if (onItemDeleteListener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        onItemDeleteListener.onItemDelete(foodItems.get(position));
                    }
                }
            });

            llConsumedFoodItem.setOnClickListener(v -> {
                if (onItemEditListener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        onItemEditListener.onItemEdit(foodItems.get(position));
                    }
                }
            });
        }

        public void bind(ConsumedFoodItem foodItem) {
            tvFoodNameItem.setText(foodItem.getTitle()); // <-- UBAH KE getTitle()
            tvFoodCaloriesItem.setText(String.format(java.util.Locale.getDefault(), "%.0f kcal", foodItem.getCalories())); // Tambah Locale
        }
    }
}