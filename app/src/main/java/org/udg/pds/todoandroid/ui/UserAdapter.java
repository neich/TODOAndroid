package org.udg.pds.todoandroid.ui;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.udg.pds.todoandroid.data.db.UserEntity;
import org.udg.pds.todoandroid.databinding.ItemUserBinding;

import java.util.ArrayList;
import java.util.List;

/**
 * RecyclerView Adapter for displaying user items.
 * Uses View Binding for type-safe view access.
 */
public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserVH> {

    private final List<UserEntity> items = new ArrayList<>();

    public void submitList(List<UserEntity> newItems) {
        items.clear();
        if (newItems != null) {
            items.addAll(newItems);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public UserVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemUserBinding binding = ItemUserBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new UserVH(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull UserVH holder, int position) {
        UserEntity user = items.get(position);
        holder.bind(user);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    /**
     * ViewHolder using View Binding for type-safe view access.
     */
    static class UserVH extends RecyclerView.ViewHolder {

        private final ItemUserBinding binding;

        UserVH(@NonNull ItemUserBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(UserEntity user) {
            binding.textName.setText(user.name);
            binding.textEmail.setText(user.email);
        }
    }
}
