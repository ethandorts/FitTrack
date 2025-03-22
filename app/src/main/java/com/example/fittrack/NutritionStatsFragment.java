package com.example.fittrack;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.anychart.AnyChart;
import com.anychart.AnyChartView;
import com.anychart.chart.common.dataentry.DataEntry;
import com.anychart.chart.common.dataentry.ValueDataEntry;
import com.anychart.charts.Pie;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class NutritionStatsFragment extends Fragment {
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FoodDatabaseUtil foodUtil = new FoodDatabaseUtil(db);

    public NutritionStatsFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_nutrition_stats, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //TextView txtCalories = view.findViewById(R.id.txtCalculation);
        SelectedDateViewModel selectedDateViewModel = new ViewModelProvider(getActivity()).get(SelectedDateViewModel.class);

        selectedDateViewModel.getSelectedDate().observe(getViewLifecycleOwner(), selectedDate -> {
            if(selectedDate != null && !selectedDate.isEmpty()) {
                loadFragment(PieChartNutrients.newInstance(selectedDate), R.id.frameLayout7);
                foodUtil.getTotalCalories(selectedDate, new FoodDatabaseUtil.DayCaloriesCallback() {
                    @Override
                    public void onCallback(double calories) {
                        //txtCalories.setText("Total Daily Calories: " + calories + " calories");
                    }
                });
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();

        SelectedDateViewModel selectedDateViewModel = new ViewModelProvider(getActivity()).get(SelectedDateViewModel.class);

        selectedDateViewModel.getSelectedDate().observe(getViewLifecycleOwner(), selectedDate -> {
            if(selectedDate != null && !selectedDate.isEmpty()) {
                loadFragment(PieChartNutrients.newInstance(selectedDate), R.id.frameLayout7);
                foodUtil.getTotalCalories(selectedDate, new FoodDatabaseUtil.DayCaloriesCallback() {
                    @Override
                    public void onCallback(double calories) {
                        //txtCalories.setText("Total Daily Calories: " + calories + " calories");
                    }
                });
            }
        });
    }

    public void loadFragment(Fragment fragment, int frameLayoutId) {
        FragmentManager fm = getChildFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();

        Fragment existingFragment = fm.findFragmentById(frameLayoutId);
        if (existingFragment != null) {
            transaction.remove(existingFragment);
        }

        transaction.replace(frameLayoutId, fragment);
        transaction.commit();
    }

}
