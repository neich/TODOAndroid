package org.udg.pds.todoandroid.data;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import org.udg.pds.todoandroid.api.ApiService;
import org.udg.pds.todoandroid.api.GroupDto;
import org.udg.pds.todoandroid.util.Resource;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@Singleton
public class GroupRepository {

    private final ApiService apiService;

    @Inject
    public GroupRepository(ApiService apiService) {
        this.apiService = apiService;
    }

    public LiveData<Resource<List<GroupDto>>> getGroupsOwned() {
        MutableLiveData<Resource<List<GroupDto>>> result = new MutableLiveData<>();
        result.setValue(Resource.loading(null));

        apiService.getGroupsOwned().enqueue(new Callback<List<GroupDto>>() {
            @Override
            public void onResponse(@NonNull Call<List<GroupDto>> call,
                                   @NonNull Response<List<GroupDto>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    result.setValue(Resource.success(response.body()));
                } else {
                    result.setValue(Resource.error("Failed to load groups", null));
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<GroupDto>> call,
                                  @NonNull Throwable t) {
                result.setValue(Resource.error("Network error: " + t.getMessage(), null));
            }
        });

        return result;
    }
}