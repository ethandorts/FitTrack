package com.example.fittrack;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;

public class GroupMeetupsFragment extends Fragment {
    MeetupsRecyclerViewAdapter meetupsAdapter;
    GroupMeetupsViewModel meetupsViewModel;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_group_meetups, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        RecyclerView meetupsRecyclerView = view.findViewById(R.id.meetupsRecyclerView);
        ProgressBar loadingActivities = view.findViewById(R.id.meetupsProgressBar);

        Query query = db.collection("Groups")
                .document("Sg8JLYf9lpE1akjQRHBv")
                .collection("Meetups");

        FirestoreRecyclerOptions<MeetupModel> options =
                new FirestoreRecyclerOptions.Builder<MeetupModel>()
                        .setQuery(query, MeetupModel.class)
                        .build();

        meetupsAdapter = new MeetupsRecyclerViewAdapter(options, getActivity());
        meetupsRecyclerView.setAdapter(meetupsAdapter);
//        meetupsViewModel = new ViewModelProvider(this).get(GroupMeetupsViewModel.class);
//        meetupsViewModel.loadGroupMeetups("Sg8JLYf9lpE1akjQRHBv");
//        meetupsViewModel.getGroupMeetups().observe(getActivity(), new Observer<ArrayList<MeetupModel>>() {
//            @Override
//            public void onChanged(ArrayList<MeetupModel> meetupModels) {
//                meetupsAdapter.updateAdapter(meetupModels);
//            }
//        });
        LinearLayoutManager layout = new LinearLayoutManager(getActivity());
        meetupsRecyclerView.setLayoutManager(layout);
        meetupsRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));
//        meetupsViewModel.getIsLoading().observe(getActivity(), new Observer<Boolean>() {
//            @Override
//            public void onChanged(Boolean aBoolean) {
//                loadingActivities.setVisibility(View.GONE);
//            }
//        });

//        meetupsRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
//            @Override
//            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
//                super.onScrolled(recyclerView, dx, dy);
//                if(layout != null) {
//                    int onScreen = layout.getChildCount();
//                    int totalItems = layout.getItemCount();
//                    int firstItem = layout.findFirstVisibleItemPosition();
//
//                    if(!meetupsViewModel.getIsLoading().getValue() && !meetupsViewModel.getIsEndofArray().getValue()) {
//                        if((onScreen + firstItem) >= totalItems - 1 && firstItem >= 0) {
//                            //loadingActivities.setVisibility(View.VISIBLE);
//                            meetupsViewModel.loadGroupMeetups("Sg8JLYf9lpE1akjQRHBv");
//                        }
//                    }
//                }
//            }
//        });
    }

    @Override
    public void onStart() {
        super.onStart();
        meetupsAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        meetupsAdapter.stopListening();
    }
}
