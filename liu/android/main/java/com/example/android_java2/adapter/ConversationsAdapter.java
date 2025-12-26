package com.example.android_java2.adapter;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.android_java2.R;
import com.example.android_java2.model.Conversation;
import com.example.android_java2.repository.LMessageRepository;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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
        holder.time.setText(LMessageRepository.formatTime(conversation.getTimestamp()));
        
        // 先设置默认头像
        holder.avatar.setImageResource(R.drawable.ic_user_avatar);
        
        // 加载用户头像
        String avatarUrl = conversation.getAvatar();
        if (!TextUtils.isEmpty(avatarUrl)) {
            loadAvatar(holder.avatar, avatarUrl);
        }

        // 显示未读消息数
        int unreadCount = conversation.getUnreadCount();
        if (unreadCount > 0) {
            holder.unreadBadge.setVisibility(View.VISIBLE);
            if (unreadCount > 99) {
                holder.unreadBadge.setText("99+");
            } else {
                holder.unreadBadge.setText(String.valueOf(unreadCount));
            }
        } else {
            holder.unreadBadge.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onConversationClick(conversation);
            }
        });
    }
    
    private void loadAvatar(ImageView avatarView, String avatarUrl) {
        if (TextUtils.isEmpty(avatarUrl)) {
            return; // 已经设置了默认头像，不需要再次设置
        }
        
        // 使用tag来避免重复加载和错误的图片设置
        String currentTag = (String) avatarView.getTag();
        if (avatarUrl.equals(currentTag)) {
            // 已经在加载或已加载这个头像
            return;
        }
        avatarView.setTag(avatarUrl);
        
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            Bitmap bitmap = null;
            InputStream input = null;
            HttpURLConnection connection = null;
            try {
                URL url = new URL(avatarUrl);
                connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.setConnectTimeout(10000);
                connection.setReadTimeout(10000);
                connection.connect();
                input = connection.getInputStream();
                
                // 先获取图片尺寸，不加载完整图片
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inJustDecodeBounds = true;
                BitmapFactory.decodeStream(input, null, options);
                input.close();
                connection.disconnect();
                
                // 检查图片尺寸是否合理
                if (options.outWidth <= 0 || options.outHeight <= 0) {
                    throw new Exception("Invalid image dimensions");
                }
                
                // 计算缩放比例（头像目标尺寸约120px，40dp * 3）
                int targetSize = 120;
                int scale = 1;
                if (options.outHeight > targetSize || options.outWidth > targetSize) {
                    int heightRatio = Math.round((float) options.outHeight / targetSize);
                    int widthRatio = Math.round((float) options.outWidth / targetSize);
                    scale = Math.max(heightRatio, widthRatio);
                }
                
                // 限制最大缩放比例
                if (scale < 1) scale = 1;
                if (scale > 8) scale = 8;
                
                // 重新连接并加载缩放后的图片
                connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.setConnectTimeout(10000);
                connection.setReadTimeout(10000);
                connection.connect();
                input = connection.getInputStream();
                
                options.inJustDecodeBounds = false;
                options.inSampleSize = scale;
                options.inPreferredConfig = Bitmap.Config.RGB_565; // 使用更节省内存的格式
                options.inDither = false;
                options.inPurgeable = true;
                options.inInputShareable = true;
                
                bitmap = BitmapFactory.decodeStream(input, null, options);
                
                if (bitmap != null) {
                    // 确保头像不超过目标尺寸
                    int maxSize = 150;
                    if (bitmap.getWidth() > maxSize || bitmap.getHeight() > maxSize) {
                        float scaleFactor = Math.min((float) maxSize / bitmap.getWidth(), 
                                                     (float) maxSize / bitmap.getHeight());
                        int newWidth = Math.round(bitmap.getWidth() * scaleFactor);
                        int newHeight = Math.round(bitmap.getHeight() * scaleFactor);
                        Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true);
                        if (scaledBitmap != bitmap) {
                            bitmap.recycle();
                            bitmap = scaledBitmap;
                        }
                    }
                    
                    final Bitmap finalBitmap = bitmap;
                    avatarView.post(() -> {
                        // 检查tag是否还是这个URL，避免设置错误的图片
                        if (avatarUrl.equals(avatarView.getTag())) {
                            avatarView.setImageBitmap(finalBitmap);
                        } else if (finalBitmap != null) {
                            finalBitmap.recycle();
                        }
                    });
                }
            } catch (OutOfMemoryError e) {
                android.util.Log.e("ConversationsAdapter", "OutOfMemoryError loading avatar: " + avatarUrl, e);
                if (bitmap != null) {
                    bitmap.recycle();
                }
            } catch (Exception e) {
                android.util.Log.e("ConversationsAdapter", "Error loading avatar: " + avatarUrl, e);
                if (bitmap != null) {
                    bitmap.recycle();
                }
            } finally {
                try {
                    if (input != null) input.close();
                    if (connection != null) connection.disconnect();
                } catch (Exception e) {
                    e.printStackTrace();
                }
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
        TextView unreadBadge;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            avatar = itemView.findViewById(R.id.conversation_avatar);
            name = itemView.findViewById(R.id.conversation_name);
            preview = itemView.findViewById(R.id.conversation_preview);
            time = itemView.findViewById(R.id.conversation_time);
            unreadBadge = itemView.findViewById(R.id.conversation_unread_badge);
        }
    }
}


