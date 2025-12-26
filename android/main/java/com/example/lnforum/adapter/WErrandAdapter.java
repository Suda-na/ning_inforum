package com.example.lnforum.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lnforum.R;
import com.example.lnforum.model.WErrandOrder;

import java.util.ArrayList;
import java.util.List;

public class WErrandAdapter extends RecyclerView.Adapter<WErrandAdapter.ViewHolder> {

    public interface OnItemClickListener {
        void onOpen(WErrandOrder order);
    }

    private final List<WErrandOrder> data;
    private final OnItemClickListener listener;
    private final Context context;

    public WErrandAdapter(Context context, List<WErrandOrder> data, OnItemClickListener listener) {
        this.context = context;
        this.data = data;  // 直接引用，不复制
        this.listener = listener;
    }

    public void setData(List<WErrandOrder> list) {
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
        WErrandOrder order = data.get(position);
        holder.title.setText(order.getTitle());
        holder.desc.setText(order.getDesc());
        holder.from.setText(order.getFrom());
        holder.to.setText(order.getTo());
        holder.price.setText("￥" + order.getPrice());
        holder.status.setText(order.getStatus());

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onOpen(order);
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

