package org.udg.pds.todoandroid.activity;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.widget.Toast;
import org.udg.pds.todoandroid.R;
import org.udg.pds.todoandroid.TodoApp;
import org.udg.pds.todoandroid.entity.Task;
import org.udg.pds.todoandroid.entity.User;
import org.udg.pds.todoandroid.entity.UserLogin;
import org.udg.pds.todoandroid.fragment.Login;
import org.udg.pds.todoandroid.fragment.TaskList;
import org.udg.pds.todoandroid.fragment.taskRecycler.TRTaskList;
import org.udg.pds.todoandroid.rest.TodoApi;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.util.List;

// FragmentActivity is a base class for activities that want to use the support-based Fragment and Loader APIs.
// http://developer.android.com/reference/android/support/v4/app/FragmentActivity.html
public class MainActivity extends Activity implements Callback<User>, Login.OnLoginListener, TaskList.OnTaskListListener {

  private TaskList mTaskList;

  TodoApi mTodoService;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.act_main);

    mTodoService = ((TodoApp)this.getApplication()).getAPI();

    FragmentManager fm = getFragmentManager();

    // Check if the TaskList fragment exists. If not it means that the user has not authenticated
    // show the login fragment
    mTaskList = (TRTaskList) fm.findFragmentByTag("TaskList");

    if (mTaskList != null) {
      updateTaskList();
    } else {
      Fragment login = (Login) fm.findFragmentByTag("login");
      if (login == null)
        login();
    }
  }

  /**
   * Shows the login fragment
   */
  void login() {
    FragmentManager fm = getFragmentManager();
    FragmentTransaction ft = fm.beginTransaction();
    Login l = new Login();
    ft.add(R.id.fragment_content, l, "login");
    ft.commit();
  }

  // This method is called when the "Login" button is pressed in the Login fragment
  public void checkCredentials(String username, String password) {
    UserLogin ul = new UserLogin();
    ul.username = username;
    ul.password = password;
    Call<User> call = mTodoService.login(ul);
    call.enqueue(this);
  }


  // This function is called whenever we want to update the task list
  // It just calls the responder to make the REST call
  public void updateTaskList() {

    Call<List<Task>> call = mTodoService.getTasks();

    call.enqueue(new Callback<List<Task>>() {
      @Override
      public void onResponse(Call<List<Task>> call, Response<List<Task>> response) {
        if (response.isSuccessful()) {
          mTaskList.showTaskList(response.body());
        } else {
          Toast.makeText(MainActivity.this, "Error reading tasks", Toast.LENGTH_LONG);
        }
      }

      @Override
      public void onFailure(Call<List<Task>> call, Throwable t) {

      }
    });
  }

  @Override
  public void onResponse(Call<User> call, Response<User> response) {

    if (response.isSuccessful()) {
      FragmentManager fm = getFragmentManager();
      FragmentTransaction ft = fm.beginTransaction();

      // mTaskList =  new TLVTaskList();
      mTaskList = new TRTaskList();

      ft.replace(R.id.fragment_content, mTaskList, "TaskList");
      ft.commit();
      this.updateTaskList();
    } else {
      Toast toast = Toast.makeText(this, "Error logging in", Toast.LENGTH_SHORT);
      toast.show();
    }
  }

  @Override
  public void onFailure(Call<User> call, Throwable t) {
    Toast toast = Toast.makeText(this, "Error logging in", Toast.LENGTH_SHORT);
    toast.show();
  }
}