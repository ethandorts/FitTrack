package com.example.fittrack;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import java.util.ArrayList;
import java.util.List;

public class MealTypesFragmentStateAdapter extends FragmentStateAdapter {
    private List<Fragment> mealFragments = new ArrayList<>();
    public MealTypesFragmentStateAdapter(@NonNull FragmentActivity fragment) {
        super(fragment);
    }

    public void addFragment(Fragment fragment) {
        mealFragments.add(fragment);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return mealFragments.get(position);
    }

    @Override
    public int getItemCount() {
        return mealFragments.size();
    }
}
