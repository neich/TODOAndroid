package org.udg.pds.todoandroid.ui;

import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.udg.pds.todoandroid.R;
import org.udg.pds.todoandroid.api.TaskDto;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

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
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_task, parent, false);
        return new TaskVH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskVH holder, int position) {
        TaskDto task = items.get(position);
        holder.textTitle.setText(task.text);

        // Format the date limit
        String formattedDate = formatDate(task.dateLimit);
        holder.textDateLimit.setText(formattedDate != null ? "Due: " + formattedDate : "");

        holder.checkCompleted.setChecked(task.completed);

        // Strike through text if completed
        if (task.completed) {
            holder.textTitle.setPaintFlags(holder.textTitle.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            holder.textTitle.setAlpha(0.6f);
        } else {
            holder.textTitle.setPaintFlags(holder.textTitle.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
            holder.textTitle.setAlpha(1.0f);
        }

        // Set click listener
        holder.itemView.setOnClickListener(v -> {
            if (clickListener != null) {
                clickListener.onTaskClick(task);
            }
        });
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

    static class TaskVH extends RecyclerView.ViewHolder {

        TextView textTitle;
        TextView textDateLimit;
        CheckBox checkCompleted;

        TaskVH(@NonNull View itemView) {
            super(itemView);
            textTitle = itemView.findViewById(R.id.textTitle);
            textDateLimit = itemView.findViewById(R.id.textDateLimit);
            checkCompleted = itemView.findViewById(R.id.checkCompleted);
        }
    }
}

