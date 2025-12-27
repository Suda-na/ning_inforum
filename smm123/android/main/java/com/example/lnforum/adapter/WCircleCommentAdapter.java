package com.example.lnforum.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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
        
        // 检查是否是已删除的评论
        boolean isDeleted = "该评论已被删除".equals(comment.getContent()) && 
                          (comment.getAuthor() == null || comment.getAuthor().isEmpty());
        
        if (isDeleted) {
            // 已删除的评论：不显示用户和时间
            holder.author.setVisibility(View.GONE);
            holder.time.setVisibility(View.GONE);
            holder.avatar.setVisibility(View.GONE);
            holder.content.setText("该评论已被删除");
            holder.content.setTextColor(holder.itemView.getContext().getResources().getColor(android.R.color.darker_gray));
            // 已删除的评论不能长按
            holder.itemView.setOnLongClickListener(null);
        } else {
            // 正常评论：显示用户和时间
            holder.author.setVisibility(View.VISIBLE);
            holder.time.setVisibility(View.VISIBLE);
            holder.avatar.setVisibility(View.VISIBLE);
            holder.author.setText(comment.getAuthor());
            holder.content.setText(comment.getContent());
            holder.time.setText(comment.getTime());
            holder.content.setTextColor(holder.itemView.getContext().getResources().getColor(android.R.color.black));
            
            // 加载头像（使用ImageLoader工具类）
            com.example.lnforum.utils.ImageLoader.loadAvatar(comment.getAvatar(), holder.avatar);
            
            // 头像点击事件：跳转到用户主页（但不能点击自己的头像）
            holder.avatar.setOnClickListener(v -> {
                // 检查是否是当前登录用户
                com.example.lnforum.repository.CSessionManager sessionManager = 
                    com.example.lnforum.repository.CSessionManager.getInstance(v.getContext());
                com.example.lnforum.model.CUser currentUser = sessionManager.getCurrentCUser();
                
                // 如果是自己的评论，不跳转
                if (currentUser != null && currentUser.getUsername() != null) {
                    if (currentUser.getUsername().equals(comment.getAuthor())) {
                        // 是自己的评论，不跳转
                        return;
                    }
                }
                
                // 不是自己的评论，可以跳转
                if (comment.getAuthor() != null && !comment.getAuthor().isEmpty()) {
                    android.content.Intent intent = new android.content.Intent(v.getContext(), com.example.lnforum.activity.UserProfileActivity.class);
                    intent.putExtra(com.example.lnforum.activity.UserProfileActivity.EXTRA_USER_NAME, comment.getAuthor());
                    v.getContext().startActivity(intent);
                }
            });
            
            holder.itemView.setOnLongClickListener(v -> {
                if (listener != null) listener.onLongClick(comment, v);
                return true;
            });
        }
        Log.d(TAG, "onBindViewHolder - 数据已设置到TextView");
    }

    @Override
    public int getItemCount() {
        int count = data == null ? 0 : data.size();
        Log.d(TAG, "getItemCount - 返回: " + count);
        return count;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView author, content, time;
        ImageView avatar;
        ViewHolder(@NonNull View itemView) {
            super(itemView);
            author = itemView.findViewById(R.id.comment_author);
            content = itemView.findViewById(R.id.comment_content);
            time = itemView.findViewById(R.id.comment_time);
            avatar = itemView.findViewById(R.id.comment_avatar);
        }
    }
}

