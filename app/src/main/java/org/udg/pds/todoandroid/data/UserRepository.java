package org.udg.pds.todoandroid.data;

import androidx.lifecycle.LiveData;

import org.udg.pds.todoandroid.api.ApiService;
import org.udg.pds.todoandroid.api.UserDto;
import org.udg.pds.todoandroid.data.db.AppDatabase;
import org.udg.pds.todoandroid.data.db.UserEntity;

import java.util.ArrayList;
import java.util.List;
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

    @Inject
    public UserRepository(ApiService api, AppDatabase db) {
        this.api = api;
        this.db = db;
    }

    public LiveData<List<UserEntity>> getUsers() {
        LiveData<List<UserEntity>> dbSource = db.userDao().getAll();

        api.getUsers().enqueue(new Callback<List<UserDto>>() {
            @Override
            public void onResponse(Call<List<UserDto>> call, Response<List<UserDto>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<UserEntity> entities = new ArrayList<>();
                    for (UserDto dto : response.body()) {
                        entities.add(new UserEntity(dto.id, dto.name, dto.email));
                    }
                    Executors.newSingleThreadExecutor().execute(
                            () -> db.userDao().insertAll(entities)
                    );
                }
            }

            @Override
            public void onFailure(Call<List<UserDto>> call, Throwable t) {
                // In a real app, handle error state
            }
        });

        return dbSource;
    }
}
