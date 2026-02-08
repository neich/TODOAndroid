package org.udg.pds.todoandroid.ui.viewmodel;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import org.udg.pds.todoandroid.api.ApiService;
import org.udg.pds.todoandroid.api.TaskDto;
import org.udg.pds.todoandroid.util.Resource;

import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * ViewModel for TasksFragment.
 * Handles loading and displaying the list of tasks.
 */
@HiltViewModel
public class TaskListViewModel extends ViewModel {

    private final ApiService apiService;
    private final MutableLiveData<Resource<List<TaskDto>>> tasks = new MutableLiveData<>();

    @Inject
    public TaskListViewModel(ApiService apiService) {
        this.apiService = apiService;
    }

    public LiveData<Resource<List<TaskDto>>> getTasks() {
        return tasks;
    }

    public void loadTasks() {
        tasks.setValue(Resource.loading(null));

        apiService.getTasks().enqueue(new Callback<List<TaskDto>>() {
            @Override
            public void onResponse(@NonNull Call<List<TaskDto>> call,
                                   @NonNull Response<List<TaskDto>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    tasks.setValue(Resource.success(response.body()));
                } else {
                    tasks.setValue(Resource.error("Failed to load tasks", null));
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<TaskDto>> call, @NonNull Throwable t) {
                tasks.setValue(Resource.error("Network error: " + t.getMessage(), null));
            }
        });
    }

    /**
     * Refresh tasks - can be called on pull-to-refresh or when returning from add task
     */
    public void refreshTasks() {
        loadTasks();
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        // Release any resources if needed in the future
    }
}
