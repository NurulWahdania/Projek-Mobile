package com.example.projek;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.projek.Helper.DatabaseHelper;
import com.example.projek.Model.UserProfile;
import com.example.projek.Utils.AppExecutors;

import java.util.Locale;

public class DailyCalorieCalculatorFragment extends Fragment {

    // --- Variabel UI ---
    private EditText etAge, etWeight, etHeight, etTargetWeight;
    private RadioGroup rgGender;
    private RadioButton rbMale, rbFemale;
    private Spinner spinnerActivityLevel, spinnerGoal;
    private Button btnCalculateCalories;
    private TextView tvResultLabel, tvBMRResult, tvTDEEResult, tvDailyCaloriesResult;
    private Button btnContinueToTracking;
    private ProgressBar progressBarCalculatorLoad;

    // --- Variabel lain ---
    private DatabaseHelper databaseHelper;
    private double calculatedDailyCalories = 0.0;

    // ================== LANGKAH 1: Jadikan Adapter sebagai Member Variable ==================
    private ArrayAdapter<CharSequence> activityAdapter;
    private ArrayAdapter<CharSequence> goalAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_daily_calorie_calculator, container, false);

        initViews(view);
        databaseHelper = DatabaseHelper.getInstance(requireContext());

        // ================== LANGKAH 2: Perbaiki Inisialisasi Adapter ==================
        // Hapus "ArrayAdapter<CharSequence>" di awal baris agar kita menginisialisasi
        // member variable yang sudah dideklarasikan di atas, bukan membuat variabel lokal baru.
        activityAdapter = ArrayAdapter.createFromResource(requireContext(),
                R.array.activity_level_options, android.R.layout.simple_spinner_item);
        activityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerActivityLevel.setAdapter(activityAdapter);

        goalAdapter = ArrayAdapter.createFromResource(requireContext(),
                R.array.weight_goal_options, android.R.layout.simple_spinner_item);
        goalAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerGoal.setAdapter(goalAdapter);

        setupListeners();
        loadExistingProfile();

        return view;
    }

    private void loadExistingProfile() {
        Log.d("CalculatorFragment", "Memulai memuat profil pengguna...");
        progressBarCalculatorLoad.setVisibility(View.VISIBLE);
        hideResults();

        AppExecutors.getInstance().diskIO().execute(() -> {
            try {
                UserProfile userProfile = databaseHelper.getUserProfile();
                if (getActivity() == null) return;

                getActivity().runOnUiThread(() -> {
                    progressBarCalculatorLoad.setVisibility(View.GONE);
                    if (userProfile != null) {
                        // ================== LANGKAH 3: Sederhanakan Pemanggilan Metode ==================
                        // Panggil metode tanpa perlu mengirim adapter sebagai argumen
                        populateUIWithProfile(userProfile);
                        Toast.makeText(getContext(), "Profil yang tersimpan berhasil dimuat.", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getContext(), "Silakan lengkapi profil Anda.", Toast.LENGTH_SHORT).show();
                    }
                });
            } catch (Exception e) {
                Log.e("CalculatorFragment", "Error loading user profile: " + e.getMessage(), e);
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        progressBarCalculatorLoad.setVisibility(View.GONE);
                        Toast.makeText(getContext(), "Gagal memuat profil.", Toast.LENGTH_SHORT).show();
                    });
                }
            }
        });
    }

    // Metode ini juga disederhanakan, tidak perlu lagi menerima adapter sebagai parameter
    private void populateUIWithProfile(UserProfile userProfile) {
        if (userProfile.getAge() != null) etAge.setText(String.valueOf(userProfile.getAge()));
        if (userProfile.getWeight() != null) etWeight.setText(String.valueOf(userProfile.getWeight()));
        if (userProfile.getHeight() != null) etHeight.setText(String.valueOf(userProfile.getHeight()));

        if ("male".equalsIgnoreCase(userProfile.getGender())) {
            rbMale.setChecked(true);
        } else if ("female".equalsIgnoreCase(userProfile.getGender())) {
            rbFemale.setChecked(true);
        }

        if (userProfile.getActivityLevel() != null && activityAdapter != null) {
            int spinnerPosition = activityAdapter.getPosition(userProfile.getActivityLevel());
            if (spinnerPosition >= 0) {
                spinnerActivityLevel.setSelection(spinnerPosition);
            }
        }

        if (userProfile.getTargetCalories() != null && userProfile.getTargetCalories() > 0) {
            calculatedDailyCalories = userProfile.getTargetCalories();
            tvResultLabel.setVisibility(View.VISIBLE);
            tvBMRResult.setVisibility(View.GONE);
            tvTDEEResult.setVisibility(View.GONE);
            tvDailyCaloriesResult.setText(String.format(Locale.getDefault(), "Kebutuhan Kalori Harian: %.0f kcal", calculatedDailyCalories));
            tvDailyCaloriesResult.setVisibility(View.VISIBLE);
            btnContinueToTracking.setVisibility(View.VISIBLE);
        }
    }

    // ... (Sisa kode dari onDestroyView hingga akhir tidak berubah, sudah benar) ...
    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    private void initViews(View view) {
        etAge = view.findViewById(R.id.et_age);
        rgGender = view.findViewById(R.id.rg_gender);
        rbMale = view.findViewById(R.id.rb_male);
        rbFemale = view.findViewById(R.id.rb_female);
        etWeight = view.findViewById(R.id.et_weight);
        etHeight = view.findViewById(R.id.et_height);
        spinnerActivityLevel = view.findViewById(R.id.spinner_activity_level);
        spinnerGoal = view.findViewById(R.id.spinner_goal);
        btnCalculateCalories = view.findViewById(R.id.btn_calculate_calories);
        tvResultLabel = view.findViewById(R.id.tv_result_label);
        tvBMRResult = view.findViewById(R.id.tv_bmr_result);
        tvTDEEResult = view.findViewById(R.id.tv_tdee_result);
        tvDailyCaloriesResult = view.findViewById(R.id.tv_daily_calories_result);
        btnContinueToTracking = view.findViewById(R.id.btn_continue_to_tracking);
        progressBarCalculatorLoad = view.findViewById(R.id.progress_bar_calculator_load);
        etTargetWeight = view.findViewById(R.id.et_target_weight);
    }

    private void setupListeners() {
        btnCalculateCalories.setOnClickListener(v -> calculateAndSaveProfile());

        btnContinueToTracking.setOnClickListener(v -> {
            if (calculatedDailyCalories > 0) {
                NavHostFragment.findNavController(this)
                        .navigate(R.id.action_dailyCalorieCalculatorFragment_to_dailyCalorieTrackerFragment);
            } else {
                Toast.makeText(getContext(), "Mohon hitung kebutuhan kalori Anda terlebih dahulu.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void calculateAndSaveProfile() {
        hideResults();

        String ageStr = etAge.getText().toString();
        String weightStr = etWeight.getText().toString();
        String heightStr = etHeight.getText().toString();

        if (ageStr.isEmpty() || weightStr.isEmpty() || heightStr.isEmpty()) {
            Toast.makeText(getContext(), "Mohon lengkapi data usia, berat, dan tinggi badan.", Toast.LENGTH_SHORT).show();
            return;
        }

        int age = Integer.parseInt(ageStr);
        double weight = Double.parseDouble(weightStr);
        double height = Double.parseDouble(heightStr);

        if (age <= 0 || weight <= 0 || height <= 0) {
            Toast.makeText(getContext(), "Usia, berat, dan tinggi harus lebih dari nol.", Toast.LENGTH_SHORT).show();
            return;
        }

        int selectedGenderId = rgGender.getCheckedRadioButtonId();
        if (selectedGenderId == -1) {
            Toast.makeText(getContext(), "Pilih jenis kelamin.", Toast.LENGTH_SHORT).show();
            return;
        }
        String gender = ((RadioButton) requireView().findViewById(selectedGenderId)).getText().toString().toLowerCase(Locale.ROOT);

        double bmr = ("male".equals(gender))
                ? (10 * weight) + (6.25 * height) - (5 * age) + 5
                : (10 * weight) + (6.25 * height) - (5 * age) - 161;

        double activityFactor = 1.2;
        switch (spinnerActivityLevel.getSelectedItemPosition()) {
            case 1: activityFactor = 1.375; break;
            case 2: activityFactor = 1.55; break;
            case 3: activityFactor = 1.725; break;
            case 4: activityFactor = 1.9; break;
        }
        double tdee = bmr * activityFactor;

        double calorieAdjustment = 0;
        switch (spinnerGoal.getSelectedItemPosition()) {
            case 1: calorieAdjustment = -500; break;
            case 2: calorieAdjustment = -1000; break;
            case 3: calorieAdjustment = 500; break;
            case 4: calorieAdjustment = 1000; break;
        }
        calculatedDailyCalories = tdee + calorieAdjustment;

        String activityLevelString = spinnerActivityLevel.getSelectedItem().toString();
        UserProfile userProfile = new UserProfile(
                "Pengguna",
                age,
                gender,
                weight,
                height,
                activityLevelString,
                calculatedDailyCalories
        );

        AppExecutors.getInstance().diskIO().execute(() -> {
            databaseHelper.insertUserProfile(userProfile);
            if (getActivity() != null) {
                getActivity().runOnUiThread(() -> Toast.makeText(getContext(), "Profil pengguna disimpan.", Toast.LENGTH_SHORT).show());
            }
        });

        tvResultLabel.setVisibility(View.VISIBLE);
        tvBMRResult.setText(String.format(Locale.getDefault(), "BMR: %.0f kcal", bmr));
        tvBMRResult.setVisibility(View.VISIBLE);
        tvTDEEResult.setText(String.format(Locale.getDefault(), "TDEE: %.0f kcal", tdee));
        tvTDEEResult.setVisibility(View.VISIBLE);
        tvDailyCaloriesResult.setText(String.format(Locale.getDefault(), "Kebutuhan Kalori Harian: %.0f kcal", calculatedDailyCalories));
        tvDailyCaloriesResult.setVisibility(View.VISIBLE);

        btnContinueToTracking.setVisibility(View.VISIBLE);
    }

    private void hideResults() {
        tvResultLabel.setVisibility(View.GONE);
        tvBMRResult.setVisibility(View.GONE);
        tvTDEEResult.setVisibility(View.GONE);
        tvDailyCaloriesResult.setVisibility(View.GONE);
        btnContinueToTracking.setVisibility(View.GONE);
    }
}