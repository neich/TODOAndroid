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
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import org.udg.pds.todoandroid.R;
import org.udg.pds.todoandroid.api.ApiService;
import org.udg.pds.todoandroid.api.TaskDto;
import org.udg.pds.todoandroid.databinding.FragmentTasksBinding;

import java.time.format.DateTimeFormatter;
import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@AndroidEntryPoint
public class TasksFragment extends Fragment {

    private FragmentTasksBinding binding;
    private TaskAdapter adapter;

    @Inject
    ApiService apiService;

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

        setupMenu();
        setupRecyclerView();
        loadTasks();
    }

    @Override
    public void onResume() {
        super.onResume();
        // Reload tasks when returning from add/detail fragments
        loadTasks();
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

    private void loadTasks() {
        showLoading(true);

        apiService.getTasks().enqueue(new Callback<List<TaskDto>>() {
            @Override
            public void onResponse(@NonNull Call<List<TaskDto>> call,
                                   @NonNull Response<List<TaskDto>> response) {
                showLoading(false);
                if (response.isSuccessful() && response.body() != null) {
                    List<TaskDto> tasks = response.body();
                    if (tasks.isEmpty()) {
                        showEmpty(true);
                    } else {
                        showEmpty(false);
                        adapter.submitList(tasks);
                    }
                } else {
                    showError("Failed to load tasks");
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<TaskDto>> call, @NonNull Throwable t) {
                showLoading(false);
                showError("Network error: " + t.getMessage());
            }
        });
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
