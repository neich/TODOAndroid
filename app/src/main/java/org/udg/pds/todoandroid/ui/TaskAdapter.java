package org.udg.pds.todoandroid.ui;

import android.graphics.Paint;
import android.view.LayoutInflater;
 import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.udg.pds.todoandroid.api.TaskDto;
import org.udg.pds.todoandroid.databinding.ItemTaskBinding;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * RecyclerView Adapter for displaying task items.
 * Uses View Binding for type-safe view access.
 */
public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskVH> {

    public interface OnTaskClickListener {
        void onTaskClick(TaskDto task);
    }

    private final List<TaskDto> items = new ArrayList<>();
    private final DateTimeFormatter displayFormatter;
    private OnTaskClickListener clickListener;

    public TaskAdapter() {
        displayFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm z", Locale.getDefault());
    }

    public void setOnTaskClickListener(OnTaskClickListener listener) {
        this.clickListener = listener;
    }

    public void submitList(List<TaskDto> newItems) {
        items.clear();
        if (newItems != null) {
            items.addAll(newItems);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public TaskVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemTaskBinding binding = ItemTaskBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new TaskVH(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskVH holder, int position) {
        TaskDto task = items.get(position);
        holder.bind(task, clickListener, displayFormatter);
    }

    private String formatDate(ZonedDateTime dateTime) {
        if (dateTime == null) {
            return null;
        }
        return dateTime.format(displayFormatter);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    /**
     * ViewHolder using View Binding for type-safe view access.
     */
    static class TaskVH extends RecyclerView.ViewHolder {

        private final ItemTaskBinding binding;

        TaskVH(@NonNull ItemTaskBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(TaskDto task, OnTaskClickListener clickListener, DateTimeFormatter formatter) {
            binding.textTitle.setText(task.text);

            // Format the date limit
            String formattedDate = task.dateLimit != null ? task.dateLimit.format(formatter) : null;
            binding.textDateLimit.setText(formattedDate != null ? "Due: " + formattedDate : "");

            binding.checkCompleted.setChecked(task.completed);

            // Strike through text if completed
            if (task.completed) {
                binding.textTitle.setPaintFlags(
                        binding.textTitle.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                binding.textTitle.setAlpha(0.6f);
            } else {
                binding.textTitle.setPaintFlags(
                        binding.textTitle.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
                binding.textTitle.setAlpha(1.0f);
            }

            // Set click listener
            itemView.setOnClickListener(v -> {
                if (clickListener != null) {
                    clickListener.onTaskClick(task);
                }
            });
        }
    }
}

