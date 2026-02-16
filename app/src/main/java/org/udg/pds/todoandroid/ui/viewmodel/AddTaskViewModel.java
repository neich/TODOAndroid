package org.udg.pds.todoandroid.ui.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.ViewModel;

import org.udg.pds.todoandroid.api.CreateTaskRequest;
import org.udg.pds.todoandroid.api.IdDto;
import org.udg.pds.todoandroid.data.TaskRepository;
import org.udg.pds.todoandroid.util.Resource;

import java.time.ZonedDateTime;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

/**
 * ViewModel for AddTaskFragment.
 * Handles creating new tasks via TaskRepository.
 */
@HiltViewModel
public class AddTaskViewModel extends ViewModel {

    private final TaskRepository taskRepository;
    private final MediatorLiveData<Resource<IdDto>> createTaskResult = new MediatorLiveData<>();
    private LiveData<Resource<IdDto>> currentSource;

    @Inject
    public AddTaskViewModel(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    public LiveData<Resource<IdDto>> getCreateTaskResult() {
        return createTaskResult;
    }

    public void createTask(String text, ZonedDateTime dateCreated, ZonedDateTime dateLimit) {
        // Remove previous source if exists
        if (currentSource != null) {
            createTaskResult.removeSource(currentSource);
        }

        // Create request and get source from repository
        CreateTaskRequest request = new CreateTaskRequest(text, dateCreated, dateLimit);
        currentSource = taskRepository.createTask(request);

        // Add as source to mediator
        createTaskResult.addSource(currentSource, resource -> createTaskResult.setValue(resource));
    }

    /**
     * Reset the result state
     */
    public void resetCreateTaskResult() {
        if (currentSource != null) {
            createTaskResult.removeSource(currentSource);
            currentSource = null;
        }
        createTaskResult.setValue(null);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        if (currentSource != null) {
            createTaskResult.removeSource(currentSource);
        }
    }
}
