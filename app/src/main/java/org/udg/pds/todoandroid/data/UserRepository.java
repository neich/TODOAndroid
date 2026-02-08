package org.udg.pds.todoandroid.data;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import org.udg.pds.todoandroid.api.ApiService;
import org.udg.pds.todoandroid.api.UserDto;
import org.udg.pds.todoandroid.data.db.AppDatabase;
import org.udg.pds.todoandroid.data.db.UserEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.inject.Inject;
import javax.inject.Singleton;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@Singleton
public class UserRepository {

    private final ApiService api;
    private final AppDatabase db;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    @Inject
    public UserRepository(ApiService api, AppDatabase db) {
        this.api = api;
        this.db = db;
    }

    public LiveData<List<UserEntity>> getUsers() {
        LiveData<List<UserEntity>> dbSource = db.userDao().getAll();

        api.getUsers().enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<List<UserDto>> call,
                                   @NonNull Response<List<UserDto>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<UserEntity> entities = new ArrayList<>();
                    for (UserDto dto : response.body()) {
                        entities.add(new UserEntity(dto.id, dto.username, dto.email));
                    }
                    executor.execute(() -> db.userDao().insertAll(entities));
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<UserDto>> call, @NonNull Throwable t) {
                // In a real app, handle error state
            }
        });

        return dbSource;
    }
}
