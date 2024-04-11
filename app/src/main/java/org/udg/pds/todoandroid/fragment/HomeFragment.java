package org.udg.pds.todoandroid.fragment;


import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

import org.udg.pds.todoandroid.R;
import org.udg.pds.todoandroid.TodoApp;
import org.udg.pds.todoandroid.activity.NavigationActivity;
import org.udg.pds.todoandroid.databinding.HomeFragmentBinding;
import org.udg.pds.todoandroid.service.ShowNotification;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {

    private HomeFragmentBinding binding;

    private TodoApp todoApp;

    private final ActivityResultLauncher<String> requestPermissionLauncher =
        registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
            if (isGranted) {
                // Permission is granted. Continue the action or workflow in your
                // app.
                HomeFragment.this.programNotification();
            } else {
                Toast.makeText(HomeFragment.this.requireActivity(), "You need permissions to send notifications!!!!.",
                    Toast.LENGTH_LONG).show();
            }
        });

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = HomeFragmentBinding.inflate(inflater);

        todoApp = ((TodoApp) this.requireActivity().getApplication());

        binding.secondsNot.setText("2");
        binding.buttonNot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(
                    HomeFragment.this.requireActivity(), android.Manifest.permission.POST_NOTIFICATIONS) ==
                    PackageManager.PERMISSION_GRANTED) {
                    // You can use the API that requires the permission.
                    programNotification();
                } else if (ActivityCompat.shouldShowRequestPermissionRationale(
                    HomeFragment.this.requireActivity(), android.Manifest.permission.POST_NOTIFICATIONS)) {
                    // In an educational UI, explain to the user why your app requires this
                    // permission for a specific feature to behave as expected, and what
                    // features are disabled if it's declined. In this UI, include a
                    // "cancel" or "no thanks" button that lets the user continue
                    // using your app without granting the permission.
                    Toast.makeText(HomeFragment.this.requireActivity(), "You need permissions to send notifications!!!!.",
                        Toast.LENGTH_LONG).show();
                } else {
                    // You can directly ask for the permission.
                    // The registered ActivityResultCallback gets the result of this request.
                    requestPermissionLauncher.launch(
                        android.Manifest.permission.POST_NOTIFICATIONS);
                }
            }
        });

        return binding.getRoot();
    }

    @Override
    public void onStart() {
        super.onStart();

        ArrayAdapter<CharSequence> adapter_xml = ArrayAdapter.createFromResource(
            requireActivity(),
            R.array.planets_array,
            android.R.layout.simple_spinner_item
        );

        Spinner spDefault = binding.spFromXml;
        adapter_xml.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner.
        spDefault.setAdapter(adapter_xml);

        ArrayAdapter<CharSequence> adapter_runtime = new ArrayAdapter<>(
            requireActivity(),
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

    private void programNotification() {
        Intent notificationIntent = new Intent(this.requireActivity(), ShowNotification.class);

        PendingIntent contentIntent = PendingIntent.getService(this.requireActivity(), 0,
                notificationIntent, PendingIntent.FLAG_CANCEL_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        AlarmManager am = (AlarmManager) this.requireActivity().getSystemService(Context.ALARM_SERVICE);
        // am.cancel(contentIntent);
        if (am.canScheduleExactAlarms()) {
            long secs = Long.parseLong(binding.secondsNot.getText().toString());
            var triggerAtMillis = System.currentTimeMillis() + secs * 1000;
            am.setExact(AlarmManager.RTC_WAKEUP, triggerAtMillis, contentIntent);
        } else {
            var i = new Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            this.requireActivity().startActivity(i);
        }
    }
}
