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

public class GroupPostsFragment extends Fragment {
    GroupPostsViewModel groupPostsViewModel;
    PostsRecyclerViewAdapter postsAdapter;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private String GroupID;

    public GroupPostsFragment(String groupID) {
        GroupID = groupID;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_groups_post, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        RecyclerView postRecyclerView = view.findViewById(R.id.postsRecyclerView);
        ProgressBar loadingActivities = view.findViewById(R.id.postProgressBar);

        Query query = db.collection("Groups")
                .document(GroupID)
                .collection("Posts");

        FirestoreRecyclerOptions<PostModel> options =
                new FirestoreRecyclerOptions.Builder<PostModel>()
                        .setQuery(query, PostModel.class)
                        .build();

        postsAdapter = new PostsRecyclerViewAdapter(options, getActivity());
        postRecyclerView.setAdapter(postsAdapter);
//        groupPostsViewModel = new ViewModelProvider(this).get(GroupPostsViewModel.class);
//        groupPostsViewModel.loadGroupPosts("Sg8JLYf9lpE1akjQRHBv");
//        groupPostsViewModel.getGroupActivities().observe(getActivity(), new Observer<ArrayList<PostModel>>() {
//            @Override
//            public void onChanged(ArrayList<PostModel> postModels) {
//                postsAdapter.updateAdapter(postModels);
//            }
//        });
        LinearLayoutManager layout = new LinearLayoutManager(getActivity());
        postRecyclerView.setLayoutManager(layout);
        postRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));

//        groupPostsViewModel.getIsLoading().observe(getActivity(), new Observer<Boolean>() {
//            @Override
//            public void onChanged(Boolean aBoolean) {
//                loadingActivities.setVisibility(View.GONE);
//            }
//        });

//        postRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
//            @Override
//            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
//                super.onScrolled(recyclerView, dx, dy);
//                if(layout != null) {
//                    int onScreen = layout.getChildCount();
//                    int totalItems = layout.getItemCount();
//                    int firstItem = layout.findFirstVisibleItemPosition();
//
//                    if(!groupPostsViewModel.getIsLoading().getValue() && !groupPostsViewModel.getIsEndofArray().getValue()) {
//                        if((onScreen + firstItem) >= totalItems - 1 && firstItem >= 0) {
//                            loadingActivities.setVisibility(View.VISIBLE);
//                            groupPostsViewModel.loadGroupPosts("Sg8JLYf9lpE1akjQRHBv");
//                        }
//                    }
//                }
//            }
//        });
    }

    @Override
    public void onStart() {
        super.onStart();
        postsAdapter.startListening();
    }

    @Override
    public void onResume() {
        super.onResume();
        postsAdapter.startListening();
        postsAdapter.notifyDataSetChanged();
    }

    @Override
    public void onStop() {
        super.onStop();
        postsAdapter.stopListening();
    }
}
