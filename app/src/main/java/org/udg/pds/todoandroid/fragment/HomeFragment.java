package org.udg.pds.todoandroid.fragment;


import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.annotation.NonNull;
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


        return binding.getRoot();
    }

    @Override
    public void onStart() {
        super.onStart();

        ArrayAdapter<CharSequence> adapter_xml = ArrayAdapter.createFromResource(
            getActivity(),
            R.array.planets_array,
            android.R.layout.simple_spinner_item
        );

        Spinner spDefault = binding.spFromXml;
        adapter_xml.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner.
        spDefault.setAdapter(adapter_xml);

        ArrayAdapter<CharSequence> adapter_runtime = new ArrayAdapter<>(
            getActivity(),
            android.R.layout.simple_spinner_item
        );
        adapter_runtime.add("First option");
        adapter_runtime.add("Second option");
        adapter_runtime.add("Last option");


        Spinner spRuntime = binding.spRuntime;
        adapter_runtime.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner.
        spRuntime.setAdapter(adapter_runtime);




    }
}
