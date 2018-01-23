package org.udg.pds.todoandroid.fragment.taskRecycler;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import org.udg.pds.todoandroid.R;

/**
 * Created by imartin on 12/02/16.
 */
public class TaskViewHolder extends RecyclerView.ViewHolder {
  TextView title;
  TextView dateLimit;
  View view;

  TaskViewHolder(View itemView) {
    super(itemView);
    view = itemView;
    title = (TextView) itemView.findViewById(R.id.taskitem_title);
    dateLimit = (TextView) itemView.findViewById(R.id.taskitem_date_limit);


  }
}
