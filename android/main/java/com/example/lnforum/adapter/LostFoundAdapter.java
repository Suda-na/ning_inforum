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
import com.example.lnforum.model.LostFoundItem;

import java.util.ArrayList;
import java.util.List;

public class LostFoundAdapter extends RecyclerView.Adapter<LostFoundAdapter.ViewHolder> {

    public interface OnItemClickListener {
        void onOpen(LostFoundItem item);
    }

    private final Context context;
    private final OnItemClickListener listener;
    private final List<LostFoundItem> data = new ArrayList<>();

    public LostFoundAdapter(Context context, List<LostFoundItem> list, OnItemClickListener listener) {
        this.context = context;
        this.listener = listener;
        if (list != null) {
            data.addAll(list);
        }
    }

    public void setData(List<LostFoundItem> list) {
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
        LostFoundItem item = data.get(position);
        holder.user.setText(item.getUser());
        holder.time.setText(item.getTime());
        holder.title.setText(item.getTitle());
        holder.desc.setText(item.getDesc());
        holder.tag.setText(item.getTag());
        holder.location.setText(item.getLocation());
        holder.views.setText(String.valueOf(item.getViews()));

        int tagColor = "招领".equals(item.getTag())
                ? context.getColor(R.color.primary_blue)
                : context.getColor(R.color.text_secondary);
        holder.tag.setTextColor(tagColor);

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
        return data.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView user, time, title, desc, tag, location, views;
        ImageView avatar;
        LinearLayout imagesContainer;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            user = itemView.findViewById(R.id.item_user);
            time = itemView.findViewById(R.id.item_time);
            title = itemView.findViewById(R.id.item_title);
            desc = itemView.findViewById(R.id.item_desc);
            tag = itemView.findViewById(R.id.item_tag);
            location = itemView.findViewById(R.id.item_location);
            views = itemView.findViewById(R.id.item_views);
            avatar = itemView.findViewById(R.id.item_avatar);
            imagesContainer = itemView.findViewById(R.id.item_images_container);
        }
    }
}


