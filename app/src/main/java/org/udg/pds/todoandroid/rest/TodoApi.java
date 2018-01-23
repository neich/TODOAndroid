package org.udg.pds.todoandroid.rest;

import org.udg.pds.todoandroid.entity.Task;
import org.udg.pds.todoandroid.entity.User;
import org.udg.pds.todoandroid.entity.UserLogin;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

import java.util.List;

/**
 * Created by imartin on 13/02/17.
 */
public interface TodoApi {
  @POST("rest/users/auth")
  Call<User> login(@Body UserLogin login);

  @POST("rest/tasks")
  Call<Task> addTask(@Body Task task);

  @GET("rest/tasks")
  Call<List<Task>> getTasks();
}

