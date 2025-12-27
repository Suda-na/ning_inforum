package com.example.lnforum.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lnforum.R;
import com.example.lnforum.model.ErrandOrder;

import java.util.ArrayList;
import java.util.List;

/**
 * 我的订单列表适配器，复用跑腿卡片样式。
 * mode = "publish" 表示我的发布，状态按钮显示“取消”
 * mode = "accept" 表示我的接单，状态按钮显示“已完成”
 */
public class ProfileOrderAdapter extends RecyclerView.Adapter<ProfileOrderAdapter.ViewHolder> {

    public interface OnItemClickListener {
        void onItemClick(ErrandOrder order);
    }

    private final List<ErrandOrder> data = new ArrayList<>();
    private final OnItemClickListener listener;
    private final String mode; // "publish" or "accept"

    public ProfileOrderAdapter(String mode, OnItemClickListener listener) {
        this.mode = mode;
        this.listener = listener;
    }

    public void setData(List<ErrandOrder> list) {
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
                .inflate(R.layout.item_errand, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ErrandOrder order = data.get(position);
        holder.title.setText(order.getTitle());
        holder.desc.setText(order.getDesc());
        holder.from.setText(order.getFrom());
        holder.to.setText(order.getTo());
        holder.price.setText("￥" + order.getPrice());

        // 根据模式设置状态按钮文本和颜色
        if ("accept".equals(mode)) {
            holder.status.setText("已完成");
            int color = ContextCompat.getColor(holder.itemView.getContext(), R.color.primary_blue_dark);
            holder.status.setBackgroundTintList(android.content.res.ColorStateList.valueOf(color));
        } else {
            holder.status.setText("取消");
            int color = ContextCompat.getColor(holder.itemView.getContext(), R.color.text_secondary);
            holder.status.setBackgroundTintList(android.content.res.ColorStateList.valueOf(color));
        }

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(order);
            }
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView title, desc, from, to, price, status;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.item_title);
            desc = itemView.findViewById(R.id.item_desc);
            from = itemView.findViewById(R.id.item_from);
            to = itemView.findViewById(R.id.item_to);
            price = itemView.findViewById(R.id.item_price);
            status = itemView.findViewById(R.id.item_status);
        }
    }
}







