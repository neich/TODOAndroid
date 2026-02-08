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
 * ViewModel for SplashActivity.
 * Handles checking user authentication status.
 */
@HiltViewModel
public class SplashViewModel extends ViewModel {

    private final ApiService apiService;
    private final MutableLiveData<Resource<UserDto>> authStatus = new MutableLiveData<>();

    @Inject
    public SplashViewModel(ApiService apiService) {
        this.apiService = apiService;
    }

    public LiveData<Resource<UserDto>> getAuthStatus() {
        return authStatus;
    }

    public void checkAuthentication() {
        authStatus.setValue(Resource.loading(null));

        apiService.checkSession().enqueue(new Callback<UserDto>() {
            @Override
            public void onResponse(@NonNull Call<UserDto> call, @NonNull Response<UserDto> response) {
                if (response.isSuccessful()) {
                    // User is authenticated (200 with body or 204 No Content)
                    authStatus.setValue(Resource.success(response.body()));
                } else {
                    // Not authenticated (401, 403, etc.)
                    authStatus.setValue(Resource.error("Not authenticated", null));
                }
            }

            @Override
            public void onFailure(@NonNull Call<UserDto> call, @NonNull Throwable t) {
                authStatus.setValue(Resource.error("Network error: " + t.getMessage(), null));
            }
        });
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        // Release any resources if needed in the future
    }
}
