package org.udg.pds.todoandroid.fragment;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import org.udg.pds.todoandroid.entity.Task;
import org.udg.pds.todoandroid.util.Global;

import java.util.List;

/**
 * Created by imartin on 12/02/16.
 */
public abstract class TaskList extends Fragment {

  protected TaskList.OnTaskListListener mCallback;

  @Override
  public void onAttach(Context ctx) {
    super.onAttach(ctx);
    // This makes sure that the container activity has implemented
    // the callback interface. If not, it throws an exception
    try {
      mCallback = (TaskList.OnTaskListListener) getActivity();
    } catch (ClassCastException e) {
      throw new ClassCastException(getActivity().toString()
          + " must implement OnTaskListListener");
    }
  }

  @Override
  public void onActivityResult(int requestCode, int resultCode, Intent data) {
    if (requestCode == Global.RQ_ADD_TASK) {
      mCallback.updateTaskList();
    }
  }

  public abstract void showTaskList(List<Task> tl);

  // This is the interface that the container activity has to implement
  // in order to use this fragment
  public interface OnTaskListListener {
    public void updateTaskList();
  }
}
