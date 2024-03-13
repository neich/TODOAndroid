package org.udg.pds.todoandroid.fragment;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

import org.udg.pds.todoandroid.R;
import org.udg.pds.todoandroid.activity.NavigationActivity;
import org.udg.pds.todoandroid.databinding.HomeFragmentBinding;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {

    private HomeFragmentBinding binding;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = HomeFragmentBinding.inflate(inflater);
        binding.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavDirections action = HomeFragmentDirections.actionActionHomeToNavDrawerActivity();
                Navigation.findNavController(view).navigate(action);
            }
        });
        binding.button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Another way to navigate
                ((NavigationActivity)(getActivity())).navigateTo(R.id.navDrawerActivity);
            }
        });

        return binding.getRoot();
    }
}
