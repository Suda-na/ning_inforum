package com.example.lnforum.adapter;

import android.content.Context;
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
import com.example.lnforum.model.CirclePost;

import java.util.List;

public class CirclePostAdapter extends RecyclerView.Adapter<CirclePostAdapter.ViewHolder> {

    public interface OnPostActionListener {
        void onOpen(CirclePost post);
        void onLike(CirclePost post);
    }

    private final List<CirclePost> data;
    private final OnPostActionListener listener;
    private final Context context;

    public CirclePostAdapter(Context context, List<CirclePost> data, OnPostActionListener listener) {
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
        CirclePost post = data.get(position);
        holder.author.setText(post.getAuthor());
        holder.time.setText(post.getTime());
        holder.title.setText(post.getTitle());
        holder.content.setText(post.getContent());
        holder.tag.setText(post.getTag());
        holder.views.setText(String.valueOf(post.getViews()));
        holder.comments.setText(String.valueOf(post.getComments()));
        holder.likes.setText(String.valueOf(post.getLikes()));

        int likeColor = post.isLiked() ? ContextCompat.getColor(context, R.color.primary_blue)
                : ContextCompat.getColor(context, R.color.text_secondary);
        holder.likeIcon.setColorFilter(likeColor);
        holder.likes.setTextColor(likeColor);

        // 加载图片
        loadImages(holder, post.getImages());

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onOpen(post);
        });
        holder.likeIcon.setOnClickListener(v -> {
            if (listener != null) listener.onLike(post);
            notifyItemChanged(position);
        });
    }

    private void loadImages(ViewHolder holder, List<String> imageUrls) {
        holder.imagesContainer.removeAllViews();
        if (imageUrls == null || imageUrls.isEmpty()) {
            holder.imagesContainer.setVisibility(View.GONE);
            return;
        }
        holder.imagesContainer.setVisibility(View.VISIBLE);

        int maxImages = Math.min(imageUrls.size(), 3); // 最多显示3张
        int imageSize = dpToPx(80);
        int margin = dpToPx(4);

        for (int i = 0; i < maxImages; i++) {
            ImageView imageView = new ImageView(context);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(imageSize, imageSize);
            if (i > 0) {
                params.setMargins(margin, 0, 0, 0);
            }
            imageView.setLayoutParams(params);
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setBackgroundColor(ContextCompat.getColor(context, R.color.divider));
            // 这里后续可以接入图片加载库如Glide
            // Glide.with(context).load(imageUrls.get(i)).into(imageView);
            holder.imagesContainer.addView(imageView);
        }
    }

    private int dpToPx(int dp) {
        float density = context.getResources().getDisplayMetrics().density;
        return Math.round(dp * density);
    }

    @Override
    public int getItemCount() {
        return data == null ? 0 : data.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView author, time, title, content, tag, views, comments, likes;
        ImageView likeIcon;
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
            imagesContainer = itemView.findViewById(R.id.post_images_container);
        }
    }
}


