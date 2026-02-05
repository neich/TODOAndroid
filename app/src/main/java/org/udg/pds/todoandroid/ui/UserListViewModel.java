package org.udg.pds.todoandroid.ui;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import org.udg.pds.todoandroid.data.UserRepository;
import org.udg.pds.todoandroid.data.db.UserEntity;

import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

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
}
