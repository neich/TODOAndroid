package org.udg.pds.todoandroid.fragment.taskRecycler;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import org.udg.pds.todoandroid.R;

/**
 * Created by imartin on 12/02/16.
 */
public class TaskViewHolder extends RecyclerView.ViewHolder {
  TextView description;
  TextView dateLimit;
  View view;

  TaskViewHolder(View itemView) {
    super(itemView);
    view = itemView;
    description = (TextView) itemView.findViewById(R.id.itemDescription);
    dateLimit = (TextView) itemView.findViewById(R.id.itemDateLimit);


  }
}
