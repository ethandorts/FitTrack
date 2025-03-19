package com.example.fittrack;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PieChartNutrients extends Fragment {
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FoodDatabaseUtil foodUtil = new FoodDatabaseUtil(db);
    private String selectedDate;

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
//        anyChartView.setJsListener(new AnyChartView.JsListener() {
//            @Override
//            public void onJsLineAdd(String s) {
//
//            }
//        });

        Log.d("PieChartNutrients", "Pie date: " + selectedDate);

        if (selectedDate != null) {
            getNutrientChartData(pieChart, selectedDate);
            getRecyclerNutrientData(view, selectedDate);
        } else {
            getNutrientChartData(pieChart, "22-02-2025");
            getRecyclerNutrientData(view, "22-02-2025");
        }
    }

    public void getNutrientChartData(PieChart pieChart, String selectedDate) {
        foodUtil.getCaloriesForMealType(selectedDate, new FoodDatabaseUtil.CaloriesMealCallback() {
            @Override
            public void onCallback(HashMap<String, Double> caloriesMap) {
                if (caloriesMap == null || caloriesMap.isEmpty()) {
                    Log.e("PieChartNutrientsChart", "No food data found for " + selectedDate);
                    return;
                }

                List<PieEntry> entries = new ArrayList<>();
                entries.add(new PieEntry(caloriesMap.getOrDefault("Breakfast", 0.0).floatValue(), "Breakfast"));
                entries.add(new PieEntry(caloriesMap.getOrDefault("Lunch", 0.0).floatValue(), "Lunch"));
                entries.add(new PieEntry(caloriesMap.getOrDefault("Dinner", 0.0).floatValue(), "Dinner"));
                entries.add(new PieEntry(caloriesMap.getOrDefault("Snacks", 0.0).floatValue(), "Snacks"));

                PieDataSet dataSet = new PieDataSet(entries, "Calories Consumed Per Meal");
                dataSet.setColors(Color.RED, Color.BLUE, Color.GREEN, Color.YELLOW);
                dataSet.setValueTextSize(16f);

                PieData pieData = new PieData(dataSet);

                if (isAdded() && getView() != null) {
                    getActivity().runOnUiThread(() -> {
                        PieChart updatedChartView = getView().findViewById(R.id.paceGraph);
                        if (updatedChartView != null) {
                            updatedChartView.setData(pieData);
                            updatedChartView.invalidate();
                            updatedChartView.setCenterText("Calories Consumed per Meal");
                            updatedChartView.setDrawHoleEnabled(true);
                            updatedChartView.setUsePercentValues(true);
                            updatedChartView.animateY(1400);
                        } else {
                            Log.e("Pie Chart Nutrients Fragment Error ", "MPAndroidChart is null!");
                        }
                    });
                } else {
                    Log.e("PieChartNutrients", "No nutrient chart update!");
                }
            }
        });
    }


    public void getRecyclerNutrientData(View view, String selectedDate) {
        RecyclerView recyclerView = view.findViewById(R.id.recyclerView2);
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
                recyclerView.setAdapter(adapter);
                recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            }
        });
    }
}
