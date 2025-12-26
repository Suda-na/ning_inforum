package com.example.lnforum.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lnforum.R;
import com.example.lnforum.model.WCircleComment;

import java.util.List;

public class WCircleCommentAdapter extends RecyclerView.Adapter<WCircleCommentAdapter.ViewHolder> {
    private static final String TAG = "WCircleCommentAdapter";

    public interface OnCommentLongClickListener {
        void onLongClick(WCircleComment comment, View anchor);
    }

    private final List<WCircleComment> data;
    private final OnCommentLongClickListener listener;

    public WCircleCommentAdapter(List<WCircleComment> data, OnCommentLongClickListener listener) {
        this.data = data;
        this.listener = listener;
        Log.d(TAG, "WCircleCommentAdapter构造函数 - data大小: " + (data != null ? data.size() : "null"));
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.d(TAG, "onCreateViewHolder - 创建ViewHolder");
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_circle_comment, parent, false);
        if (view == null) {
            Log.e(TAG, "onCreateViewHolder - view为null，布局加载失败");
        }
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Log.d(TAG, "onBindViewHolder - position: " + position + ", data大小: " + (data != null ? data.size() : "null"));
        
        if (data == null || position >= data.size()) {
            Log.e(TAG, "onBindViewHolder - data为null或position超出范围");
            return;
        }
        
        WCircleComment comment = data.get(position);
        Log.d(TAG, "onBindViewHolder - 绑定评论: id=" + comment.getId() + ", author=" + comment.getAuthor() + ", content长度=" + (comment.getContent() != null ? comment.getContent().length() : 0));
        
        if (holder.author == null || holder.content == null || holder.time == null) {
            Log.e(TAG, "onBindViewHolder - ViewHolder中的TextView为null");
            Log.e(TAG, "onBindViewHolder - author: " + (holder.author == null ? "null" : "not null"));
            Log.e(TAG, "onBindViewHolder - content: " + (holder.content == null ? "null" : "not null"));
            Log.e(TAG, "onBindViewHolder - time: " + (holder.time == null ? "null" : "not null"));
            return;
        }
        
        holder.author.setText(comment.getAuthor());
        holder.content.setText(comment.getContent());
        holder.time.setText(comment.getTime());
        Log.d(TAG, "onBindViewHolder - 数据已设置到TextView");
        
        holder.itemView.setOnLongClickListener(v -> {
            if (listener != null) listener.onLongClick(comment, v);
            return true;
        });
    }

    @Override
    public int getItemCount() {
        int count = data == null ? 0 : data.size();
        Log.d(TAG, "getItemCount - 返回: " + count);
        return count;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView author, content, time;
        ViewHolder(@NonNull View itemView) {
            super(itemView);
            author = itemView.findViewById(R.id.comment_author);
            content = itemView.findViewById(R.id.comment_content);
            time = itemView.findViewById(R.id.comment_time);
        }
    }
}

