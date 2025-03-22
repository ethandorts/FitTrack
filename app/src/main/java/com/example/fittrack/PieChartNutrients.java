package com.example.fittrack;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.anychart.APIlib;
import com.anychart.AnyChart;
import com.anychart.AnyChartView;
import com.anychart.chart.common.dataentry.DataEntry;
import com.anychart.chart.common.dataentry.ValueDataEntry;
import com.anychart.charts.Pie;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PieChartNutrients extends Fragment {
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FoodDatabaseUtil foodUtil = new FoodDatabaseUtil(db);
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private String UserID = mAuth.getUid();
    private FirebaseDatabaseHelper userUtil = new FirebaseDatabaseHelper(db);
    private String selectedDate;
    private TextView txtDailyCaloriesConsumed, txtCaloriesBurned, txtCalorieGoal, txtCaloriesRemaining, txtNoChartData;
    private Button btnFurtherStats;

    public PieChartNutrients(String selectedDate) {
        this.selectedDate = selectedDate;
    }

    public PieChartNutrients() {
    }

    public static PieChartNutrients newInstance(String selectedDate) {
        PieChartNutrients fragment = new PieChartNutrients();
        Bundle args = new Bundle();
        args.putString("selectedDate", selectedDate);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            selectedDate = getArguments().getString("selectedDate");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.pie_chart_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        PieChart pieChart = view.findViewById(R.id.paceGraph);
        txtDailyCaloriesConsumed = view.findViewById(R.id.txtDailyCaloriesConsumed);
        txtCaloriesBurned = view.findViewById(R.id.txtDailyCaloriesBurned);
        txtCalorieGoal = view.findViewById(R.id.txtCalorieGoal);
        txtCaloriesRemaining = view.findViewById(R.id.txtDailyCaloriesRemaining);
        btnFurtherStats = view.findViewById(R.id.btnFurtherNutritionalStats);
        txtNoChartData = view.findViewById(R.id.txtNoChartData);

        userUtil.retrieveUserName(UserID, new FirebaseDatabaseHelper.FirestoreUserNameCallback() {
            @Override
            public void onCallback(String FullName, long weight, long height, long activityFrequency, long dailyCalorieGoal) {
                foodUtil.getTotalCalories(selectedDate, new FoodDatabaseUtil.DayCaloriesCallback() {
                    @Override
                    public void onCallback(double caloriesConsumed) {
                        foodUtil.retrieveCaloriesBurnedToday(selectedDate, new FoodDatabaseUtil.DayCaloriesBurnedCallback() {
                            @Override
                            public void onCallback(long caloriesBurned) {
                                txtCalorieGoal.setText(String.valueOf(dailyCalorieGoal) + " calories");
                                txtDailyCaloriesConsumed.setText(String.format("%.2f calories", caloriesConsumed));
                                txtCaloriesBurned.setText(String.valueOf(caloriesBurned) + " calories");
                                txtCaloriesRemaining.setText(String.format("%.2f calories", (dailyCalorieGoal - caloriesConsumed)));
                            }
                        });
                    }
                });
            }
        });

        Log.d("PieChartNutrients", "Pie date: " + selectedDate);

        if (selectedDate != null) {
            getNutrientChartData(pieChart, selectedDate);
            getRecyclerNutrientData(view, selectedDate);
        } else {
            getNutrientChartData(pieChart, "22-02-2025");
            getRecyclerNutrientData(view, "22-02-2025");
        }

        btnFurtherStats.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), AdditionalNutrtionalInformationActivity.class);
                intent.putExtra("selectedDate", selectedDate);
                startActivity(intent);
            }
        });
    }

    public void getNutrientChartData(PieChart pieChart, String selectedDate) {
        foodUtil.getCaloriesForMealType(selectedDate, new FoodDatabaseUtil.CaloriesMealCallback() {
            @Override
            public void onCallback(HashMap<String, Double> caloriesMap) {
                if (caloriesMap == null || caloriesMap.isEmpty()) {
                    Log.e("PieChartNutrientsChart", "No food data found for " + selectedDate);
                    txtNoChartData.setVisibility(View.VISIBLE);
                    return;
                }

                List<PieEntry> entries = new ArrayList<>();

                if (caloriesMap.getOrDefault("Breakfast", 0.0) > 0)
                    entries.add(new PieEntry(caloriesMap.get("Breakfast").floatValue(), "Breakfast"));
                if (caloriesMap.getOrDefault("Lunch", 0.0) > 0)
                    entries.add(new PieEntry(caloriesMap.get("Lunch").floatValue(), "Lunch"));
                if (caloriesMap.getOrDefault("Dinner", 0.0) > 0)
                    entries.add(new PieEntry(caloriesMap.get("Dinner").floatValue(), "Dinner"));
                if (caloriesMap.getOrDefault("Snacks", 0.0) > 0)
                    entries.add(new PieEntry(caloriesMap.get("Snacks").floatValue(), "Snacks"));

                List<Integer> colors = new ArrayList<>();
                colors.add(Color.parseColor("#FF5733"));
                colors.add(Color.parseColor("#4285F4"));
                colors.add(Color.parseColor("#34A853"));
                colors.add(Color.parseColor("#F4B400"));

                PieDataSet dataSet = new PieDataSet(entries, "Meal Times");
                dataSet.setColors(colors);
                dataSet.setValueTextSize(30f);
                dataSet.setValueTextColor(Color.WHITE);

                PieData pieData = new PieData(dataSet);

                if (isAdded() && getView() != null) {
                    getActivity().runOnUiThread(() -> {
                        PieChart updatedChartView = getView().findViewById(R.id.paceGraph);

                        if (updatedChartView != null) {
                            updatedChartView.setData(pieData);
                            updatedChartView.setDrawHoleEnabled(false);
                            updatedChartView.getDescription().setEnabled(false);
                            updatedChartView.setNoDataText("No chart data available");
                            updatedChartView.setNoDataTextColor(Color.GRAY);
                            updatedChartView.setNoDataTextTypeface(Typeface.DEFAULT_BOLD);
                            updatedChartView.getLegend().setEnabled(true);
                            updatedChartView.animateY(1400);
                            updatedChartView.invalidate();
                        } else {
                            Log.e("Pie Chart Nutrients Fragment Error", "MPAndroidChart is null");
                        }
                    });
                } else {
                    Log.e("PieChartNutrients", "No nutrient chart update");
                }
            }
        });
    }


    public void getRecyclerNutrientData(View view, String selectedDate) {
        //RecyclerView recyclerView = view.findViewById(R.id.recyclerView2);
        ArrayList<NutrientModel> nutrientList = new ArrayList<>();
        foodUtil.getTodayNutrient(selectedDate, new FoodDatabaseUtil.NutrientCallback() {
            @Override
            public void onCallback(double fatAmount, double saturatedFatAmount, double proteinAmount, double sodiumAmount, double potassiumAmount, double carbsAmount, double fiberAmount, double sugarAmount) {
                nutrientList.add(new NutrientModel(" Fat", fatAmount));
                nutrientList.add(new NutrientModel(" Saturated Fat", saturatedFatAmount));
                nutrientList.add(new NutrientModel(" Protein", proteinAmount));
                nutrientList.add(new NutrientModel(" Potassium", potassiumAmount));
                nutrientList.add(new NutrientModel(" Carbohydrates", carbsAmount));
                nutrientList.add(new NutrientModel(" Fiber", fiberAmount));
                nutrientList.add(new NutrientModel(" Sugar", sugarAmount));

                NutrientRecyclerAdapter adapter = new NutrientRecyclerAdapter(getContext(), nutrientList);
                //recyclerView.setAdapter(adapter);
                //recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            }
        });
    }
}
