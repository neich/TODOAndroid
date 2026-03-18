package org.udg.pds.todoandroid.ui;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.udg.pds.todoandroid.api.GroupDto;
import org.udg.pds.todoandroid.databinding.ItemGroupBinding;

import java.util.ArrayList;
import java.util.List;

public class GroupAdapter extends RecyclerView.Adapter<GroupAdapter.GroupVH> {

    private final List<GroupDto> items = new ArrayList<>();

    public void submitList(List<GroupDto> newItems) {
        items.clear();
        if (newItems != null) {
            items.addAll(newItems);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public GroupVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemGroupBinding binding = ItemGroupBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new GroupVH(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull GroupVH holder, int position) {
        holder.bind(items.get(position));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class GroupVH extends RecyclerView.ViewHolder {
        private final ItemGroupBinding binding;

        GroupVH(@NonNull ItemGroupBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(GroupDto group) {
            binding.textGroupName.setText(group.name);
            binding.textGroupDescription.setText(group.description);
        }
    }
}