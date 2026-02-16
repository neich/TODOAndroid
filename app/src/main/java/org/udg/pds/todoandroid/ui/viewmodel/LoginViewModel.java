package org.udg.pds.todoandroid.ui.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.ViewModel;

import org.udg.pds.todoandroid.api.UserDto;
import org.udg.pds.todoandroid.data.AuthRepository;
import org.udg.pds.todoandroid.util.Resource;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

/**
 * ViewModel for LoginActivity.
 * Handles user login operations via AuthRepository.
 */
@HiltViewModel
public class LoginViewModel extends ViewModel {

    private final AuthRepository authRepository;
    private final MediatorLiveData<Resource<UserDto>> loginResult = new MediatorLiveData<>();
    private LiveData<Resource<UserDto>> currentSource;

    @Inject
    public LoginViewModel(AuthRepository authRepository) {
        this.authRepository = authRepository;
    }

    public LiveData<Resource<UserDto>> getLoginResult() {
        return loginResult;
    }

    public void login(String username, String password) {
        // Remove previous source if exists
        if (currentSource != null) {
            loginResult.removeSource(currentSource);
        }

        // Get new source from repository
        currentSource = authRepository.login(username, password);

        // Add as source to mediator
        loginResult.addSource(currentSource, resource -> loginResult.setValue(resource));
    }

    /**
     * Reset login result state (useful when navigating back to login)
     */
    public void resetLoginResult() {
        if (currentSource != null) {
            loginResult.removeSource(currentSource);
            currentSource = null;
        }
        loginResult.setValue(null);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        if (currentSource != null) {
            loginResult.removeSource(currentSource);
        }
    }
}
