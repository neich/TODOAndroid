package org.udg.pds.todoandroid.fragment;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.NavDirections;
import androidx.navigation.fragment.NavHostFragment;

import org.udg.pds.todoandroid.TodoApp;
import org.udg.pds.todoandroid.databinding.AddTaskBinding;
import org.udg.pds.todoandroid.entity.IdObject;
import org.udg.pds.todoandroid.entity.Task;
import org.udg.pds.todoandroid.rest.TodoApi;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Calendar;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created with IntelliJ IDEA.
 * User: imartin
 * Date: 29/03/13
 * Time: 0:45
 * To change this template use File | Settings | File Templates.
 */

// Fragment used to create a new task
public class AddTaskFragment extends Fragment implements Callback<IdObject>, DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {

    TodoApi mTodoService;

    int myDay, myMonth, myYear, myHour, myMinute;
    ZonedDateTime dateTimeLimit;

    AddTaskBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = AddTaskBinding.inflate(inflater);
        View root = binding.getRoot();

        mTodoService = ((TodoApp) this.getActivity().getApplication()).getAPI();

        FragmentManager fm = getParentFragmentManager();

        // Show the time selection dialog when the "Set" button is pressed
        binding.atDateLimitButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog datePickerDialog = new DatePickerDialog(AddTaskFragment.this.getActivity(), AddTaskFragment.this, year, month, day);
                datePickerDialog.show();            }
        });


        // When the "Save" button is pressed, we make the call to the responder
        binding.atSaveButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                try {
                    String text = binding.atText.getText().toString();
                    Task task = new Task();
                    task.text = text;
                    task.dateLimit = TodoApp.AppDateFormatter.format(AddTaskFragment.this.dateTimeLimit);
                    task.dateCreated = TodoApp.AppDateFormatter.format(ZonedDateTime.now());
                    Call<IdObject> call = mTodoService.addTask(task);
                    call.enqueue(AddTaskFragment.this);
                } catch (Exception ex) {
                    Toast.makeText(AddTaskFragment.this.getContext(), "Error creating limit date", Toast.LENGTH_LONG).show();
                }

            }
        });
        return root;
    }

    @Override
    public void onResponse(Call<IdObject> call, Response<IdObject> response) {
        if (response.isSuccessful()) {
            NavDirections action =
                AddTaskFragmentDirections
                    .actionAddTaskFragmentToActionTasks();
            NavHostFragment.findNavController(this).navigate(action);
        } else {
            Toast.makeText(this.getContext(), "Error adding task", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onFailure(Call<IdObject> call, Throwable t) {
        Toast.makeText(this.getContext(), "Failure adding task", Toast.LENGTH_LONG).show();

    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int day) {
        myYear = year;
        myDay = day;
        myMonth = month;
        Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR);
        int minute = c.get(Calendar.MINUTE);
        TimePickerDialog timePickerDialog = new TimePickerDialog(this.getActivity(), this, hour, minute, DateFormat.is24HourFormat(this.getActivity()));
        timePickerDialog.show();
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        myHour = hourOfDay;
        myMinute = minute;
        dateTimeLimit = ZonedDateTime.of(myYear, myMonth+1, myDay, myHour, myMinute, 0, 0, ZoneId.systemDefault());
        binding.atDateLimit.setText(dateTimeLimit.format(TodoApp.AppDateFormatter));
    }

}
