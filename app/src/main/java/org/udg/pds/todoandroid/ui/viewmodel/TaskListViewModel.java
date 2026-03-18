package org.udg.pds.todoandroid.ui.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.ViewModel;

import org.udg.pds.todoandroid.api.TaskDto;
import org.udg.pds.todoandroid.data.TaskRepository;
import org.udg.pds.todoandroid.util.Resource;

import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

/**
 * ViewModel for TasksFragment.
 * Handles loading and displaying the list of tasks.
 * Uses TaskRepository as the data source (Repository pattern).
 */
@HiltViewModel
public class TaskListViewModel extends ViewModel {

    private final TaskRepository taskRepository;
    private final MediatorLiveData<Resource<List<TaskDto>>> tasks = new MediatorLiveData<>();
    private LiveData<Resource<List<TaskDto>>> currentSource;

    @Inject
    public TaskListViewModel(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    public LiveData<Resource<List<TaskDto>>> getTasks() {
        return tasks;
    }

    public void loadTasks() {
        // Remove previous source if exists
        if (currentSource != null) {
            tasks.removeSource(currentSource);
        }

        // Get new source from repository
        currentSource = taskRepository.getTasks();

        // Add as source to mediator
        tasks.addSource(currentSource, tasks::setValue);
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
        // Remove source to prevent memory leaks
        if (currentSource != null) {
            tasks.removeSource(currentSource);
        }
    }
}
