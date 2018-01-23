package org.udg.pds.todoandroid.fragment.taskRecycler;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;
import org.udg.pds.todoandroid.R;
import org.udg.pds.todoandroid.entity.Task;

import java.util.ArrayList;
import java.util.List;

public class TRAdapter extends RecyclerView.Adapter<TaskViewHolder> {

  List<Task> list = new ArrayList<>();
  Context context;

  public TRAdapter(Context context) {
    this.context = context;
  }

  @Override
  public TaskViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.task_layout, parent, false);
    TaskViewHolder holder = new TaskViewHolder(v);

    return holder;
  }

  @Override
  public void onBindViewHolder(TaskViewHolder holder, final int position) {
    holder.title.setText(list.get(position).text);
    holder.dateLimit.setText(list.get(position).dateLimit.toString());

    holder.view.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        int duration = Toast.LENGTH_LONG;

        Toast toast = Toast.makeText(context, String.format("Hey, I'm item %1d", position), duration);
        toast.show();
      }
    });

    holder.dateLimit.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        int duration = Toast.LENGTH_LONG;

        Toast toast = Toast.makeText(context, "adios!", duration);
        toast.show();
      }
    });

    animate(holder);
  }

  @Override
  public int getItemCount() {
    return list.size();
  }

  @Override
  public void onAttachedToRecyclerView(RecyclerView recyclerView) {

    super.onAttachedToRecyclerView(recyclerView);
  }

  // Insert a new item to the RecyclerView
  public void insert(int position, Task data) {
    list.add(position, data);
    notifyItemInserted(position);
  }
  // Remove a RecyclerView item containing the Data object
  public void remove(Task data) {
    int position = list.indexOf(data);
    list.remove(position);
    notifyItemRemoved(position);
  }

  public void animate(RecyclerView.ViewHolder viewHolder) {
    final Animation animAnticipateOvershoot = AnimationUtils.loadAnimation(context, R.anim.anticipate_overshoot_interpolator);
    viewHolder.itemView.setAnimation(animAnticipateOvershoot);
  }

  public void add(Task t) {
    list.add(t);
    this.notifyItemInserted(list.size()-1);
  }

  public void clear() {
    int size = list.size();
    list.clear();
    this.notifyItemRangeRemoved(0, size);
  }
}
