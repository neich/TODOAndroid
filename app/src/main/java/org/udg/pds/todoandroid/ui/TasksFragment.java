package org.udg.pds.todoandroid.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.MenuProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import org.udg.pds.todoandroid.R;
import org.udg.pds.todoandroid.api.TaskDto;
import org.udg.pds.todoandroid.databinding.FragmentTasksBinding;
import org.udg.pds.todoandroid.ui.viewmodel.TaskListViewModel;

import java.time.format.DateTimeFormatter;
import java.util.List;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class TasksFragment extends Fragment {

    private FragmentTasksBinding binding;
    private TaskAdapter adapter;
    private TaskListViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, 
                             @Nullable ViewGroup container, 
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentTasksBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize ViewModel
        viewModel = new ViewModelProvider(this).get(TaskListViewModel.class);

        setupMenu();
        setupRecyclerView();
        observeTasks();

        // Only load tasks if we don't already have data
        if (viewModel.getTasks().getValue() == null) {
            viewModel.loadTasks();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        // Reload tasks when returning from add/detail fragments
        // Only if viewModel is initialized
        if (viewModel != null) {
            viewModel.refreshTasks();
        }
    }

    private void observeTasks() {
        viewModel.getTasks().observe(getViewLifecycleOwner(), resource -> {
            if (resource == null) return;

            switch (resource.status) {
                case LOADING:
                    showLoading(true);
                    break;
                case SUCCESS:
                    showLoading(false);
                    List<TaskDto> tasks = resource.data;
                    if (tasks == null || tasks.isEmpty()) {
                        showEmpty(true);
                    } else {
                        showEmpty(false);
                        adapter.submitList(tasks);
                    }
                    break;
                case ERROR:
                    showLoading(false);
                    showError(resource.message != null ? resource.message : "Failed to load tasks");
                    break;
            }
        });
    }

    private void setupMenu() {
        requireActivity().addMenuProvider(new MenuProvider() {
            @Override
            public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {
                menuInflater.inflate(R.menu.menu_tasks, menu);
            }

            @Override
            public boolean onMenuItemSelected(@NonNull MenuItem menuItem) {
                if (menuItem.getItemId() == R.id.action_add_task) {
                    navigateToAddTask();
                    return true;
                }
                return false;
            }
        }, getViewLifecycleOwner(), Lifecycle.State.RESUMED);
    }

    private void navigateToAddTask() {
        Navigation.findNavController(requireView())
                .navigate(R.id.action_tasksFragment_to_addTaskFragment);
    }

    private void setupRecyclerView() {
        adapter = new TaskAdapter();
        adapter.setOnTaskClickListener(this::navigateToTaskDetail);
        binding.recyclerTasks.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recyclerTasks.setAdapter(adapter);
    }

    private void navigateToTaskDetail(TaskDto task) {
        Bundle args = new Bundle();
        args.putString("taskText", task.text);
        args.putBoolean("taskCompleted", task.completed);

        if (task.dateCreated != null) {
            args.putString("taskDateCreated",
                    task.dateCreated.format(DateTimeFormatter.ISO_ZONED_DATE_TIME));
        }
        if (task.dateLimit != null) {
            args.putString("taskDateLimit",
                    task.dateLimit.format(DateTimeFormatter.ISO_ZONED_DATE_TIME));
        }

        Navigation.findNavController(requireView())
                .navigate(R.id.action_tasksFragment_to_taskDetailFragment, args);
    }


    private void showLoading(boolean show) {
        binding.progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        binding.recyclerTasks.setVisibility(show ? View.GONE : View.VISIBLE);
    }

    private void showEmpty(boolean show) {
        binding.textEmpty.setVisibility(show ? View.VISIBLE : View.GONE);
        binding.recyclerTasks.setVisibility(show ? View.GONE : View.VISIBLE);
    }

    private void showError(String message) {
        if (getContext() != null) {
            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
