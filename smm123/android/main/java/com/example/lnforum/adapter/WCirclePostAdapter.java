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
        
        // 加载帖子图片（最多3张，网格布局）
        List<String> images = post.getImages();
        if (images != null && !images.isEmpty()) {
            holder.imagesContainer.setVisibility(android.view.View.VISIBLE);
            holder.imagesContainer.removeAllViews();
            
            int imageCount = Math.min(images.size(), 3); // 最多3张
            int screenWidth = context.getResources().getDisplayMetrics().widthPixels;
            int padding = (int) (12 * context.getResources().getDisplayMetrics().density); // 12dp转px
            int availableWidth = screenWidth - padding * 2;
            
            if (imageCount == 1) {
                // 单张图片：全宽显示
                String imageUrl = images.get(0);
                if (imageUrl != null && !imageUrl.isEmpty()) {
                    android.widget.ImageView imageView = new android.widget.ImageView(context);
                    android.widget.LinearLayout.LayoutParams params = new android.widget.LinearLayout.LayoutParams(
                        android.view.ViewGroup.LayoutParams.MATCH_PARENT,
                        android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
                    params.setMargins(0, 8, 0, 0);
                    imageView.setLayoutParams(params);
                    imageView.setScaleType(android.widget.ImageView.ScaleType.CENTER_CROP);
                    imageView.setAdjustViewBounds(true);
                    imageView.setMaxHeight(600);
                    com.example.lnforum.utils.ImageLoader.loadImageWithMaxHeight(imageUrl, imageView, 600);
                    holder.imagesContainer.addView(imageView);
                }
            } else if (imageCount == 2) {
                // 两张图片：并排显示，各占一半
                android.widget.LinearLayout rowLayout = new android.widget.LinearLayout(context);
                rowLayout.setOrientation(android.widget.LinearLayout.HORIZONTAL);
                android.widget.LinearLayout.LayoutParams rowParams = new android.widget.LinearLayout.LayoutParams(
                    android.view.ViewGroup.LayoutParams.MATCH_PARENT,
                    android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
                rowParams.setMargins(0, 8, 0, 0);
                rowLayout.setLayoutParams(rowParams);
                
                int imageWidth = (availableWidth - 8) / 2; // 减去间距
                for (int i = 0; i < 2; i++) {
                    String imageUrl = images.get(i);
                    if (imageUrl != null && !imageUrl.isEmpty()) {
                        android.widget.ImageView imageView = new android.widget.ImageView(context);
                        android.widget.LinearLayout.LayoutParams params = new android.widget.LinearLayout.LayoutParams(
                            imageWidth,
                            imageWidth);
                        if (i == 0) {
                            params.setMargins(0, 0, 8, 0);
                        }
                        imageView.setLayoutParams(params);
                        imageView.setScaleType(android.widget.ImageView.ScaleType.CENTER_CROP);
                        com.example.lnforum.utils.ImageLoader.loadImage(imageUrl, imageView);
                        rowLayout.addView(imageView);
                    }
                }
                holder.imagesContainer.addView(rowLayout);
            } else {
                // 三张图片：第一张全宽，下面两张并排
                // 第一张
                String imageUrl1 = images.get(0);
                if (imageUrl1 != null && !imageUrl1.isEmpty()) {
                    android.widget.ImageView imageView1 = new android.widget.ImageView(context);
                    android.widget.LinearLayout.LayoutParams params1 = new android.widget.LinearLayout.LayoutParams(
                        android.view.ViewGroup.LayoutParams.MATCH_PARENT,
                        android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
                    params1.setMargins(0, 8, 0, 8);
                    imageView1.setLayoutParams(params1);
                    imageView1.setScaleType(android.widget.ImageView.ScaleType.CENTER_CROP);
                    imageView1.setAdjustViewBounds(true);
                    imageView1.setMaxHeight(400);
                    com.example.lnforum.utils.ImageLoader.loadImageWithMaxHeight(imageUrl1, imageView1, 400);
                    holder.imagesContainer.addView(imageView1);
                }
                
                // 下面两张并排
                android.widget.LinearLayout rowLayout = new android.widget.LinearLayout(context);
                rowLayout.setOrientation(android.widget.LinearLayout.HORIZONTAL);
                android.widget.LinearLayout.LayoutParams rowParams = new android.widget.LinearLayout.LayoutParams(
                    android.view.ViewGroup.LayoutParams.MATCH_PARENT,
                    android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
                rowLayout.setLayoutParams(rowParams);
                
                int imageWidth = (availableWidth - 8) / 2;
                for (int i = 1; i < 3; i++) {
                    String imageUrl = images.get(i);
                    if (imageUrl != null && !imageUrl.isEmpty()) {
                        android.widget.ImageView imageView = new android.widget.ImageView(context);
                        android.widget.LinearLayout.LayoutParams params = new android.widget.LinearLayout.LayoutParams(
                            imageWidth,
                            imageWidth);
                        if (i == 1) {
                            params.setMargins(0, 0, 8, 0);
                        }
                        imageView.setLayoutParams(params);
                        imageView.setScaleType(android.widget.ImageView.ScaleType.CENTER_CROP);
                        com.example.lnforum.utils.ImageLoader.loadImage(imageUrl, imageView);
                        rowLayout.addView(imageView);
                    }
                }
                holder.imagesContainer.addView(rowLayout);
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

