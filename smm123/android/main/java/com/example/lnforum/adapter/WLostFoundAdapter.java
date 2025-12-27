package com.example.lnforum.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lnforum.R;
import com.example.lnforum.model.WLostFoundItem;
import com.example.lnforum.utils.ImageLoader;

import java.util.ArrayList;
import java.util.List;

public class WLostFoundAdapter extends RecyclerView.Adapter<WLostFoundAdapter.ViewHolder> {

    public interface OnItemClickListener {
        void onOpen(WLostFoundItem item);
    }

    private final List<WLostFoundItem> data;
    private final OnItemClickListener listener;
    private final Context context;

    public WLostFoundAdapter(Context context, List<WLostFoundItem> list, OnItemClickListener listener) {
        this.context = context;
        this.listener = listener;
        this.data = list != null ? list : new ArrayList<>();  // 直接引用，不复制
    }

    public void setData(List<WLostFoundItem> list) {
        data.clear();
        if (list != null) {
            data.addAll(list);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_lost_found, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        WLostFoundItem item = data.get(position);
        holder.title.setText(item.getTitle());
        holder.desc.setText(item.getDesc());
        holder.tag.setText(item.getTag());
        holder.user.setText(item.getUser());
        holder.time.setText(item.getTime());
        if (holder.views != null) {
            holder.views.setText(String.valueOf(item.getViews()));
        }
        
        // 加载头像（使用ImageLoader工具类，参考圈子动态的实现）
        String avatarUrl = item.getAvatar();
        android.util.Log.d("WLostFoundAdapter", "加载头像: user=" + item.getUser() + ", avatarUrl=" + avatarUrl);
        ImageLoader.loadAvatar(avatarUrl, holder.avatar);

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onOpen(item);
            }
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView title, desc, tag, user, time, views;
        ImageView avatar;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.item_title);
            desc = itemView.findViewById(R.id.item_desc);
            tag = itemView.findViewById(R.id.item_tag);
            user = itemView.findViewById(R.id.item_user);
            time = itemView.findViewById(R.id.item_time);
            views = itemView.findViewById(R.id.item_views);
            avatar = itemView.findViewById(R.id.item_avatar);
        }
    }
}

