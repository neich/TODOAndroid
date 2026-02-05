package org.udg.pds.todoandroid.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import org.udg.pds.todoandroid.R;
import org.udg.pds.todoandroid.databinding.FragmentTaskDetailBinding;

import java.time.ZonedDateTime;
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

        setupToolbar();
        displayTaskDetails();
    }

    private void setupToolbar() {
        binding.toolbar.setNavigationOnClickListener(v ->
                Navigation.findNavController(v).navigateUp());
    }

    private void displayTaskDetails() {
        Bundle args = getArguments();
        if (args == null) return;

        String taskText = args.getString(ARG_TASK_TEXT);
        boolean completed = args.getBoolean(ARG_TASK_COMPLETED, false);
        String dateCreatedStr = args.getString(ARG_TASK_DATE_CREATED);
        String dateLimitStr = args.getString(ARG_TASK_DATE_LIMIT);

        // Display task text
        binding.textTaskText.setText(taskText != null ? taskText : "-");

        // Display completed status
        binding.textTaskCompleted.setText(completed ?
                getString(R.string.task_status_completed) :
                getString(R.string.task_status_pending));

        // Display date created
        if (dateCreatedStr != null && !dateCreatedStr.isEmpty()) {
            try {
                ZonedDateTime dateCreated = ZonedDateTime.parse(dateCreatedStr);
                binding.textTaskDateCreated.setText(dateCreated.format(displayFormatter));
            } catch (Exception e) {
                binding.textTaskDateCreated.setText(dateCreatedStr);
            }
        } else {
            binding.textTaskDateCreated.setText("-");
        }

        // Display date limit
        if (dateLimitStr != null && !dateLimitStr.isEmpty()) {
            try {
                ZonedDateTime dateLimit = ZonedDateTime.parse(dateLimitStr);
                binding.textTaskDateLimit.setText(dateLimit.format(displayFormatter));
            } catch (Exception e) {
                binding.textTaskDateLimit.setText(dateLimitStr);
            }
        } else {
            binding.textTaskDateLimit.setText("-");
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
