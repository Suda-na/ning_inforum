package com.example.lnforum.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lnforum.R;
import com.example.lnforum.activity.UserProfileActivity;
import com.example.lnforum.model.WCirclePost;

import java.util.List;

public class WCirclePostAdapter extends RecyclerView.Adapter<WCirclePostAdapter.ViewHolder> {

    public interface OnPostActionListener {
        void onOpen(WCirclePost post);
        void onLike(WCirclePost post);
    }

    private final List<WCirclePost> data;
    private final OnPostActionListener listener;
    private final Context context;

    public WCirclePostAdapter(Context context, List<WCirclePost> data, OnPostActionListener listener) {
        this.context = context;
        this.data = data;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_circle_post, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        WCirclePost post = data.get(position);
        holder.author.setText(post.getAuthor());
        holder.time.setText(post.getTime());
        holder.title.setText(post.getTitle());
        holder.content.setText(post.getContent());
        holder.tag.setText(post.getTag());
        holder.views.setText(String.valueOf(post.getViews()));
        holder.comments.setText(String.valueOf(post.getComments()));
        holder.likes.setText(String.valueOf(post.getLikes()));

        // 加载头像（使用ImageLoader工具类）
        com.example.lnforum.utils.ImageLoader.loadAvatar(post.getAvatar(), holder.avatar);
        
        // 头像点击查看用户主页
        holder.avatar.setOnClickListener(v -> {
            Intent intent = new Intent(context, UserProfileActivity.class);
            intent.putExtra(UserProfileActivity.EXTRA_USER_NAME, post.getAuthor());
            context.startActivity(intent);
        });
        
        // 加载帖子图片
        if (post.getImages() != null && !post.getImages().isEmpty()) {
            holder.imagesContainer.setVisibility(android.view.View.VISIBLE);
            holder.imagesContainer.removeAllViews();
            for (String imageUrl : post.getImages()) {
                if (imageUrl != null && !imageUrl.isEmpty()) {
                    android.widget.ImageView imageView = new android.widget.ImageView(context);
                    android.widget.LinearLayout.LayoutParams params = new android.widget.LinearLayout.LayoutParams(
                        android.view.ViewGroup.LayoutParams.MATCH_PARENT,
                        android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
                    params.setMargins(0, 4, 0, 4);
                    imageView.setLayoutParams(params);
                    imageView.setScaleType(android.widget.ImageView.ScaleType.CENTER_CROP);
                    imageView.setAdjustViewBounds(true);
                    imageView.setMaxHeight(400);
                    // 使用ImageLoader工具类加载图片
                    com.example.lnforum.utils.ImageLoader.loadImageWithMaxHeight(imageUrl, imageView, 400);
                    holder.imagesContainer.addView(imageView);
                }
            }
        } else {
            holder.imagesContainer.setVisibility(android.view.View.GONE);
        }

        // 设置点赞图标颜色
        if (post.isLiked()) {
            holder.likeIcon.setColorFilter(ContextCompat.getColor(context, android.R.color.holo_red_dark));
        } else {
            holder.likeIcon.setColorFilter(ContextCompat.getColor(context, R.color.nav_unselected));
        }

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onOpen(post);
        });

        holder.likeIcon.setOnClickListener(v -> {
            if (listener != null) listener.onLike(post);
        });
    }
    

    @Override
    public int getItemCount() {
        return data == null ? 0 : data.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView author, time, title, content, tag, views, comments, likes;
        ImageView likeIcon, avatar;
        LinearLayout imagesContainer;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            author = itemView.findViewById(R.id.post_author);
            time = itemView.findViewById(R.id.post_time);
            title = itemView.findViewById(R.id.post_title);
            content = itemView.findViewById(R.id.post_content);
            tag = itemView.findViewById(R.id.post_tag);
            views = itemView.findViewById(R.id.post_views);
            comments = itemView.findViewById(R.id.post_comments);
            likes = itemView.findViewById(R.id.post_likes);
            likeIcon = itemView.findViewById(R.id.post_like_icon);
            avatar = itemView.findViewById(R.id.post_avatar);
            imagesContainer = itemView.findViewById(R.id.post_images_container);
        }
    }
}

