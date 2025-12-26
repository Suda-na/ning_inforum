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
import com.example.android_java2.model.ChatMessage;
import com.example.android_java2.model.CUser;
import com.example.android_java2.repository.CSessionManager;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 聊天消息适配器。
 */
public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_ME = 1;
    private static final int TYPE_OTHER = 2;

    private final List<ChatMessage> data;
    private String otherUserAvatar; // 对方用户头像URL

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
        if (holder instanceof LeftHolder) {
            LeftHolder leftHolder = (LeftHolder) holder;
            if (message.isImage()) {
                // 显示图片
                leftHolder.content.setVisibility(View.GONE);
                leftHolder.imageView.setVisibility(View.VISIBLE);
                loadImage(leftHolder.imageView, message.getImageUrl());
            } else {
                // 显示文本
                leftHolder.content.setVisibility(View.VISIBLE);
                leftHolder.imageView.setVisibility(View.GONE);
                String content = message.getContent();
                leftHolder.content.setText(TextUtils.isEmpty(content) ? "" : content);
            }
            // 先设置默认头像
            leftHolder.avatar.setImageResource(R.drawable.ic_user_avatar);
            // 加载对方用户头像
            loadAvatar(leftHolder.avatar, otherUserAvatar);
        } else if (holder instanceof RightHolder) {
            RightHolder rightHolder = (RightHolder) holder;
            if (message.isImage()) {
                // 显示图片
                rightHolder.content.setVisibility(View.GONE);
                rightHolder.imageView.setVisibility(View.VISIBLE);
                loadImage(rightHolder.imageView, message.getImageUrl());
            } else {
                // 显示文本
                rightHolder.content.setVisibility(View.VISIBLE);
                rightHolder.imageView.setVisibility(View.GONE);
                String content = message.getContent();
                rightHolder.content.setText(TextUtils.isEmpty(content) ? "" : content);
            }
            // 先设置默认头像
            rightHolder.avatar.setImageResource(R.drawable.ic_user_avatar);
            // 加载当前用户头像
            CUser currentUser = CSessionManager.getInstance(rightHolder.itemView.getContext()).getCurrentCUser();
            String avatarUrl = currentUser != null ? currentUser.getAvatar() : null;
            loadAvatar(rightHolder.avatar, avatarUrl);
        }
    }
    
    private void loadImage(ImageView imageView, String imageUrl) {
        if (TextUtils.isEmpty(imageUrl)) {
            imageView.setVisibility(View.GONE);
            return;
        }
        
        // 先显示占位符
        imageView.setImageResource(R.drawable.ic_image);
        
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            Bitmap bitmap = null;
            InputStream input = null;
            HttpURLConnection connection = null;
            try {
                URL url = new URL(imageUrl);
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
                
                // 计算缩放比例（目标尺寸200dp，约600px）
                int targetSize = 600;
                int scale = 1;
                if (options.outHeight > targetSize || options.outWidth > targetSize) {
                    int heightRatio = Math.round((float) options.outHeight / targetSize);
                    int widthRatio = Math.round((float) options.outWidth / targetSize);
                    scale = Math.max(heightRatio, widthRatio);
                }
                
                // 限制最大缩放比例，避免内存问题
                if (scale < 1) scale = 1;
                if (scale > 8) scale = 8; // 最多缩小8倍
                
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
                    // 检查bitmap是否太大，如果太大则进一步缩放
                    int maxSize = 800; // 最大800px
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
                    imageView.post(() -> {
                        imageView.setImageBitmap(finalBitmap);
                    });
                } else {
                    imageView.post(() -> {
                        imageView.setImageResource(R.drawable.ic_image);
                    });
                }
            } catch (OutOfMemoryError e) {
                android.util.Log.e("ChatAdapter", "OutOfMemoryError loading image: " + imageUrl, e);
                if (bitmap != null) {
                    bitmap.recycle();
                }
                imageView.post(() -> {
                    imageView.setImageResource(R.drawable.ic_image);
                });
            } catch (Exception e) {
                android.util.Log.e("ChatAdapter", "Error loading image: " + imageUrl, e);
                if (bitmap != null) {
                    bitmap.recycle();
                }
                imageView.post(() -> {
                    // 加载失败，显示占位符
                    imageView.setImageResource(R.drawable.ic_image);
                });
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
                
                // 计算缩放比例（头像目标尺寸约84px，28dp * 3）
                int targetSize = 84;
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
                    // 头像不需要太大，确保不超过目标尺寸
                    int maxSize = 120;
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
                android.util.Log.e("ChatAdapter", "OutOfMemoryError loading avatar: " + avatarUrl, e);
                if (bitmap != null) {
                    bitmap.recycle();
                }
            } catch (Exception e) {
                android.util.Log.e("ChatAdapter", "Error loading avatar: " + avatarUrl, e);
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

    static class LeftHolder extends RecyclerView.ViewHolder {
        ImageView avatar;
        TextView content;
        ImageView imageView;
        LeftHolder(@NonNull View itemView) {
            super(itemView);
            avatar = itemView.findViewById(R.id.chat_left_avatar);
            content = itemView.findViewById(R.id.chat_left_text);
            imageView = itemView.findViewById(R.id.chat_left_image);
        }
    }

    static class RightHolder extends RecyclerView.ViewHolder {
        ImageView avatar;
        TextView content;
        ImageView imageView;
        RightHolder(@NonNull View itemView) {
            super(itemView);
            avatar = itemView.findViewById(R.id.chat_right_avatar);
            content = itemView.findViewById(R.id.chat_right_text);
            imageView = itemView.findViewById(R.id.chat_right_image);
        }
    }
}


