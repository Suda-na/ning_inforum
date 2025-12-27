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
        
        // 加载头像
        loadImage(item.getSellerAvatar(), holder.avatar);
        
        // 加载商品图片
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
                    loadImage(imageUrl, imageView);
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
    
    /**
     * 加载网络图片
     */
    private void loadImage(String imageUrl, ImageView imageView) {
        if (imageUrl == null || imageUrl.isEmpty()) {
            return;
        }
        
        new Thread(() -> {
            try {
                java.net.URL url = new java.net.URL(imageUrl);
                java.net.HttpURLConnection connection = (java.net.HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.setConnectTimeout(5000);
                connection.setReadTimeout(5000);
                connection.connect();
                java.io.InputStream input = connection.getInputStream();
                android.graphics.Bitmap bitmap = android.graphics.BitmapFactory.decodeStream(input);
                input.close();
                
                android.os.Handler mainHandler = new android.os.Handler(android.os.Looper.getMainLooper());
                mainHandler.post(() -> {
                    imageView.setImageBitmap(bitmap);
                });
            } catch (Exception e) {
                android.util.Log.e("WSecondHandAdapter", "加载图片失败: " + imageUrl, e);
            }
        }).start();
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

