package org.udg.pds.todoandroid.ui.viewmodel;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import org.udg.pds.todoandroid.api.ApiService;
import org.udg.pds.todoandroid.api.CreateTaskRequest;
import org.udg.pds.todoandroid.api.IdDto;
import org.udg.pds.todoandroid.util.Resource;

import java.time.ZonedDateTime;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * ViewModel for AddTaskFragment.
 * Handles creating new tasks.
 */
@HiltViewModel
public class AddTaskViewModel extends ViewModel {

    private final ApiService apiService;
    private final MutableLiveData<Resource<IdDto>> createTaskResult = new MutableLiveData<>();

    @Inject
    public AddTaskViewModel(ApiService apiService) {
        this.apiService = apiService;
    }

    public LiveData<Resource<IdDto>> getCreateTaskResult() {
        return createTaskResult;
    }

    public void createTask(String text, ZonedDateTime dateCreated, ZonedDateTime dateLimit) {
        createTaskResult.setValue(Resource.loading(null));

        CreateTaskRequest request = new CreateTaskRequest(text, dateCreated, dateLimit);
        apiService.createTask(request).enqueue(new Callback<IdDto>() {
            @Override
            public void onResponse(@NonNull Call<IdDto> call, @NonNull Response<IdDto> response) {
                if (response.isSuccessful()) {
                    createTaskResult.setValue(Resource.success(response.body()));
                } else {
                    createTaskResult.setValue(Resource.error("Failed to create task", null));
                }
            }

            @Override
            public void onFailure(@NonNull Call<IdDto> call, @NonNull Throwable t) {
                createTaskResult.setValue(Resource.error("Network error: " + t.getMessage(), null));
            }
        });
    }

    /**
     * Reset the result state
     */
    public void resetCreateTaskResult() {
        createTaskResult.setValue(null);
    }
}
