package org.udg.pds.todoandroid.ui;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import org.udg.pds.todoandroid.R;
import org.udg.pds.todoandroid.databinding.FragmentAddTaskBinding;
import org.udg.pds.todoandroid.ui.viewmodel.AddTaskViewModel;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class AddTaskFragment extends Fragment {

    private FragmentAddTaskBinding binding;
    private AddTaskViewModel viewModel;
    private LocalDateTime selectedDateTime = LocalDateTime.now();
    private final DateTimeFormatter displayFormatter = DateTimeFormatter.ofPattern("MMM dd, yyyy 'at' HH:mm z");

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentAddTaskBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize ViewModel
        viewModel = new ViewModelProvider(this).get(AddTaskViewModel.class);

        setupDatePicker();
        setupSaveButton();
        observeCreateTaskResult();
    }

    private void observeCreateTaskResult() {
        viewModel.getCreateTaskResult().observe(getViewLifecycleOwner(), resource -> {
            if (resource == null) return;

            switch (resource.status) {
                case LOADING:
                    showLoading(true);
                    break;
                case SUCCESS:
                    showLoading(false);
                    Toast.makeText(requireContext(), R.string.task_created_success, Toast.LENGTH_SHORT).show();
                    Navigation.findNavController(requireView()).navigateUp();
                    break;
                case ERROR:
                    showLoading(false);
                    String errorMsg = resource.message != null && resource.message.contains("Network error")
                            ? getString(R.string.error_network)
                            : getString(R.string.error_task_creation_failed);
                    Toast.makeText(requireContext(), errorMsg, Toast.LENGTH_SHORT).show();
                    break;
            }
        });
    }


    private void setupDatePicker() {
        binding.editTextDateLimit.setOnClickListener(v -> showDatePicker());
        binding.textInputLayoutDateLimit.setEndIconOnClickListener(v -> showDatePicker());
    }

    private void showDatePicker() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                requireContext(),
                (view, year, month, dayOfMonth) -> {
                    selectedDateTime = selectedDateTime
                            .withYear(year)
                            .withMonth(month + 1)
                            .withDayOfMonth(dayOfMonth);
                    showTimePicker();
                },
                selectedDateTime.getYear(),
                selectedDateTime.getMonthValue() - 1,
                selectedDateTime.getDayOfMonth()
        );
        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis());
        datePickerDialog.show();
    }

    private void showTimePicker() {
        TimePickerDialog timePickerDialog = new TimePickerDialog(
                requireContext(),
                (view, hourOfDay, minute) -> {
                    selectedDateTime = selectedDateTime
                            .withHour(hourOfDay)
                            .withMinute(minute);
                    updateDateDisplay();
                },
                selectedDateTime.getHour(),
                selectedDateTime.getMinute(),
                true // 24-hour format
        );
        timePickerDialog.show();
    }

    private void updateDateDisplay() {
        ZonedDateTime zonedDateTime = selectedDateTime.atZone(ZoneId.systemDefault());
        binding.editTextDateLimit.setText(zonedDateTime.format(displayFormatter));
    }

    private void setupSaveButton() {
        binding.buttonSaveTask.setOnClickListener(v -> saveTask());
    }

    private void saveTask() {
        String taskText = binding.editTextTaskText.getText() != null
                ? binding.editTextTaskText.getText().toString().trim() : "";
        String dateLimit = binding.editTextDateLimit.getText() != null
                ? binding.editTextDateLimit.getText().toString().trim() : "";

        // Validation
        if (taskText.isEmpty()) {
            binding.textInputLayoutTaskText.setError(getString(R.string.error_task_text_required));
            return;
        } else {
            binding.textInputLayoutTaskText.setError(null);
        }

        if (dateLimit.isEmpty()) {
            binding.textInputLayoutDateLimit.setError(getString(R.string.error_date_limit_required));
            return;
        } else {
            binding.textInputLayoutDateLimit.setError(null);
        }

        // Convert LocalDateTime to ZonedDateTime in UTC
        ZonedDateTime zonedDateLimit = selectedDateTime.atZone(ZoneId.systemDefault())
                .withZoneSameInstant(ZoneId.of("UTC"));

        // Set dateCreated to current time
        ZonedDateTime dateCreated = ZonedDateTime.now(ZoneId.of("UTC"));

        // Use ViewModel to create task
        viewModel.createTask(taskText, dateCreated, zonedDateLimit);
    }

    private void showLoading(boolean show) {
        binding.progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        binding.buttonSaveTask.setEnabled(!show);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
