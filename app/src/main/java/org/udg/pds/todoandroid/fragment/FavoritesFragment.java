package org.udg.pds.todoandroid.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

import org.udg.pds.todoandroid.R;
import org.udg.pds.todoandroid.activity.NavDrawerActivity;
import org.udg.pds.todoandroid.activity.NavigationActivity;
import org.udg.pds.todoandroid.databinding.ContentFavoritesBinding;
import org.udg.pds.todoandroid.databinding.LoginBinding;

/**
 * A simple {@link Fragment} subclass.
 */
public class FavoritesFragment extends Fragment {

    private ContentFavoritesBinding binding;

    public FavoritesFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = ContentFavoritesBinding.inflate(inflater);
        binding.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavDirections action = FavoritesFragmentDirections.actionActionHomeToNavDrawerActivity();
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
