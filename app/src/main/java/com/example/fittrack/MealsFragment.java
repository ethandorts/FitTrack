package com.example.fittrack;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.Date;

public class MealsFragment extends Fragment {
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private String currentUser = mAuth.getUid();
    private NutritionListAdapter breakfastAdapter, lunchAdapter, dinnerAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_meals, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //RecyclerView breakfastRecycler = view.findViewById(R.id.breakfastRecyclerView);
        //RecyclerView lunchRecycler = view.findViewById(R.id.lunchRecyclerView);
        RecyclerView dinnerRecycler = view.findViewById(R.id.dinnerRecyclerView);
        //ImageButton btnAddBreakfast = view.findViewById(R.id.addBreakfastItem);
        //ImageButton btnAddLunch = view.findViewById(R.id.addLunchItem);
        ImageButton btnAddDinner = view.findViewById(R.id.btnAddFood);
        //ImageButton btnAddSnack = view.findViewById(R.id.addSnacksItem);

        Date date = new Date();
        String stringDate = String.valueOf(date.getDate()) + "-" + String.format("%02d", date.getMonth() + 1) + "-" + String.valueOf(date.getYear() + 1900);
        System.out.println(stringDate);

        Query breakfastQuery = db.collection("Users")
                .document(currentUser)
                .collection("Nutrition")
                .document(stringDate)
                .collection("Meals")
                .whereEqualTo("mealType", "Breakfast");

        Query lunchQuery = db.collection("Users")
                .document(currentUser)
                .collection("Nutrition")
                .document(stringDate)
                .collection("Meals")
                .whereEqualTo("mealType", "Lunch");

        Query dinnerQuery = db.collection("Users")
                .document(currentUser)
                .collection("Nutrition")
                .document(stringDate)
                .collection("Meals")
                .whereEqualTo("mealType", "Dinner");


        FirestoreRecyclerOptions<FoodModel> breakfastOptions =
                new FirestoreRecyclerOptions.Builder<FoodModel>()
                        .setQuery(breakfastQuery, FoodModel.class)
                        .build();

        FirestoreRecyclerOptions<FoodModel> lunchOptions =
                new FirestoreRecyclerOptions.Builder<FoodModel>()
                        .setQuery(lunchQuery, FoodModel.class)
                        .build();

        FirestoreRecyclerOptions<FoodModel> dinnerOptions =
                new FirestoreRecyclerOptions.Builder<FoodModel>()
                        .setQuery(dinnerQuery, FoodModel.class)
                        .build();

        breakfastAdapter = new NutritionListAdapter(breakfastOptions, getContext());
        lunchAdapter = new NutritionListAdapter(lunchOptions, getContext());
        dinnerAdapter = new NutritionListAdapter(dinnerOptions, getContext());
//        breakfastRecycler.setAdapter(breakfastAdapter);
//        breakfastRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
//        lunchRecycler.setAdapter(lunchAdapter);
//        lunchRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        dinnerRecycler.setAdapter(dinnerAdapter);
        dinnerRecycler.setLayoutManager(new LinearLayoutManager(getContext()));

//        btnAddBreakfast.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(getContext(), NutritionTracking.class);
//                intent.putExtra("mealType", "Breakfast");
//                startActivity(intent);
//            }
//        });

//        btnAddLunch.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(getContext(), NutritionTracking.class);
//                intent.putExtra("mealType", "Lunch");
//                startActivity(intent);
//            }
//        });
//
//        btnAddDinner.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(getContext(), NutritionTracking.class);
//                intent.putExtra("mealType", "Dinner");
//                startActivity(intent);
//            }
//        });
//
//        btnAddSnack.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(getContext(), NutritionTracking.class);
//                intent.putExtra("mealType", "Snack");
//                startActivity(intent);
//            }
//        });
    }

    @Override
    public void onResume() {
        super.onResume();
        breakfastAdapter.notifyDataSetChanged();
        lunchAdapter.notifyDataSetChanged();
        dinnerAdapter.notifyDataSetChanged();
    }

    @Override
    public void onStart() {
        super.onStart();
        breakfastAdapter.startListening();
        lunchAdapter.startListening();
        dinnerAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        breakfastAdapter.stopListening();
        lunchAdapter.stopListening();
        dinnerAdapter.stopListening();
    }
}
