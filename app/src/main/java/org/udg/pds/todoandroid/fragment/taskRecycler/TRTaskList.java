package org.udg.pds.todoandroid.fragment.taskRecycler;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import org.udg.pds.todoandroid.R;
import org.udg.pds.todoandroid.activity.AddTask;
import org.udg.pds.todoandroid.entity.Task;
import org.udg.pds.todoandroid.fragment.TaskList;
import org.udg.pds.todoandroid.util.Global;

import java.util.List;

public class TRTaskList extends TaskList {

  RecyclerView mRecyclerView;
  private TRAdapter mAdapter;

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.task_list_recycler, container, false);
    Button b = (Button) view.findViewById(R.id.b_add_task_rv);

    mRecyclerView = (RecyclerView) view.findViewById(R.id.task_recyclerview);
    mAdapter = new TRAdapter(getActivity().getApplication());
    mRecyclerView.setAdapter(mAdapter);
    mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

    // This is the listener to the "Add Task" button
    b.setOnClickListener(new View.OnClickListener() {
      public void onClick(View view) {
        // When we press the "Add Task" button, the AddTask activity is called, where
        // we can introduce the data of the new task
        Intent i = new Intent(getActivity(), AddTask.class);
        // We launch the activity with startActivityForResult because we want to know when
        // the launched activity has finished. In this case, when the AddTask activity has finished
        // we will update the list to show the new task.
        startActivityForResult(i, Global.RQ_ADD_TASK);
      }
    });
    return view;
  }

  @Override
  public void showTaskList(List<Task> tl) {
    mAdapter.clear();
    for (Task t : tl) {
      mAdapter.add(t);
    }
  }
}
