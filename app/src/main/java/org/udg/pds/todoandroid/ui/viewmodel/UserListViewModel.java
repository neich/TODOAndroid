package org.udg.pds.todoandroid.ui.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import org.udg.pds.todoandroid.data.UserRepository;
import org.udg.pds.todoandroid.data.db.UserEntity;

import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

/**
 * ViewModel for user list.
 * Handles fetching and caching user data.
 */
@HiltViewModel
public class UserListViewModel extends ViewModel {

    private final UserRepository repository;
    private final LiveData<List<UserEntity>> users;

    @Inject
    public UserListViewModel(UserRepository repository) {
        this.repository = repository;
        this.users = repository.getUsers();
    }

    public LiveData<List<UserEntity>> getUsers() {
        return users;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        // Release any resources if needed in the future
    }
}
