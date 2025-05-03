package com.example.fittrack;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class IndividualBadgesFragment extends Fragment {
    private RecyclerView recyclerBadges;
    private AutoCompleteTextView monthSelector;
    private BadgesUtil badgesUtil = new BadgesUtil();
    private String UserID;

    public IndividualBadgesFragment(String UserID) {
        this.UserID = UserID;
    }
    public IndividualBadgesFragment() {

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerBadges = view.findViewById(R.id.recyclerBadges2);
        monthSelector = view.findViewById(R.id.monthSelector2);

        List<String> monthOptions = Arrays.asList("January 2025", "February 2025", "March 2025", "April 2025", "May 2025");

        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_dropdown_item_1line, monthOptions);
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

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.badges_fragment, container, false);
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
                BadgesRecyclerViewAdapter adapter = new BadgesRecyclerViewAdapter(getContext(), badgeModels);
                recyclerBadges.setLayoutManager(new LinearLayoutManager(getContext()));
                recyclerBadges.setAdapter(adapter);
            }
        });
    }
}
