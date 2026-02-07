package org.udg.pds.todoandroid.ui.viewmodel;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import org.udg.pds.todoandroid.api.ApiService;
import org.udg.pds.todoandroid.api.UserDto;
import org.udg.pds.todoandroid.util.Resource;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * ViewModel for ProfileFragment.
 * Handles user profile operations including fetching profile data and logout.
 */
@HiltViewModel
public class ProfileViewModel extends ViewModel {

    private final ApiService apiService;
    private final MutableLiveData<Resource<UserDto>> userProfile = new MutableLiveData<>();
    private final MutableLiveData<Resource<Void>> logoutResult = new MutableLiveData<>();

    @Inject
    public ProfileViewModel(ApiService apiService) {
        this.apiService = apiService;
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
        userProfile.setValue(Resource.loading(null));

        apiService.getUserProfile().enqueue(new Callback<UserDto>() {
            @Override
            public void onResponse(@NonNull Call<UserDto> call, @NonNull Response<UserDto> response) {
                if (response.isSuccessful() && response.body() != null) {
                    userProfile.setValue(Resource.success(response.body()));
                } else {
                    userProfile.setValue(Resource.error("Failed to load profile", null));
                }
            }

            @Override
            public void onFailure(@NonNull Call<UserDto> call, @NonNull Throwable t) {
                userProfile.setValue(Resource.error("Network error: " + t.getMessage(), null));
            }
        });
    }

    public void logout() {
        logoutResult.setValue(Resource.loading(null));

        apiService.logout().enqueue(new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                if (response.isSuccessful()) {
                    logoutResult.setValue(Resource.success(null));
                } else {
                    logoutResult.setValue(Resource.error("Logout failed", null));
                }
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                logoutResult.setValue(Resource.error("Network error: " + t.getMessage(), null));
            }
        });
    }

    /**
     * Reset logout result state
     */
    public void resetLogoutResult() {
        logoutResult.setValue(null);
    }
}
