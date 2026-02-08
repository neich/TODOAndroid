package org.udg.pds.todoandroid.ui.viewmodel;

import android.annotation.SuppressLint;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.ViewModel;

import java.time.ZonedDateTime;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

/**
 * ViewModel for TaskDetailFragment.
 * Holds task detail data and survives configuration changes.
 * Uses SavedStateHandle to survive process death.
 */
@HiltViewModel
public class TaskDetailViewModel extends ViewModel {

    private static final String KEY_TASK_TEXT = "taskText";
    private static final String KEY_TASK_COMPLETED = "taskCompleted";
    private static final String KEY_TASK_DATE_CREATED = "taskDateCreated";
    private static final String KEY_TASK_DATE_LIMIT = "taskDateLimit";

    private final SavedStateHandle savedStateHandle;
    private final MutableLiveData<TaskDetailState> taskDetail = new MutableLiveData<>();

    @Inject
    public TaskDetailViewModel(SavedStateHandle savedStateHandle) {
        this.savedStateHandle = savedStateHandle;
        restoreState();
    }

    public LiveData<TaskDetailState> getTaskDetail() {
        return taskDetail;
    }

    /**
     * Initialize task details from arguments.
     * This should be called from the Fragment with the navigation arguments.
     */
    public void setTaskDetails(String text, boolean completed,
                               String dateCreatedStr, String dateLimitStr) {
        // Save to SavedStateHandle for process death survival
        savedStateHandle.set(KEY_TASK_TEXT, text);
        savedStateHandle.set(KEY_TASK_COMPLETED, completed);
        savedStateHandle.set(KEY_TASK_DATE_CREATED, dateCreatedStr);
        savedStateHandle.set(KEY_TASK_DATE_LIMIT, dateLimitStr);

        // Parse dates and create state
        ZonedDateTime dateCreated = parseDateSafely(dateCreatedStr);
        ZonedDateTime dateLimit = parseDateSafely(dateLimitStr);

        taskDetail.setValue(new TaskDetailState(text, completed, dateCreated, dateLimit));
    }

    private void restoreState() {
        String text = savedStateHandle.get(KEY_TASK_TEXT);
        Boolean completed = savedStateHandle.get(KEY_TASK_COMPLETED);
        String dateCreatedStr = savedStateHandle.get(KEY_TASK_DATE_CREATED);
        String dateLimitStr = savedStateHandle.get(KEY_TASK_DATE_LIMIT);

        // Only restore if we have saved state
        if (text != null) {
            ZonedDateTime dateCreated = parseDateSafely(dateCreatedStr);
            ZonedDateTime dateLimit = parseDateSafely(dateLimitStr);
            taskDetail.setValue(new TaskDetailState(
                    text,
                    completed != null ? completed : false,
                    dateCreated,
                    dateLimit
            ));
        }
    }

    @SuppressLint("NewApi") // Safe: desugaring enabled for java.time APIs
    private ZonedDateTime parseDateSafely(String dateStr) {
        if (dateStr == null || dateStr.isEmpty()) {
            return null;
        }
        try {
            return ZonedDateTime.parse(dateStr);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Data class representing task detail state
     */
    public static class TaskDetailState {
        public final String text;
        public final boolean completed;
        public final ZonedDateTime dateCreated;
        public final ZonedDateTime dateLimit;

        public TaskDetailState(String text, boolean completed,
                               ZonedDateTime dateCreated, ZonedDateTime dateLimit) {
            this.text = text;
            this.completed = completed;
            this.dateCreated = dateCreated;
            this.dateLimit = dateLimit;
        }
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        // Release any resources if needed
    }
}



