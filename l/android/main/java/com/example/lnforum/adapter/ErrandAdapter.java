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

public class ErrandAdapter extends RecyclerView.Adapter<ErrandAdapter.ViewHolder> {

    public interface OnItemClickListener {
        void onItemClick(ErrandOrder order);
    }

    private final List<ErrandOrder> data = new ArrayList<>();
    private final OnItemClickListener listener;

    public ErrandAdapter(OnItemClickListener listener) {
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

        // 状态样式
        holder.status.setText(getStatusText(order.getStatus()));
        int bgColor = ContextCompat.getColor(holder.itemView.getContext(), R.color.primary_blue);
        if ("waiting".equals(order.getStatus())) {
            bgColor = ContextCompat.getColor(holder.itemView.getContext(), R.color.primary_blue_light);
        } else if ("completed".equals(order.getStatus())) {
            bgColor = ContextCompat.getColor(holder.itemView.getContext(), R.color.primary_blue_dark);
        }
        holder.status.setBackgroundTintList(android.content.res.ColorStateList.valueOf(bgColor));

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

    private String getStatusText(String status) {
        switch (status) {
            case "waiting":
                return "待接单";
            case "delivering":
                return "配送中";
            case "completed":
            default:
                return "订单完成";
        }
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

