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
 * ViewModel for SplashActivity.
 * Handles checking user authentication status via AuthRepository.
 */
@HiltViewModel
public class SplashViewModel extends ViewModel {

    private final AuthRepository authRepository;
    private final MediatorLiveData<Resource<UserDto>> authStatus = new MediatorLiveData<>();
    private LiveData<Resource<UserDto>> currentSource;

    @Inject
    public SplashViewModel(AuthRepository authRepository) {
        this.authRepository = authRepository;
    }

    public LiveData<Resource<UserDto>> getAuthStatus() {
        return authStatus;
    }

    public void checkAuthentication() {
        // Remove previous source if exists
        if (currentSource != null) {
            authStatus.removeSource(currentSource);
        }

        // Get new source from repository
        currentSource = authRepository.checkAuthentication();

        // Add as source to mediator
        authStatus.addSource(currentSource, resource -> authStatus.setValue(resource));
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        if (currentSource != null) {
            authStatus.removeSource(currentSource);
        }
    }
}
