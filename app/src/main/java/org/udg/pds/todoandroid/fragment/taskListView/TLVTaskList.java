package org.udg.pds.todoandroid.fragment.taskListView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;
import org.udg.pds.todoandroid.R;
import org.udg.pds.todoandroid.activity.AddTask;
import org.udg.pds.todoandroid.entity.Task;
import org.udg.pds.todoandroid.fragment.TaskList;
import org.udg.pds.todoandroid.util.Global;

import java.util.List;

public class TLVTaskList extends TaskList implements AdapterView.OnItemClickListener {

  ListView mListView;
  // This is the adapter responsible to show the list
  private TLVAdapter mAdapter;

  public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
    Context context = getActivity().getApplicationContext();
    CharSequence text = ((Task) adapterView.getItemAtPosition(i)).text;
    int duration = Toast.LENGTH_LONG;

    Toast toast = Toast.makeText(context, text, duration);
    toast.show();
  }


  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.task_list, container, false);
    mListView = (ListView) view.findViewById(R.id.task_listview);
    Button b = (Button) view.findViewById(R.id.b_add_task);
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

    mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
      @Override
      public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        Context context = getActivity().getApplicationContext();
        CharSequence text = ((Task) mAdapter.getItem(i)).text;
        int duration = Toast.LENGTH_LONG;
        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
      }

    });

    return view;
  }


  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    // This tells our Activity to keep the same instance of this
    // Fragment when the Activity is re-created during lifecycle
    // events.
    setRetainInstance(true);
  }

  @Override
  public void onActivityCreated(Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);

    // Since onActivityCreated can be called multiple times in the Fragment lifecycle, we only initialize the
    // adapter if it is null (the first time)
    if (mAdapter == null) {
      mAdapter = new TLVAdapter(getActivity(), R.layout.task_layout);
      mListView.setAdapter(mAdapter);
    }

    mListView.setOnItemClickListener(this);

  }


  // This function is called from the container activity whenever it has received
  // a new task list from a REST responder
  public void showTaskList(List<Task> tl) {
    // Load our list adapter with our Tasks. That would cause and automatic update
    // of the corresponding ListView
    mAdapter.clear();
    for (Task t : tl) {
      mAdapter.add(t);
    }
  }

  // This function is called whenever an Activity launched with startActivityForResult
  // finishes

}