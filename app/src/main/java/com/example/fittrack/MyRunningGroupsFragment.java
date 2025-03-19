package com.example.fittrack;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class MyRunningGroupsFragment extends Fragment {
    View view;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private String UserID = mAuth.getUid();
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private ArrayList<GroupModel> groupList = new ArrayList<>();
    private UserGroupsViewModel groupsViewModel = new UserGroupsViewModel();
    private GroupsRecyclerViewAdapter groupsAdapter;
    private GroupsDatabaseUtil groupsDatabaseUtil = new GroupsDatabaseUtil(db);

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_my_running_groups, container, false);


        Query query = db.collection("Groups").whereArrayContains("Runners", UserID);
        query.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot querySnapshot) {
                System.out.println("Docs: " + querySnapshot.getDocuments().size());
            }
        });

        FirestoreRecyclerOptions<GroupModel> options = new FirestoreRecyclerOptions.Builder<GroupModel>()
                .setQuery(query, GroupModel.class)
                .build();

        RecyclerView groupsRecyclerView = view.findViewById(R.id.recyclerView);
        groupsAdapter = new GroupsRecyclerViewAdapter(options, getContext());
        groupsRecyclerView.setAdapter(groupsAdapter);
        System.out.println(groupsAdapter.getItemCount() + ": Full groups count");
        LinearLayoutManager layout = new LinearLayoutManager(getContext());

        groupsRecyclerView.setLayoutManager(layout);
        groupsRecyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        groupsAdapter.startListening();
    }

    @Override
    public void onResume() {
        super.onResume();
        groupsAdapter.startListening();
        groupsAdapter.notifyDataSetChanged();
    }

    @Override
    public void onStop() {
        super.onStop();
        groupsAdapter.stopListening();
    }
}
