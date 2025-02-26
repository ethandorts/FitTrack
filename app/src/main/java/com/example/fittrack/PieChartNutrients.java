package com.example.fittrack;

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

        AnyChartView anyChartView = view.findViewById(R.id.paceGraph);
        APIlib.getInstance().setActiveAnyChartView(anyChartView);
//        anyChartView.setJsListener(new AnyChartView.JsListener() {
//            @Override
//            public void onJsLineAdd(String s) {
//
//            }
//        });

        if (anyChartView == null) {
            Log.e("PieChartNutrients", "Error: AnyChartView is NULL! Check XML layout ID.");
            return;
        }
        Pie pie = AnyChart.pie();

        Log.d("PieChartNutrients", "Pie date: " + selectedDate);

        if (selectedDate != null) {
            getNutrientChartData(anyChartView, pie, selectedDate);
            getRecyclerNutrientData(view, selectedDate);
        } else {
            getNutrientChartData(anyChartView, pie, "22-02-2025");
            getRecyclerNutrientData(view, "22-02-2025");
        }
    }

    public void getNutrientChartData(AnyChartView anyChartView, Pie pie, String selectedDate) {
        foodUtil.getCaloriesForMealType(selectedDate, new FoodDatabaseUtil.CaloriesMealCallback() {
            @Override
            public void onCallback(HashMap<String, Double> caloriesMap) {
                if (caloriesMap == null || caloriesMap.isEmpty()) {
                    Log.e("PieChartNutrientsChart", "No food data found for " + selectedDate);
                    return;
                }

                List<DataEntry> data = new ArrayList<>();
                data.add(new ValueDataEntry("Breakfast", caloriesMap.getOrDefault("Breakfast", 0.0)));
                data.add(new ValueDataEntry("Lunch", caloriesMap.getOrDefault("Lunch", 0.0)));
                data.add(new ValueDataEntry("Dinner", caloriesMap.getOrDefault("Dinner", 0.0)));
                data.add(new ValueDataEntry("Snacks", caloriesMap.getOrDefault("Snacks", 0.0)));

                pie.data(data);
                pie.title("Calories Consumed Per Meal");

                if (isAdded() && getView() != null) {
                    getActivity().runOnUiThread(() -> {
                        AnyChartView updatedChartView = getView().findViewById(R.id.paceGraph);
                        if (updatedChartView != null) {
                            updatedChartView.setChart(pie);
                        } else {
                            Log.e("Pie Chart Nutrients Fragment Error ", "AnyChartView is null!");
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
