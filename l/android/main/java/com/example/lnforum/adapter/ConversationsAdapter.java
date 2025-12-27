package com.example.lnforum.adapter;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lnforum.R;
import com.example.lnforum.activity.UserProfileActivity;
import com.example.lnforum.model.Conversation;
import com.example.lnforum.repository.MessageRepository;

import java.util.List;

/**
 * 会话列表适配器。
 */
public class ConversationsAdapter extends RecyclerView.Adapter<ConversationsAdapter.ViewHolder> {

    public interface OnConversationClickListener {
        void onConversationClick(Conversation conversation);
    }

    private final List<Conversation> data;
    private final OnConversationClickListener listener;

    public ConversationsAdapter(List<Conversation> data, OnConversationClickListener listener) {
        this.data = data;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_conversation, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Conversation conversation = data.get(position);
        holder.name.setText(conversation.getTitle());
        holder.preview.setText(conversation.getLastMessage());
        holder.time.setText(MessageRepository.formatTime(conversation.getTimestamp()));
        
        // 显示每个会话的未读数红点（在头像右上角）
        int unreadCount = conversation.getUnreadCount();
        if (holder.badge != null) {
            if (unreadCount > 0) {
                holder.badge.setVisibility(View.VISIBLE);
                if (unreadCount > 99) {
                    holder.badge.setText("99+");
                } else {
                    holder.badge.setText(String.valueOf(unreadCount));
                }
            } else {
                holder.badge.setVisibility(View.GONE);
            }
        }
        
        // 加载头像（如果有头像URL则加载，否则使用默认图标）
        String avatarUrl = conversation.getAvatar();
        if (avatarUrl != null && !avatarUrl.isEmpty()) {
            // 移除tint，让真实头像显示
            holder.avatar.setImageTintList(null);
            com.example.lnforum.utils.ImageLoader.loadAvatar(avatarUrl, holder.avatar);
        } else {
            // 统一使用 ic_message 图标作为默认头像
            holder.avatar.setImageResource(R.drawable.ic_message);
            // 设置tint颜色
            holder.avatar.setImageTintList(androidx.core.content.ContextCompat.getColorStateList(
                holder.itemView.getContext(), R.color.primary_blue));
        }

        // 头像点击查看用户主页（管理员除外）
        holder.avatar.setOnClickListener(v -> {
            String userName = conversation.getTitle();
            String type = conversation.getType();
            // 只有非管理员类型才跳转到用户主页
            if (userName != null && !"admin".equals(type)) {
                Intent intent = new Intent(holder.itemView.getContext(), UserProfileActivity.class);
                intent.putExtra(UserProfileActivity.EXTRA_USER_NAME, userName);
                holder.itemView.getContext().startActivity(intent);
            }
        });

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onConversationClick(conversation);
            }
        });
    }

    @Override
    public int getItemCount() {
        return data == null ? 0 : data.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView avatar;
        TextView name;
        TextView preview;
        TextView time;
        TextView badge;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            avatar = itemView.findViewById(R.id.conversation_avatar);
            name = itemView.findViewById(R.id.conversation_name);
            preview = itemView.findViewById(R.id.conversation_preview);
            time = itemView.findViewById(R.id.conversation_time);
            badge = itemView.findViewById(R.id.conversation_badge);
        }
    }
}


