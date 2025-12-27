package com.example.lnforum.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lnforum.R;
import com.example.lnforum.model.WSecondHandItem;
import com.example.lnforum.utils.ImageLoader;

import java.util.List;

public class WSecondHandAdapter extends RecyclerView.Adapter<WSecondHandAdapter.ViewHolder> {

    public interface OnItemClickListener {
        void onOpen(WSecondHandItem item);
    }

    private final List<WSecondHandItem> data;
    private final OnItemClickListener listener;
    private final Context context;

    public WSecondHandAdapter(Context context, List<WSecondHandItem> data, OnItemClickListener listener) {
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
        WSecondHandItem item = data.get(position);
        holder.title.setText(item.getTitle());
        
        // 显示描述内容，如果太长则截取前100个字符
        String desc = item.getDesc();
        if (desc != null && !desc.isEmpty()) {
            if (desc.length() > 100) {
                desc = desc.substring(0, 100) + "...";
            }
            holder.desc.setText(desc);
            holder.desc.setVisibility(android.view.View.VISIBLE);
        } else {
            holder.desc.setVisibility(android.view.View.GONE);
        }
        
        holder.price.setText("￥" + item.getPrice());
        holder.seller.setText(item.getSeller());
        holder.views.setText(item.getViews() + "浏览");
        
        // 加载头像（使用ImageLoader工具类，参考圈子动态的实现）
        String avatarUrl = item.getSellerAvatar();
        android.util.Log.d("WSecondHandAdapter", "加载头像: seller=" + item.getSeller() + ", avatarUrl=" + avatarUrl);
        ImageLoader.loadAvatar(avatarUrl, holder.avatar);
        
        // 加载商品图片（使用ImageLoader工具类）
        if (item.getImages() != null && !item.getImages().isEmpty()) {
            holder.imagesContainer.setVisibility(android.view.View.VISIBLE);
            holder.imagesContainer.removeAllViews();
            for (String imageUrl : item.getImages()) {
                if (imageUrl != null && !imageUrl.isEmpty()) {
                    ImageView imageView = new ImageView(context);
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        android.view.ViewGroup.LayoutParams.MATCH_PARENT,
                        android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
                    params.setMargins(0, 4, 0, 4);
                    imageView.setLayoutParams(params);
                    imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                    imageView.setAdjustViewBounds(true);
                    imageView.setMaxHeight(400);
                    ImageLoader.loadImageWithMaxHeight(imageUrl, imageView, 400);
                    holder.imagesContainer.addView(imageView);
                }
            }
        } else {
            holder.imagesContainer.setVisibility(android.view.View.GONE);
        }

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onOpen(item);
            }
        });
    }

    @Override
    public int getItemCount() {
        return data == null ? 0 : data.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView title, desc, price, seller, views;
        ImageView avatar;
        LinearLayout imagesContainer;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.item_title);
            desc = itemView.findViewById(R.id.item_desc);
            price = itemView.findViewById(R.id.item_price);
            seller = itemView.findViewById(R.id.item_seller);
            views = itemView.findViewById(R.id.item_views);
            avatar = itemView.findViewById(R.id.item_thumb);
            imagesContainer = itemView.findViewById(R.id.item_images_container);
        }
    }
}

