package com.example.fittrack;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.firebase.auth.FirebaseAuth;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class MyBadgesActivity extends AppCompatActivity {
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private String UserID = mAuth.getUid();
    private RecyclerView recyclerBadges;
    private MaterialAutoCompleteTextView monthSelector;
    private BadgesUtil badgesUtil = new BadgesUtil();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_badges);

        recyclerBadges = findViewById(R.id.recyclerBadges);
        monthSelector = findViewById(R.id.monthSelector);

        List<String> monthOptions = Arrays.asList("January 2025", "February 2025", "March 2025", "April 2025", "May 2025");

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, monthOptions);
        monthSelector.setAdapter(adapter);

        monthSelector.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                monthSelector.showDropDown();
            }
        });

        String currentMonth = new SimpleDateFormat("MMMM yyyy", Locale.getDefault()).format(java.util.Calendar.getInstance().getTime());
        if (monthOptions.contains(currentMonth)) {
            monthSelector.setText(currentMonth, false);
            retrieveBadges(currentMonth);
        }

        monthSelector.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String selectedMonth = adapterView.getItemAtPosition(i).toString();
                retrieveBadges(selectedMonth);
            }
        });
    }

    private void retrieveBadges(String month) {
        badgesUtil.retrieveUserBadges(UserID, month, new BadgesUtil.BadgesCallback() {
            @Override
            public void onCallback(List<String> badges) {
                System.out.println(badges.size());
                ArrayList<BadgeModel> badgeModels = new ArrayList<>();
                for (String badge : badges) {
                    badgeModels.add(new BadgeModel(badge, 0));
                }
                BadgesRecyclerViewAdapter adapter = new BadgesRecyclerViewAdapter(MyBadgesActivity.this, badgeModels);
                recyclerBadges.setLayoutManager(new LinearLayoutManager(MyBadgesActivity.this));
                recyclerBadges.setAdapter(adapter);
            }
        });
    }
}