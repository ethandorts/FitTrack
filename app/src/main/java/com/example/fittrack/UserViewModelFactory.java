package com.example.fittrack;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public class UserViewModelFactory implements ViewModelProvider.Factory {
    private final String GroupID;

    public UserViewModelFactory(String groupId) {
        this.GroupID = groupId;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(UserViewModel.class)) {
            return (T) new UserViewModel(GroupID);
        }
        throw new IllegalArgumentException("ViewModel class is unknown");
    }
}

