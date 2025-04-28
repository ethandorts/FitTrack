package com.example.fittrack;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class IndividualUserFragment extends Fragment {
    private Context context;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private ActivitiesRecyclerViewAdapter adapter;
    private RecyclerView recyclerIndividual;
    private String UserID;

    public IndividualUserFragment(Context context, String UserID) {
        this.context = context;
        this.UserID = UserID;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_individual_user, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerIndividual = view.findViewById(R.id.recyclerIndividual);
        Query query = db.collection("Activities")
                .whereEqualTo("UserID", UserID)
                .orderBy("date", Query.Direction.DESCENDING);

        FirestoreRecyclerOptions<ActivityModel> options = new FirestoreRecyclerOptions.Builder<ActivityModel>()
                .setQuery(query, ActivityModel.class)
                .build();

        recyclerIndividual.setHasFixedSize(true);
        adapter = new ActivitiesRecyclerViewAdapter(options, getContext());
        recyclerIndividual.setAdapter(adapter);
        recyclerIndividual.setLayoutManager(new LinearLayoutManager(context));
    }
    @Override
    public void onStart() {
        super.onStart();
        if (adapter != null) {
            adapter.startListening();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (adapter != null) {
            adapter.stopListening();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (adapter != null) {
            adapter.stopListening();
        }
        recyclerIndividual.setAdapter(null);
    }
}
