package com.example.fittrack;

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

public class InProgressGoalsFragment extends Fragment {
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private String UserID = mAuth.getUid();
    private RecyclerView goalsRecyclerView;
    private GoalsRecyclerViewAdapter goalsAdapter;
    private String status;

    public InProgressGoalsFragment(String status) {
        this.status = status;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_goals_scroll, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        goalsRecyclerView = view.findViewById(R.id.recyclerGoals);

        Query query = db.collection("Users")
                .document(UserID)
                .collection("Goals")
                .whereEqualTo("status", status);

        FirestoreRecyclerOptions<GoalModel> options = new FirestoreRecyclerOptions.Builder<GoalModel>()
                .setQuery(query, GoalModel.class)
                .build();

        goalsAdapter = new GoalsRecyclerViewAdapter(getContext(), options);
        goalsRecyclerView.setAdapter(goalsAdapter);
        goalsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    @Override
    public void onStart() {
        super.onStart();
        goalsAdapter.startListening();
    }

    @Override
    public void onResume() {
        super.onResume();
        goalsAdapter.notifyDataSetChanged();
    }

    @Override
    public void onStop() {
        super.onStop();
        goalsAdapter.stopListening();
    }
}
