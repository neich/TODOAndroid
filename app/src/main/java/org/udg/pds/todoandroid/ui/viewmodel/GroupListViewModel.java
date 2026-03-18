package org.udg.pds.todoandroid.ui.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.ViewModel;

import org.udg.pds.todoandroid.api.GroupDto;
import org.udg.pds.todoandroid.data.GroupRepository;
import org.udg.pds.todoandroid.util.Resource;

import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class GroupListViewModel extends ViewModel {

    private final GroupRepository groupRepository;
    private final MediatorLiveData<Resource<List<GroupDto>>> ownedGroups =
            new MediatorLiveData<>();
    private LiveData<Resource<List<GroupDto>>> currentSource;

    @Inject
    public GroupListViewModel(GroupRepository groupRepository) {
        this.groupRepository = groupRepository;
    }

    public LiveData<Resource<List<GroupDto>>> getOwnedGroups() {
        return ownedGroups;
    }

    public void loadOwnedGroups() {
        // Remove previous source to prevent memory leaks
        if (currentSource != null) {
            ownedGroups.removeSource(currentSource);
        }
        // Get new source from repository (each call creates a new LiveData)
        currentSource = groupRepository.getGroupsOwned();
        // Bridge the new source into our stable MediatorLiveData
        ownedGroups.addSource(currentSource, resource ->
                ownedGroups.setValue(resource));
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        if (currentSource != null) {
            ownedGroups.removeSource(currentSource);
        }
    }
}