package org.udg.pds.todoandroid.fragment;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.NavDirections;
import androidx.navigation.fragment.NavHostFragment;

import org.udg.pds.todoandroid.R;
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

    int day, month, year, hour, minute;
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
                year = calendar.get(Calendar.YEAR);
                month = calendar.get(Calendar.MONTH);
                day = calendar.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog datePickerDialog = new DatePickerDialog(AddTaskFragment.this.getActivity(), AddTaskFragment.this, year, month, day);
                datePickerDialog.show();            }
        });


        Button save = root.findViewById(R.id.at_save_button);
        // When the "Save" button is pressed, we make the call to the responder
        save.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                TextView textv = root.findViewById(R.id.at_text);

                try {
                    String text = textv.getText().toString();
                    Task task = new Task();
                    task.text = text;
                    task.dateLimit = AddTaskFragment.this.dateTimeLimit;
                    task.dateCreated = ZonedDateTime.now();
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
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        myYear = year;
        myDay = day;
        myMonth = month;
        Calendar c = Calendar.getInstance();
        hour = c.get(Calendar.HOUR);
        minute = c.get(Calendar.MINUTE);
        TimePickerDialog timePickerDialog = new TimePickerDialog(this.getActivity(), this, hour, minute, DateFormat.is24HourFormat(this.getActivity()));
        timePickerDialog.show();
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        myHour = hourOfDay;
        myMinute = minute;
        dateTimeLimit = ZonedDateTime.of(myYear, myMonth+1, myDay+1, myHour, myMinute, 0, 0, ZoneId.systemDefault());
        binding.atDateLimit.setText(dateTimeLimit.format(TodoApp.noZoneFormatter));
    }

    // This class is a Dialog used by the user to introduce a time (HH::mm)
    // It is shown when the user presses the "Set" button
    // in the "time limit" field
    public static class TimePickerFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener {

        Handler mH;

        public void setHandler(Handler h) {
            mH = h;
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current time as the default values for the picker
            final Calendar c = Calendar.getInstance();
            int hour = c.get(Calendar.HOUR_OF_DAY);
            int minute = c.get(Calendar.MINUTE);

            // Create a new instance of TimePickerDialog and return it
            return new TimePickerDialog(getActivity(), this, hour, minute,
                DateFormat.is24HourFormat(getActivity()));
        }

        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            Message msg = new Message();
            Bundle data = new Bundle();
            data.putInt("hour", hourOfDay);
            data.putInt("minute", minute);
            msg.setData(data);
            mH.sendMessage(msg);
        }
    }

    // This class is a Dialog user by the user to introduce a time (dd/mm/yyyy)
    // It is shown when the user presses the "Set" button
    // in the "date limit" field
    public static class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

        Handler mH;

        public void setHandler(Handler h) {
            mH = h;
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default values for the picker
            final Calendar c = Calendar.getInstance();
            int day = c.get(Calendar.DAY_OF_MONTH);
            int month = c.get(Calendar.MONTH);
            int year = c.get(Calendar.YEAR);

            // Create a new instance of TimePickerDialog and return it
            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            Message msg = new Message();
            Bundle data = new Bundle();
            data.putInt("day", day);
            data.putInt("month", month);
            data.putInt("year", year);

            msg.setData(data);
            mH.sendMessage(msg);
        }
    }


}
