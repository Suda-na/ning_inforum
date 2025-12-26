package com.example.lnforum.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lnforum.R;

import java.util.List;

/**
 * 黑名单列表适配器。
 */
public class BlacklistAdapter extends RecyclerView.Adapter<BlacklistAdapter.ViewHolder> {

    public interface OnRemoveClickListener {
        void onRemove(String username);
    }

    private final List<String> data;
    private final OnRemoveClickListener listener;

    public BlacklistAdapter(List<String> data, OnRemoveClickListener listener) {
        this.data = data;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_blacklist, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String username = data.get(position);
        holder.name.setText(username);
        holder.remove.setOnClickListener(v -> {
            if (listener != null) {
                listener.onRemove(username);
            }
        });
    }

    @Override
    public int getItemCount() {
        return data == null ? 0 : data.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView name;
        ImageButton remove;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.blacklist_name);
            remove = itemView.findViewById(R.id.blacklist_remove);
        }
    }
}


