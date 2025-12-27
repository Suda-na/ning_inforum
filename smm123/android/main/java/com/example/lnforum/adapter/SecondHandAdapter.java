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
import com.example.lnforum.model.SecondHandItem;

import java.util.List;

public class SecondHandAdapter extends RecyclerView.Adapter<SecondHandAdapter.ViewHolder> {

    public interface OnItemClickListener {
        void onOpen(SecondHandItem item);
    }

    private final List<SecondHandItem> data;
    private final OnItemClickListener listener;
    private final Context context;

    public SecondHandAdapter(Context context, List<SecondHandItem> data, OnItemClickListener listener) {
        this.context = context;
        this.data = data;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_second_hand, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        SecondHandItem item = data.get(position);
        holder.seller.setText(item.getSeller());
        holder.time.setText(item.getTime());
        holder.title.setText(item.getTitle());
        holder.desc.setText(item.getDesc());
        holder.tag.setText(item.getTag());
        holder.price.setText(item.getPrice());
        holder.views.setText(String.valueOf(item.getViews()));

        // 加载图片
        loadImages(holder, item.getImages());

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onOpen(item);
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
        TextView seller, time, title, desc, tag, price, views;
        ImageView thumb;
        LinearLayout imagesContainer;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            seller = itemView.findViewById(R.id.item_seller);
            time = itemView.findViewById(R.id.item_time);
            title = itemView.findViewById(R.id.item_title);
            desc = itemView.findViewById(R.id.item_desc);
            tag = itemView.findViewById(R.id.item_tag);
            price = itemView.findViewById(R.id.item_price);
            views = itemView.findViewById(R.id.item_views);
            thumb = itemView.findViewById(R.id.item_thumb);
            imagesContainer = itemView.findViewById(R.id.item_images_container);
        }
    }
}


