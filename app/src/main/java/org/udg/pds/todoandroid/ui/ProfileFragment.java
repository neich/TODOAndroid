package org.udg.pds.todoandroid.ui;

import android.content.Intent;
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
import androidx.appcompat.app.AlertDialog;
import androidx.core.view.MenuProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.ViewModelProvider;

import org.udg.pds.todoandroid.R;
import org.udg.pds.todoandroid.databinding.FragmentProfileBinding;
import org.udg.pds.todoandroid.ui.viewmodel.ProfileViewModel;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class ProfileFragment extends Fragment {

    private FragmentProfileBinding binding;
    private ProfileViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, 
                             @Nullable ViewGroup container, 
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize ViewModel
        viewModel = new ViewModelProvider(this).get(ProfileViewModel.class);

        setupMenu();
        observeUserProfile();
        observeLogoutResult();

        // Load user profile data
        viewModel.loadUserProfile();
    }

    private void observeUserProfile() {
        viewModel.getUserProfile().observe(getViewLifecycleOwner(), resource -> {
            if (resource == null) return;

            switch (resource.status) {
                case LOADING:
                    binding.progressBar.setVisibility(View.VISIBLE);
                    binding.profileContent.setVisibility(View.GONE);
                    binding.errorText.setVisibility(View.GONE);
                    break;
                case SUCCESS:
                    binding.progressBar.setVisibility(View.GONE);
                    binding.profileContent.setVisibility(View.VISIBLE);
                    binding.errorText.setVisibility(View.GONE);

                    if (resource.data != null) {
                        binding.userName.setText(resource.data.username);
                        binding.userEmail.setText(resource.data.email);
                        binding.userId.setText(getString(R.string.user_id_format, resource.data.id));
                    }
                    break;
                case ERROR:
                    binding.progressBar.setVisibility(View.GONE);
                    binding.profileContent.setVisibility(View.GONE);
                    binding.errorText.setVisibility(View.VISIBLE);

                    String errorMsg = resource.message != null && resource.message.contains("Network error")
                            ? getString(R.string.error_network)
                            : getString(R.string.profile_load_error);
                    binding.errorText.setText(errorMsg);
                    break;
            }
        });
    }

    private void observeLogoutResult() {
        viewModel.getLogoutResult().observe(getViewLifecycleOwner(), resource -> {
            if (resource == null) return;

            switch (resource.status) {
                case LOADING:
                    // Could show a loading indicator here
                    break;
                case SUCCESS:
                    Toast.makeText(requireContext(), R.string.logout_success, Toast.LENGTH_SHORT).show();
                    navigateToLogin();
                    break;
                case ERROR:
                    String errorMsg = resource.message != null && resource.message.contains("Network error")
                            ? getString(R.string.error_network)
                            : getString(R.string.logout_error);
                    Toast.makeText(requireContext(), errorMsg, Toast.LENGTH_SHORT).show();
                    break;
            }
        });
    }

    private void setupMenu() {
        requireActivity().addMenuProvider(new MenuProvider() {
            @Override
            public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {
                menuInflater.inflate(R.menu.menu_profile, menu);
            }

            @Override
            public boolean onMenuItemSelected(@NonNull MenuItem menuItem) {
                if (menuItem.getItemId() == R.id.action_logout) {
                    showLogoutConfirmationDialog();
                    return true;
                }
                return false;
            }
        }, getViewLifecycleOwner(), Lifecycle.State.RESUMED);
    }

    private void showLogoutConfirmationDialog() {
        new AlertDialog.Builder(requireContext())
                .setTitle(R.string.logout_dialog_title)
                .setMessage(R.string.logout_dialog_message)
                .setPositiveButton(R.string.logout_confirm, (dialog, which) -> viewModel.logout())
                .setNegativeButton(R.string.logout_cancel, null)
                .show();
    }


    private void navigateToLogin() {
        Intent intent = new Intent(requireContext(), LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        requireActivity().finish();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
