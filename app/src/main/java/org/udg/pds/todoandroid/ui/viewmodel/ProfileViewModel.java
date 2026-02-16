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
 * ViewModel for ProfileFragment.
 * Handles user profile operations including fetching profile data and logout.
 * Uses AuthRepository as the data source (Repository pattern).
 */
@HiltViewModel
public class ProfileViewModel extends ViewModel {

    private final AuthRepository authRepository;
    private final MediatorLiveData<Resource<UserDto>> userProfile = new MediatorLiveData<>();
    private final MediatorLiveData<Resource<Void>> logoutResult = new MediatorLiveData<>();
    private LiveData<Resource<UserDto>> profileSource;
    private LiveData<Resource<Void>> logoutSource;

    @Inject
    public ProfileViewModel(AuthRepository authRepository) {
        this.authRepository = authRepository;
    }

    public LiveData<Resource<UserDto>> getUserProfile() {
        return userProfile;
    }

    public LiveData<Resource<Void>> getLogoutResult() {
        return logoutResult;
    }

    /**
     * Fetch user profile data from the server
     */
    public void loadUserProfile() {
        // Remove previous source if exists
        if (profileSource != null) {
            userProfile.removeSource(profileSource);
        }

        // Get new source from repository
        profileSource = authRepository.getUserProfile();

        // Add as source to mediator
        userProfile.addSource(profileSource, resource -> userProfile.setValue(resource));
    }

    public void logout() {
        // Remove previous source if exists
        if (logoutSource != null) {
            logoutResult.removeSource(logoutSource);
        }

        // Get new source from repository
        logoutSource = authRepository.logout();

        // Add as source to mediator
        logoutResult.addSource(logoutSource, resource -> logoutResult.setValue(resource));
    }

    /**
     * Reset logout result state
     */
    public void resetLogoutResult() {
        if (logoutSource != null) {
            logoutResult.removeSource(logoutSource);
            logoutSource = null;
        }
        logoutResult.setValue(null);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        if (profileSource != null) {
            userProfile.removeSource(profileSource);
        }
        if (logoutSource != null) {
            logoutResult.removeSource(logoutSource);
        }
    }
}
