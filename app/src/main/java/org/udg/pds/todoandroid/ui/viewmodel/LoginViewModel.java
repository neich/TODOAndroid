package org.udg.pds.todoandroid.ui.viewmodel;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import org.udg.pds.todoandroid.api.ApiService;
import org.udg.pds.todoandroid.api.LoginCredentials;
import org.udg.pds.todoandroid.api.UserDto;
import org.udg.pds.todoandroid.util.Resource;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * ViewModel for LoginActivity.
 * Handles user login operations.
 */
@HiltViewModel
public class LoginViewModel extends ViewModel {

    private final ApiService apiService;
    private final MutableLiveData<Resource<UserDto>> loginResult = new MutableLiveData<>();

    @Inject
    public LoginViewModel(ApiService apiService) {
        this.apiService = apiService;
    }

    public LiveData<Resource<UserDto>> getLoginResult() {
        return loginResult;
    }

    public void login(String username, String password) {
        loginResult.setValue(Resource.loading(null));

        LoginCredentials credentials = new LoginCredentials(username, password);
        apiService.login(credentials).enqueue(new Callback<UserDto>() {
            @Override
            public void onResponse(@NonNull Call<UserDto> call, @NonNull Response<UserDto> response) {
                if (response.isSuccessful() && response.body() != null) {
                    loginResult.setValue(Resource.success(response.body()));
                } else {
                    loginResult.setValue(Resource.error("Login failed", null));
                }
            }

            @Override
            public void onFailure(@NonNull Call<UserDto> call, @NonNull Throwable t) {
                loginResult.setValue(Resource.error("Network error: " + t.getMessage(), null));
            }
        });
    }

    /**
     * Reset login result state (useful when navigating back to login)
     */
    public void resetLoginResult() {
        loginResult.setValue(null);
    }
}
