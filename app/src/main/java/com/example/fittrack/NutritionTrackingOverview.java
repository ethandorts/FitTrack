package com.example.fittrack;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.Date;
import java.util.Locale;

public class NutritionTrackingOverview extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nutrition_tracking_overview);

        ViewPager2 pager = findViewById(R.id.pagerFoods);
        TabLayout tabs = findViewById(R.id.tabLayoutMealTypes);
        EditText editSelectedDate = findViewById(R.id.editSelectedDate);

        Date date = new Date();
        String format = String.valueOf(date.getDate()) + "-" + String.format("%02d", date.getMonth() + 1) + "-" + String.valueOf(date.getYear() + 1900);
        editSelectedDate.setText(format);

        editSelectedDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                java.util.Calendar calendar = java.util.Calendar.getInstance();
                new DatePickerDialog(
                        NutritionTrackingOverview.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                                String selectedDate = String.format(Locale.getDefault(), "%02d-%02d-%04d", day, month + 1, year);
                                editSelectedDate.setText(selectedDate);

                                MealTypesFragmentStateAdapter adapter = new MealTypesFragmentStateAdapter(NutritionTrackingOverview.this);

                                adapter.addFragment(new MealsScrollerFragment("Breakfast", editSelectedDate.getText().toString()));
                                adapter.addFragment(new MealsScrollerFragment("Lunch", editSelectedDate.getText().toString()));
                                adapter.addFragment(new MealsScrollerFragment("Dinner", editSelectedDate.getText().toString()));
                                adapter.addFragment(new MealsScrollerFragment("Snacks", editSelectedDate.getText().toString()));
                                pager.setAdapter(adapter);
                            }
                        },
                        calendar.get(java.util.Calendar.YEAR),
                        calendar.get(java.util.Calendar.MONTH),
                        calendar.get(java.util.Calendar.DAY_OF_MONTH)
                ).show();
            }
        });

        MealTypesFragmentStateAdapter adapter = new MealTypesFragmentStateAdapter(this);

        adapter.addFragment(new MealsScrollerFragment("Breakfast", editSelectedDate.getText().toString()));
        adapter.addFragment(new MealsScrollerFragment("Lunch", editSelectedDate.getText().toString()));
        adapter.addFragment(new MealsScrollerFragment("Dinner", editSelectedDate.getText().toString()));
        adapter.addFragment(new MealsScrollerFragment("Snacks", editSelectedDate.getText().toString()));
        pager.setAdapter(adapter);

        new TabLayoutMediator(tabs, pager,
                new TabLayoutMediator.TabConfigurationStrategy() {
                    @Override
                    public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                        switch (position) {
                            case 0:
                                tab.setText("Breakfast");
                                break;
                            case 1:
                                tab.setText("Lunch");
                                break;
                            case 2:
                                tab.setText("Dinner");
                                break;
                            case 3:
                                tab.setText("Snacks");
                                break;
                        }
                    }
                }).attach();
    }
}