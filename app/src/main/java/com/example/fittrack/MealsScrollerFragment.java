package com.example.fittrack;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Date;

public class MealsScrollerFragment extends Fragment {
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private String currentUser = mAuth.getUid();
    private NutritionListAdapter mealsAdapter;
    private String mealType;

    public MealsScrollerFragment(String mealType) {
        this.mealType = mealType;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_meals_scroller, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView mealsRecyclerView = view.findViewById(R.id.dinnerRecyclerView);
        TextView txtMealTitle = view.findViewById(R.id.txtMealTitle);
        ImageButton btnAddMeal = view.findViewById(R.id.btnAddFood);

        txtMealTitle.setText(mealType);

        SelectedDateViewModel selectedDateViewModel = new ViewModelProvider(getActivity()).get(SelectedDateViewModel.class);

        selectedDateViewModel.getSelectedDate().observe(getViewLifecycleOwner(), date -> {
            Query query = db.collection("Users")
                    .document(currentUser)
                    .collection("Nutrition")
                    .document(date)
                    .collection("Meals")
                    .whereEqualTo("mealType", mealType);


            FirestoreRecyclerOptions<FoodModel> options =
                    new FirestoreRecyclerOptions.Builder<FoodModel>()
                            .setQuery(query, FoodModel.class)
                            .build();

            mealsAdapter = new NutritionListAdapter(options, getContext(), selectedDateViewModel.getSelectedDate().getValue().toString());
            mealsRecyclerView.setAdapter(mealsAdapter);
            mealsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            mealsAdapter.startListening();
        });

        btnAddMeal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), NutritionTracking.class);
                intent.putExtra("mealType", mealType);
                intent.putExtra("selectedDate", selectedDateViewModel.getSelectedDate().getValue());
                startActivity(intent);
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
       if(mealsAdapter != null) {
           mealsAdapter.startListening();
       }
    }

    @Override
    public void onResume() {
        super.onResume();
        mealsAdapter.notifyDataSetChanged();
    }

    @Override
    public void onStop() {
        super.onStop();
        mealsAdapter.stopListening();
    }
}
