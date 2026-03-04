package org.udg.pds.todoandroid.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import org.udg.pds.todoandroid.R;
import org.udg.pds.todoandroid.databinding.FragmentTaskDetailBinding;
import org.udg.pds.todoandroid.ui.viewmodel.TaskDetailViewModel;

import java.time.format.DateTimeFormatter;
import java.util.Locale;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class TaskDetailFragment extends Fragment {

    private static final String ARG_TASK_TEXT = "taskText";
    private static final String ARG_TASK_COMPLETED = "taskCompleted";
    private static final String ARG_TASK_DATE_CREATED = "taskDateCreated";
    private static final String ARG_TASK_DATE_LIMIT = "taskDateLimit";

    private FragmentTaskDetailBinding binding;
    private TaskDetailViewModel viewModel;
    private final DateTimeFormatter displayFormatter =
            DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy 'at' HH:mm z", Locale.getDefault());

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentTaskDetailBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize ViewModel
        viewModel = new ViewModelProvider(this).get(TaskDetailViewModel.class);

        initializeTaskDetails();
        observeTaskDetail();
    }


    private void initializeTaskDetails() {
        Bundle args = getArguments();
        if (args == null) return;

        // Only initialize if ViewModel doesn't have data (first load)
        if (viewModel.getTaskDetail().getValue() == null) {
            String taskText = args.getString(ARG_TASK_TEXT);
            boolean completed = args.getBoolean(ARG_TASK_COMPLETED, false);
            String dateCreatedStr = args.getString(ARG_TASK_DATE_CREATED);
            String dateLimitStr = args.getString(ARG_TASK_DATE_LIMIT);

            viewModel.setTaskDetails(taskText, completed, dateCreatedStr, dateLimitStr);
        }
    }

    private void observeTaskDetail() {
        viewModel.getTaskDetail().observe(getViewLifecycleOwner(), state -> {
            if (state == null) return;

            // Display task text
            binding.textTaskText.setText(state.text != null ? state.text : "-");

            // Display completed status
            binding.textTaskCompleted.setText(state.completed ?
                    getString(R.string.task_status_completed) :
                    getString(R.string.task_status_pending));

            // Display date created
            if (state.dateCreated != null) {
                binding.textTaskDateCreated.setText(state.dateCreated.format(displayFormatter));
            } else {
                binding.textTaskDateCreated.setText("-");
            }

            // Display date limit
            if (state.dateLimit != null) {
                binding.textTaskDateLimit.setText(state.dateLimit.format(displayFormatter));
            } else {
                binding.textTaskDateLimit.setText("-");
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
