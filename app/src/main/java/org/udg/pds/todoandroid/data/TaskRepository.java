package org.udg.pds.todoandroid.data;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import org.udg.pds.todoandroid.api.ApiService;
import org.udg.pds.todoandroid.api.CreateTaskRequest;
import org.udg.pds.todoandroid.api.IdDto;
import org.udg.pds.todoandroid.api.TaskDto;
import org.udg.pds.todoandroid.util.Resource;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Repository for Task data operations.
 * Single source of truth for task data in the app.
 * Handles data from network (and could handle local cache in the future).
 */
@Singleton
public class TaskRepository {

    private final ApiService apiService;

    @Inject
    public TaskRepository(ApiService apiService) {
        this.apiService = apiService;
    }

    /**
     * Fetch all tasks from the server.
     * @return LiveData with Resource wrapper containing loading/success/error states
     */
    public LiveData<Resource<List<TaskDto>>> getTasks() {
        MutableLiveData<Resource<List<TaskDto>>> result = new MutableLiveData<>();
        result.setValue(Resource.loading(null));

        apiService.getTasks().enqueue(new Callback<List<TaskDto>>() {
            @Override
            public void onResponse(@NonNull Call<List<TaskDto>> call,
                                   @NonNull Response<List<TaskDto>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    result.setValue(Resource.success(response.body()));
                } else {
                    result.setValue(Resource.error("Failed to load tasks", null));
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<TaskDto>> call, @NonNull Throwable t) {
                result.setValue(Resource.error("Network error: " + t.getMessage(), null));
            }
        });

        return result;
    }

    /**
     * Create a new task on the server.
     * @param request The task creation request
     * @return LiveData with Resource wrapper containing the created task ID
     */
    public LiveData<Resource<IdDto>> createTask(CreateTaskRequest request) {
        MutableLiveData<Resource<IdDto>> result = new MutableLiveData<>();
        result.setValue(Resource.loading(null));

        apiService.createTask(request).enqueue(new Callback<IdDto>() {
            @Override
            public void onResponse(@NonNull Call<IdDto> call,
                                   @NonNull Response<IdDto> response) {
                if (response.isSuccessful() && response.body() != null) {
                    result.setValue(Resource.success(response.body()));
                } else {
                    result.setValue(Resource.error("Failed to create task", null));
                }
            }

            @Override
            public void onFailure(@NonNull Call<IdDto> call, @NonNull Throwable t) {
                result.setValue(Resource.error("Network error: " + t.getMessage(), null));
            }
        });

        return result;
    }
}

