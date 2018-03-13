package org.udg.pds.todoandroid.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import org.udg.pds.todoandroid.R;
import org.udg.pds.todoandroid.TodoApp;
import org.udg.pds.todoandroid.rest.TodoApi;

// FragmentActivity is a base class for activities that want to use the support-based Fragment and Loader APIs.
// http://developer.android.com/reference/android/support/v4/app/FragmentActivity.html
public class NavigationActivity extends Activity {

  private TaskList mTaskList;

  TodoApi mTodoService;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.main);

    mTodoService = ((TodoApp)this.getApplication()).getAPI();

    final FrameLayout content = (FrameLayout) findViewById(R.id.main_content);
    BottomNavigationView bottomNavigationView = (BottomNavigationView)
            findViewById(R.id.bottom_navigation);

    bottomNavigationView.setOnNavigationItemSelectedListener(
            new BottomNavigationView.OnNavigationItemSelectedListener() {
              @Override
              public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                  case R.id.action_favorites:
                      content.removeAllViews();
                      getLayoutInflater().inflate(R.layout.content_favorites, content);
                      break;
                  case R.id.action_schedules:
                      content.removeAllViews();
                      getLayoutInflater().inflate(R.layout.content_schedules, content);
                      break;
                  case R.id.action_tasks:
                    NavigationActivity.this.startActivity(new Intent(NavigationActivity.this, TaskList.class));
                }
                return true;
              }
            });

    switch (bottomNavigationView.getSelectedItemId()) {
      case R.id.action_favorites:
        getLayoutInflater().inflate(R.layout.content_favorites, content);
        break;
      case R.id.action_schedules:
        getLayoutInflater().inflate(R.layout.content_schedules, content);
        break;
      case R.id.action_tasks:
    }
  }
}