package com.example.lnforum.adapter;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.lnforum.R;
import com.example.lnforum.model.ChatMessage;
import com.example.lnforum.model.CUser;
import com.example.lnforum.repository.CSessionManager;

import java.util.List;

/**
 * 聊天消息适配器。
 */
public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_ME = 1;
    private static final int TYPE_OTHER = 2;

    private final List<ChatMessage> data;
    private String otherUserAvatar; // 对方用户头像

    public ChatAdapter(List<ChatMessage> data) {
        this.data = data;
    }
    
    public void setOtherUserAvatar(String avatarUrl) {
        this.otherUserAvatar = avatarUrl;
    }

    @Override
    public int getItemViewType(int position) {
        return data.get(position).isFromMe() ? TYPE_ME : TYPE_OTHER;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == TYPE_ME) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_chat_message_right, parent, false);
            return new RightHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_chat_message_left, parent, false);
            return new LeftHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ChatMessage message = data.get(position);
        boolean isImage = message.getMsgFormat() != null && message.getMsgFormat() == 1;
        String imageUrl = message.getImageUrl();
        
        if (holder instanceof LeftHolder) {
            LeftHolder leftHolder = (LeftHolder) holder;
            // 显示文本或图片
            if (isImage && !TextUtils.isEmpty(imageUrl)) {
                leftHolder.content.setVisibility(View.GONE);
                leftHolder.image.setVisibility(View.VISIBLE);
                
                // 参考动态的图片加载方式：使用ImageLoader，maxHeight=400
                leftHolder.image.setMaxHeight(dpToPx(400));
                com.example.lnforum.utils.ImageLoader.loadImageWithMaxHeight(
                    imageUrl, 
                    leftHolder.image, 
                    dpToPx(400)
                );
            } else {
                leftHolder.content.setVisibility(View.VISIBLE);
                leftHolder.image.setVisibility(View.GONE);
                String content = message.getContent();
                if (TextUtils.isEmpty(content)) {
                    content = "[图片]";
                }
                leftHolder.content.setText(content);
            }
            
            // 参考主页的头像加载方式：使用Glide.circleCrop()
            if (!TextUtils.isEmpty(otherUserAvatar)) {
                Glide.with(leftHolder.itemView.getContext())
                    .load(otherUserAvatar)
                    .placeholder(R.drawable.ic_user_avatar)
                    .error(R.drawable.ic_user_avatar)
                    .circleCrop()
                    .into(leftHolder.avatar);
            } else {
                // 如果没有头像URL，使用默认头像
                leftHolder.avatar.setImageResource(R.drawable.ic_user_avatar);
            }
        } else if (holder instanceof RightHolder) {
            RightHolder rightHolder = (RightHolder) holder;
            // 显示文本或图片
            if (isImage && !TextUtils.isEmpty(imageUrl)) {
                rightHolder.content.setVisibility(View.GONE);
                rightHolder.image.setVisibility(View.VISIBLE);
                
                // 参考动态的图片加载方式：使用ImageLoader，maxHeight=400
                rightHolder.image.setMaxHeight(dpToPx(400));
                com.example.lnforum.utils.ImageLoader.loadImageWithMaxHeight(
                    imageUrl, 
                    rightHolder.image, 
                    dpToPx(400)
                );
            } else {
                rightHolder.content.setVisibility(View.VISIBLE);
                rightHolder.image.setVisibility(View.GONE);
                rightHolder.content.setText(message.getContent());
            }
            
            // 参考主页的头像加载方式：使用Glide.circleCrop()
            CUser currentUser = CSessionManager.getInstance(rightHolder.itemView.getContext()).getCurrentCUser();
            if (currentUser != null && currentUser.getAvatar() != null && !currentUser.getAvatar().isEmpty()) {
                Glide.with(rightHolder.itemView.getContext())
                    .load(currentUser.getAvatar())
                    .placeholder(R.drawable.ic_user_avatar)
                    .error(R.drawable.ic_user_avatar)
                    .circleCrop()
                    .into(rightHolder.avatar);
            } else {
                // 如果没有头像URL，使用默认头像
                rightHolder.avatar.setImageResource(R.drawable.ic_user_avatar);
            }
        }
    }

    @Override
    public int getItemCount() {
        return data == null ? 0 : data.size();
    }
    
    /**
     * dp转px
     */
    private int dpToPx(int dp) {
        float density = android.content.res.Resources.getSystem().getDisplayMetrics().density;
        return Math.round(dp * density);
    }

    static class LeftHolder extends RecyclerView.ViewHolder {
        TextView content;
        ImageView image;
        ImageView avatar;
        LeftHolder(@NonNull View itemView) {
            super(itemView);
            content = itemView.findViewById(R.id.chat_left_text);
            image = itemView.findViewById(R.id.chat_left_image);
            avatar = itemView.findViewById(R.id.chat_left_avatar);
        }
    }

    static class RightHolder extends RecyclerView.ViewHolder {
        TextView content;
        ImageView image;
        ImageView avatar;
        RightHolder(@NonNull View itemView) {
            super(itemView);
            content = itemView.findViewById(R.id.chat_right_text);
            image = itemView.findViewById(R.id.chat_right_image);
            avatar = itemView.findViewById(R.id.chat_right_avatar);
        }
    }
}


