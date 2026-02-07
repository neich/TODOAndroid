package org.udg.pds.todoandroid.api;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface ApiService {

    @GET("users")
    Call<List<UserDto>> getUsers();

    @GET("users/check")
    Call<UserDto> checkSession();

    @GET("users/me")
    Call<UserDto> getUserProfile();

    @POST("users/login")
    Call<UserDto> login(@Body LoginCredentials credentials);

    @GET("tasks")
    Call<List<TaskDto>> getTasks();

    @POST("tasks")
    Call<IdDto> createTask(@Body CreateTaskRequest request);

    @POST("users/logout")
    Call<Void> logout();
}
