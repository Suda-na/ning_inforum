package com.example.lnforum.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lnforum.R;
import com.example.lnforum.model.CircleComment;

import java.util.List;

public class CircleCommentAdapter extends RecyclerView.Adapter<CircleCommentAdapter.ViewHolder> {

    // 改为普通的点击监听器，用户体验更好
    public interface OnCommentClickListener {
        void onCommentClick(CircleComment comment, View anchor);
    }

    private final List<CircleComment> data;
    private final OnCommentClickListener listener;

    // 构造函数，注意这里接口名字变了
    public CircleCommentAdapter(List<CircleComment> data, OnCommentClickListener listener) {
        this.data = data;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // 确保加载的是 item_circle_comment
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_circle_comment, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CircleComment comment = data.get(position);

        // 1. 绑定文字
        holder.author.setText(comment.getAuthor());
        holder.content.setText(comment.getContent());
        holder.time.setText(comment.getTime());

        // 2. 绑定头像 (如果没有网络加载库，先用本地默认图)
        holder.avatar.setImageResource(R.drawable.ic_user_avatar);

        // 3. 设置点击事件 (改为单击)
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onCommentClick(comment, v);
            }
        });
    }

    @Override
    public int getItemCount() {
        return data == null ? 0 : data.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView author, content, time;
        ImageView avatar; // ✅ 加上头像

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            author = itemView.findViewById(R.id.comment_author);
            content = itemView.findViewById(R.id.comment_content);
            time = itemView.findViewById(R.id.comment_time);
            avatar = itemView.findViewById(R.id.comment_avatar); // ✅ 绑定XML里的头像ID
        }
    }
}