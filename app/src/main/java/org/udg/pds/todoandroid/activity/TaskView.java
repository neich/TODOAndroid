package org.udg.pds.todoandroid.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.view.MenuItem;

import org.udg.pds.todoandroid.databinding.ActivityTaskViewBinding;
import org.udg.pds.todoandroid.entity.Task;

public class TaskView extends AppCompatActivity {

    ActivityTaskViewBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTaskViewBinding.inflate(getLayoutInflater());



        var args = TaskViewArgs.fromBundle(getIntent().getExtras());
        Task t = args.getTaskItem();

        binding.tvDatecreated.setText(t.dateCreated);
        binding.tvDatelimit.setText(t.dateLimit);
        binding.tvText.setText(t.text);

        setContentView(binding.getRoot());

        Toolbar toolbar = binding.toolbar;
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) // Press Back Icon
        {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }
}
