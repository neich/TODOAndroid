package org.udg.pds.todoandroid.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import org.udg.pds.todoandroid.api.GroupDto;
import org.udg.pds.todoandroid.databinding.FragmentGroupsBinding;
import org.udg.pds.todoandroid.ui.viewmodel.GroupListViewModel;

import java.util.List;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class GroupsFragment extends Fragment {

    private FragmentGroupsBinding binding;
    private GroupAdapter adapter;
    private GroupListViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentGroupsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this).get(GroupListViewModel.class);

        setupRecyclerView();
        observeGroups();

        if (viewModel.getOwnedGroups().getValue() == null) {
            viewModel.loadOwnedGroups();
        }
    }

    private void setupRecyclerView() {
        adapter = new GroupAdapter();
        binding.recyclerGroups.setLayoutManager(
                new LinearLayoutManager(getContext()));
        binding.recyclerGroups.setAdapter(adapter);
    }

    private void observeGroups() {
        // IMPORTANT: Use getViewLifecycleOwner(), NOT 'this'
        viewModel.getOwnedGroups().observe(getViewLifecycleOwner(), resource -> {
            if (resource == null) return;

            switch (resource.status) {
                case LOADING:
                    showLoading(true);
                    break;
                case SUCCESS:
                    showLoading(false);
                    List<GroupDto> groups = resource.data;
                    if (groups == null || groups.isEmpty()) {
                        showEmpty(true);
                    } else {
                        showEmpty(false);
                        adapter.submitList(groups);
                    }
                    break;
                case ERROR:
                    showLoading(false);
                    Toast.makeText(getContext(),
                            resource.message != null
                                    ? resource.message
                                    : "Failed to load groups",
                            Toast.LENGTH_SHORT).show();
                    break;
            }
        });
    }

    private void showLoading(boolean show) {
        binding.progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        binding.recyclerGroups.setVisibility(show ? View.GONE : View.VISIBLE);
    }

    private void showEmpty(boolean show) {
        binding.textEmpty.setVisibility(show ? View.VISIBLE : View.GONE);
        binding.recyclerGroups.setVisibility(show ? View.GONE : View.VISIBLE);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null; // CRITICAL: Prevents memory leak
    }
}