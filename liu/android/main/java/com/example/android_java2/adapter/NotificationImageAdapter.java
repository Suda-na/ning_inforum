package com.example.android_java2.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.android_java2.R;

import java.util.List;

/**
 * 通知弹窗中的图片适配器
 */
public class NotificationImageAdapter extends RecyclerView.Adapter<NotificationImageAdapter.ImageViewHolder> {

    private List<String> imageUrls;

    public NotificationImageAdapter(List<String> imageUrls) {
        this.imageUrls = imageUrls;
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_notification_image, parent, false);
        return new ImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
        String imageUrl = imageUrls.get(position);
        
        // 这里后续可以接入图片加载库如Glide
        // Glide.with(holder.itemView.getContext()).load(imageUrl).into(holder.imageView);
        
        // 暂时使用占位背景色
        holder.imageView.setBackgroundColor(
            holder.itemView.getContext().getResources().getColor(R.color.divider)
        );
    }

    @Override
    public int getItemCount() {
        return imageUrls == null ? 0 : imageUrls.size();
    }

    static class ImageViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;

        ImageViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.image_view);
        }
    }
}

