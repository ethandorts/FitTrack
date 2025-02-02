package com.example.fittrack;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
        Pie pie = AnyChart.pie();

        System.out.println("Pie date" + selectedDate);

        if (selectedDate != null) {
            foodUtil.getCaloriesForMealType(selectedDate, new FoodDatabaseUtil.CaloriesMealCallback() {
                @Override
                public void onCallback(HashMap<String, Double> caloriesMap) {
                    List<DataEntry> data = new ArrayList<>();
                    data.add(new ValueDataEntry("Breakfast", caloriesMap.get("Breakfast")));
                    data.add(new ValueDataEntry("Lunch", caloriesMap.get("Lunch")));
                    data.add(new ValueDataEntry("Dinner", caloriesMap.get("Dinner")));
                    data.add(new ValueDataEntry("Snacks", caloriesMap.get("Snacks")));

                    pie.data(data);

                    pie.title("Calories Consumed Per Meal");
                    anyChartView.setChart(pie);
                }
            });

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
        } else {
            foodUtil.getCaloriesForMealType("25-01-2025", new FoodDatabaseUtil.CaloriesMealCallback() {
                @Override
                public void onCallback(HashMap<String, Double> caloriesMap) {
                    List<DataEntry> data = new ArrayList<>();
                    data.add(new ValueDataEntry("Breakfast", caloriesMap.get("Breakfast")));
                    data.add(new ValueDataEntry("Lunch", caloriesMap.get("Lunch")));
                    data.add(new ValueDataEntry("Dinner", caloriesMap.get("Dinner")));
                    data.add(new ValueDataEntry("Snacks", caloriesMap.get("Snacks")));

                    pie.data(data);

                    pie.title("Calories Consumed Per Meal");
                    anyChartView.setChart(pie);
                }
            });

            RecyclerView recyclerView = view.findViewById(R.id.recyclerView2);
            ArrayList<NutrientModel> nutrientList = new ArrayList<>();
            foodUtil.getTodayNutrient("25-01-2025", new FoodDatabaseUtil.NutrientCallback() {
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
}
