package com.example.android_java2.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.android_java2.R;
import com.example.android_java2.model.CircleComment;

import java.util.List;

public class CircleCommentAdapter extends RecyclerView.Adapter<CircleCommentAdapter.ViewHolder> {

    public interface OnCommentLongClickListener {
        void onLongClick(CircleComment comment, View anchor);
    }

    private final List<CircleComment> data;
    private final OnCommentLongClickListener listener;

    public CircleCommentAdapter(List<CircleComment> data, OnCommentLongClickListener listener) {
        this.data = data;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_circle_comment, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CircleComment comment = data.get(position);
        holder.author.setText(comment.getAuthor());
        holder.content.setText(comment.getContent());
        holder.time.setText(comment.getTime());
        holder.itemView.setOnLongClickListener(v -> {
            if (listener != null) listener.onLongClick(comment, v);
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return data == null ? 0 : data.size();
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


