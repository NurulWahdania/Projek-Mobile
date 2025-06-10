// package com.example.projek;
// ... (semua import Anda tetap sama)
package com.example.projek;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projek.Adapter.ConsumedFoodAdapter;
import com.example.projek.Helper.DatabaseHelper;
import com.example.projek.Model.ConsumedFoodItem;
import com.example.projek.Model.UserProfile;
import com.example.projek.Utils.AppExecutors;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
// Hapus import yang tidak perlu
// import java.util.concurrent.ExecutionException;

public class DailyCalorieTrackerFragment extends Fragment implements ConsumedFoodAdapter.OnItemEditListener {

    // ... (Deklarasi variabel tidak berubah) ...
    private TextView tvTargetCalories, tvConsumedCalories, tvRemainingCalories;
    private ProgressBar pbCalorieProgress;
    private ImageButton btnPreviousDay, btnNextDay;
    private TextView tvCurrentDate;
    private EditText etFoodName, etFoodCalories;
    private Button btnAddFood;
    private RecyclerView rvConsumedFoodList;
    private Button btnEditTarget;

    private ConsumedFoodAdapter consumedFoodAdapter;
    private DatabaseHelper databaseHelper;
    private AppExecutors appExecutors;

    private double dailyCalorieTarget = 0.0;
    private Calendar currentCalendarDate;

    // ... (Metode onCreateView dan lainnya hingga sebelum loadUserProfileAndConsumedFood tidak berubah) ...
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_daily_calorie_tracker, container, false);

        initViews(view);
        databaseHelper = DatabaseHelper.getInstance(requireContext());
        appExecutors = AppExecutors.getInstance();
        currentCalendarDate = Calendar.getInstance();
        setupRecyclerView();
        setupListeners();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d("TrackerFragment", "onResume called. Loading data.");
        loadUserProfileAndConsumedFood();
        updateDateDisplay();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("TrackerFragment", "onDestroy called. Closing DB connection.");
        if (databaseHelper != null) {
            databaseHelper.closeDatabaseConnection();
        }
    }


    private void initViews(View view) {
        tvTargetCalories = view.findViewById(R.id.tv_target_calories);
        tvConsumedCalories = view.findViewById(R.id.tv_consumed_calories);
        tvRemainingCalories = view.findViewById(R.id.tv_remaining_calories);
        pbCalorieProgress = view.findViewById(R.id.pb_calorie_progress);
        btnPreviousDay = view.findViewById(R.id.btn_previous_day);
        btnNextDay = view.findViewById(R.id.btn_next_day);
        tvCurrentDate = view.findViewById(R.id.tv_current_date);
        etFoodName = view.findViewById(R.id.et_food_name);
        etFoodCalories = view.findViewById(R.id.et_food_calories);
        btnAddFood = view.findViewById(R.id.btn_add_food);
        rvConsumedFoodList = view.findViewById(R.id.rv_consumed_food_list);
        btnEditTarget = view.findViewById(R.id.btn_edit_target);
    }

    private void setupRecyclerView() {
        rvConsumedFoodList.setLayoutManager(new LinearLayoutManager(requireContext()));
        consumedFoodAdapter = new ConsumedFoodAdapter(requireContext());
        rvConsumedFoodList.setAdapter(consumedFoodAdapter);

        consumedFoodAdapter.setOnItemDeleteListener(foodItem -> {
            Log.d("TrackerFragment", "Delete requested for: " + foodItem.getTitle() + " (ID: " + foodItem.getConsumedId() + ")");
            appExecutors.diskIO().execute(() -> {
                try {
                    int rowsAffected = databaseHelper.deleteConsumedFood(foodItem);
                    if (getActivity() != null) {
                        getActivity().runOnUiThread(() -> {
                            if (rowsAffected > 0) {
                                Toast.makeText(getContext(), foodItem.getTitle() + " dihapus.", Toast.LENGTH_SHORT).show();
                                Log.d("TrackerFragment", "Food item deleted: " + foodItem.getTitle());
                                loadConsumedFoodAndTotalCalories();
                            } else {
                                Toast.makeText(getContext(), "Gagal menghapus " + foodItem.getTitle() + ".", Toast.LENGTH_SHORT).show();
                                Log.w("TrackerFragment", "Failed to delete " + foodItem.getTitle() + ". Rows affected: " + rowsAffected);
                            }
                        });
                    }
                } catch (Exception e) {
                    Log.e("TrackerFragment", "Error deleting food item: " + e.getMessage(), e);
                    if (getActivity() != null) {
                        getActivity().runOnUiThread(() -> {
                            Toast.makeText(getContext(), "Error DB saat menghapus: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        });
                    }
                }
            });
        });

        consumedFoodAdapter.setOnItemEditListener(this);
    }

    private void setupListeners() {
        btnAddFood.setOnClickListener(v -> addFoodItem());
        btnPreviousDay.setOnClickListener(v -> changeDate(-1));
        btnNextDay.setOnClickListener(v -> changeDate(1));

        btnEditTarget.setOnClickListener(v -> {
            NavHostFragment.findNavController(this).navigate(R.id.dailyCalorieCalculatorFragment);
        });
    }

    private void addFoodItem() {
        // Metode ini sudah diperbaiki sebelumnya dan sudah benar, tidak perlu diubah lagi
        String foodTitle = etFoodName.getText().toString().trim();
        String caloriesStr = etFoodCalories.getText().toString().trim();
        if (foodTitle.isEmpty() || caloriesStr.isEmpty()) {
            Toast.makeText(getContext(), "Nama dan kalori tidak boleh kosong.", Toast.LENGTH_SHORT).show();
            return;
        }
        double calories;
        try {
            calories = Double.parseDouble(caloriesStr);
        } catch (NumberFormatException e) {
            Toast.makeText(getContext(), "Format kalori tidak valid.", Toast.LENGTH_SHORT).show();
            return;
        }
        if (calories <= 0) {
            Toast.makeText(getContext(), "Kalori harus lebih dari nol.", Toast.LENGTH_SHORT).show();
            return;
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String currentDateString = sdf.format(currentCalendarDate.getTime());
        ConsumedFoodItem foodItem = new ConsumedFoodItem(0, foodTitle, calories, currentDateString, 1.0, "porsi");

        appExecutors.diskIO().execute(() -> {
            try {
                long insertId = databaseHelper.insertConsumedFood(foodItem);
                appExecutors.mainThread().execute(() -> {
                    if (insertId != -1) {
                        etFoodName.setText("");
                        etFoodCalories.setText("");
                        Toast.makeText(getContext(), "Makanan ditambahkan.", Toast.LENGTH_SHORT).show();
                        loadConsumedFoodAndTotalCalories();
                    } else {
                        Toast.makeText(getContext(), "Gagal menambahkan makanan.", Toast.LENGTH_SHORT).show();
                    }
                });
            } catch (Exception e) {
                appExecutors.mainThread().execute(() -> Toast.makeText(getContext(), "Error: " + e.getMessage(), Toast.LENGTH_LONG).show());
            }
        });
    }

    private void updateDateDisplay() {
        SimpleDateFormat sdfDisplay = new SimpleDateFormat("EEEE, dd MMMM yyyy", Locale.getDefault());
        Calendar today = Calendar.getInstance();
        if (currentCalendarDate.get(Calendar.YEAR) == today.get(Calendar.YEAR) &&
                currentCalendarDate.get(Calendar.DAY_OF_YEAR) == today.get(Calendar.DAY_OF_YEAR)) {
            tvCurrentDate.setText(String.format("Hari Ini, %s", new SimpleDateFormat("dd MMMM", Locale.getDefault()).format(currentCalendarDate.getTime())));
        } else {
            tvCurrentDate.setText(sdfDisplay.format(currentCalendarDate.getTime()));
        }
    }

    private void changeDate(int days) {
        currentCalendarDate.add(Calendar.DAY_OF_YEAR, days);
        updateDateDisplay();
        loadConsumedFoodAndTotalCalories();
    }

    // =================================================================================
    // ======================= PERUBAHAN UTAMA ADA DI METODE INI =======================
    // =================================================================================
    private void loadUserProfileAndConsumedFood() {
        Log.d("TrackerFragment", "Loading user profile...");
        appExecutors.diskIO().execute(() -> {
            try {
                // PERBAIKAN: Panggil metode getUserProfile() yang sederhana dan aman.
                // Ini menghilangkan penyebab deadlock.
                UserProfile userProfile = databaseHelper.getUserProfile();

                // Pastikan fragment masih aktif sebelum update UI
                if (getActivity() == null) return;

                // Setelah data didapat, pindah ke UI thread untuk update tampilan
                getActivity().runOnUiThread(() -> {
                    if (userProfile != null && userProfile.getTargetCalories() != null && userProfile.getTargetCalories() > 0) {
                        dailyCalorieTarget = userProfile.getTargetCalories();
                        tvTargetCalories.setText(String.format(Locale.getDefault(), "Target:\n%.0f kcal", dailyCalorieTarget));
                        Log.d("TrackerFragment", "User profile loaded. Target: " + dailyCalorieTarget);
                    } else {
                        dailyCalorieTarget = 0.0;
                        tvTargetCalories.setText("Target:\nN/A");
                        if (isAdded()) { // Cek jika fragment terpasang sebelum navigasi
                            Toast.makeText(getContext(), "Mohon atur target kalori Anda terlebih dahulu.", Toast.LENGTH_LONG).show();
                            NavHostFragment.findNavController(this).navigate(R.id.dailyCalorieCalculatorFragment);
                            Log.w("TrackerFragment", "User profile not found or target calories invalid. Navigating to calculator.");
                        }
                    }
                    // Lanjutkan memuat data makanan setelah profil berhasil diproses
                    loadConsumedFoodAndTotalCalories();
                });

            } catch (Exception e) {
                // Tangani error jika terjadi
                Log.e("TrackerFragment", "Error loading user profile: " + e.getMessage(), e);
                if (getActivity() == null) return;
                getActivity().runOnUiThread(() -> {
                    Toast.makeText(getContext(), "Gagal memuat profil pengguna.", Toast.LENGTH_SHORT).show();
                    dailyCalorieTarget = 0.0;
                    tvTargetCalories.setText("Target:\nN/A");
                    loadConsumedFoodAndTotalCalories();
                });
            }
        });
    }

    private void loadConsumedFoodAndTotalCalories() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String dateToLoad = sdf.format(currentCalendarDate.getTime());
        Log.d("TrackerFragment", "Loading consumed food and total calories for date: " + dateToLoad);

        appExecutors.diskIO().execute(() -> {
            List<ConsumedFoodItem> consumedFoodItems = databaseHelper.getConsumedFoodForDate(dateToLoad);
            double totalConsumed = databaseHelper.getTotalCaloriesForDate(dateToLoad);

            if (getActivity() == null) return;

            getActivity().runOnUiThread(() -> {
                if (consumedFoodAdapter != null) {
                    consumedFoodAdapter.setFoodItems(consumedFoodItems);
                }

                tvConsumedCalories.setText(String.format(Locale.getDefault(), "Dikonsumsi:\n%.0f kcal", totalConsumed));
                double remaining = dailyCalorieTarget - totalConsumed;
                tvRemainingCalories.setText(String.format(Locale.getDefault(), "Sisa:\n%.0f kcal", remaining));

                if (dailyCalorieTarget > 0) {
                    int progress = (int) ((totalConsumed / dailyCalorieTarget) * 100);
                    pbCalorieProgress.setProgress(Math.min(progress, 100));
                } else {
                    pbCalorieProgress.setProgress(0);
                }
            });
        });
    }

    @Override
    public void onItemEdit(ConsumedFoodItem foodItem) {
        Log.d("TrackerFragment", "Edit requested for food item: " + foodItem.getTitle());
        showEditFoodDialog(foodItem);
    }

    private void showEditFoodDialog(ConsumedFoodItem foodItem) {
        // ... (Metode ini sudah benar, tidak perlu diubah) ...
        if (getContext() == null) return;
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Edit Makanan");

        View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_edit_food_item, null);
        EditText etEditFoodName = dialogView.findViewById(R.id.et_edit_food_name);
        EditText etEditFoodCalories = dialogView.findViewById(R.id.et_edit_food_calories);
        EditText etEditFoodQuantity = dialogView.findViewById(R.id.et_edit_food_quantity);
        EditText etEditFoodServingSize = dialogView.findViewById(R.id.et_edit_food_serving_size);

        etEditFoodName.setText(foodItem.getTitle());
        etEditFoodCalories.setText(String.valueOf(foodItem.getCalories()));
        etEditFoodQuantity.setText(String.valueOf(foodItem.getQuantity()));
        etEditFoodServingSize.setText(foodItem.getServingSize());

        builder.setView(dialogView);

        builder.setPositiveButton("Simpan", (dialog, which) -> {
            String newTitle = etEditFoodName.getText().toString().trim();
            String newCaloriesStr = etEditFoodCalories.getText().toString().trim();
            String newQuantityStr = etEditFoodQuantity.getText().toString().trim();
            String newServingSize = etEditFoodServingSize.getText().toString().trim();

            if (newTitle.isEmpty() || newCaloriesStr.isEmpty() || newQuantityStr.isEmpty() || newServingSize.isEmpty()) {
                Toast.makeText(getContext(), "Semua field tidak boleh kosong.", Toast.LENGTH_SHORT).show();
                return;
            }

            double newCalories;
            double newQuantity;
            try {
                newCalories = Double.parseDouble(newCaloriesStr);
                newQuantity = Double.parseDouble(newQuantityStr);
            } catch (NumberFormatException e) {
                Toast.makeText(getContext(), "Format kalori atau kuantitas tidak valid.", Toast.LENGTH_SHORT).show();
                return;
            }

            if (newCalories <= 0 || newQuantity <= 0) {
                Toast.makeText(getContext(), "Kalori dan kuantitas harus lebih dari nol.", Toast.LENGTH_SHORT).show();
                return;
            }

            foodItem.setTitle(newTitle);
            foodItem.setCalories(newCalories);
            foodItem.setQuantity(newQuantity);
            foodItem.setServingSize(newServingSize);

            appExecutors.diskIO().execute(() -> {
                try {
                    int rowsAffected = databaseHelper.updateConsumedFood(foodItem);
                    if(getActivity() != null) {
                        getActivity().runOnUiThread(() -> {
                            if (rowsAffected > 0) {
                                Toast.makeText(getContext(), "Makanan diperbarui.", Toast.LENGTH_SHORT).show();
                                loadConsumedFoodAndTotalCalories();
                            } else {
                                Toast.makeText(getContext(), "Gagal memperbarui makanan.", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                } catch (Exception e) {
                    if(getActivity() != null) {
                        getActivity().runOnUiThread(() -> Toast.makeText(getContext(), "Error DB: " + e.getMessage(), Toast.LENGTH_LONG).show());
                    }
                }
            });
        });

        builder.setNegativeButton("Batal", (dialog, which) -> dialog.cancel());
        builder.show();
    }
}