package com.example.lnforum.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.ImageView;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 图片加载工具类
 * 统一处理网络图片和头像的加载
 */
public class ImageLoader {
    private static final String TAG = "ImageLoader";
    private static final String BASE_URL = "http://192.168.243.1:8080";
    
    // 线程池用于异步加载图片
    private static final ExecutorService executorService = Executors.newFixedThreadPool(5);
    
    // 默认占位图和错误图资源ID（如果需要可以扩展）
    private static final int DEFAULT_PLACEHOLDER = 0;
    private static final int DEFAULT_ERROR = 0;
    
    /**
     * 加载网络图片到ImageView
     * @param imageUrl 图片URL（可以是相对路径或完整URL）
     * @param imageView 目标ImageView
     */
    public static void loadImage(String imageUrl, ImageView imageView) {
        loadImage(imageUrl, imageView, false, 0, 0);
    }
    
    /**
     * 加载网络图片到ImageView（圆形头像）
     * @param imageUrl 图片URL（可以是相对路径或完整URL）
     * @param imageView 目标ImageView
     */
    public static void loadAvatar(String imageUrl, ImageView imageView) {
        Log.d(TAG, "loadAvatar调用: imageUrl=" + imageUrl + ", imageView=" + (imageView != null ? "not null" : "null"));
        loadImage(imageUrl, imageView, true, 0, 0);
    }
    
    /**
     * 加载网络图片到ImageView（带占位图和错误图）
     * @param imageUrl 图片URL（可以是相对路径或完整URL）
     * @param imageView 目标ImageView
     * @param placeholderResId 占位图资源ID（0表示不使用）
     * @param errorResId 错误图资源ID（0表示不使用）
     */
    public static void loadImage(String imageUrl, ImageView imageView, int placeholderResId, int errorResId) {
        loadImage(imageUrl, imageView, false, placeholderResId, errorResId);
    }
    
    /**
     * 加载网络图片到ImageView（圆形头像，带占位图和错误图）
     * @param imageUrl 图片URL（可以是相对路径或完整URL）
     * @param imageView 目标ImageView
     * @param placeholderResId 占位图资源ID（0表示不使用）
     * @param errorResId 错误图资源ID（0表示不使用）
     */
    public static void loadAvatar(String imageUrl, ImageView imageView, int placeholderResId, int errorResId) {
        loadImage(imageUrl, imageView, true, placeholderResId, errorResId);
    }
    
    /**
     * 加载网络图片的核心方法
     * @param imageUrl 图片URL
     * @param imageView 目标ImageView
     * @param isCircle 是否显示为圆形（头像）
     * @param placeholderResId 占位图资源ID
     * @param errorResId 错误图资源ID
     */
    private static void loadImage(String imageUrl, ImageView imageView, boolean isCircle, 
                                  int placeholderResId, int errorResId) {
        if (imageView == null) {
            Log.w(TAG, "ImageView is null, cannot load image");
            return;
        }
        
        if (imageUrl == null || imageUrl.isEmpty()) {
            Log.w(TAG, "Image URL is empty");
            if (errorResId != 0 && imageView.getContext() != null) {
                imageView.setImageResource(errorResId);
            }
            return;
        }
        
        // 设置占位图
        if (placeholderResId != 0 && imageView.getContext() != null) {
            imageView.setImageResource(placeholderResId);
        }
        
        // 处理URL：如果不是完整路径，添加BASE_URL前缀
        String finalImageUrl = imageUrl;
        if (!imageUrl.startsWith("http://") && !imageUrl.startsWith("https://")) {
            finalImageUrl = BASE_URL + (imageUrl.startsWith("/") ? imageUrl : "/" + imageUrl);
        }
        
        Log.d(TAG, "开始加载图片: 原始URL=" + imageUrl + ", 最终URL=" + finalImageUrl + ", isCircle=" + isCircle);
        
        // 创建final变量供lambda使用
        final String url = finalImageUrl;
        final ImageView targetView = imageView;
        
        // 使用线程池异步加载图片
        executorService.execute(() -> {
            try {
                Log.d(TAG, "线程池执行: 开始下载图片, URL=" + url);
                Bitmap bitmap = downloadBitmap(url);
                Log.d(TAG, "下载完成: URL=" + url + ", bitmap=" + (bitmap != null ? "not null" : "null"));
                
                if (bitmap != null) {
                    // 如果需要圆形头像，进行圆形处理
                    if (isCircle) {
                        bitmap = getCircleBitmap(bitmap);
                    }
                    
                    final Bitmap finalBitmap = bitmap;
                    // 在主线程更新UI
                    new Handler(Looper.getMainLooper()).post(() -> {
                        if (targetView != null) {
                            targetView.setImageBitmap(finalBitmap);
                        }
                    });
                } else {
                    // 加载失败，设置错误图
                    new Handler(Looper.getMainLooper()).post(() -> {
                        if (targetView != null && errorResId != 0 && targetView.getContext() != null) {
                            targetView.setImageResource(errorResId);
                        }
                    });
                }
            } catch (Exception e) {
                Log.e(TAG, "加载图片失败: " + url, e);
                // 加载失败，设置错误图
                new Handler(Looper.getMainLooper()).post(() -> {
                    if (targetView != null && errorResId != 0 && targetView.getContext() != null) {
                        targetView.setImageResource(errorResId);
                    }
                });
            }
        });
    }
    
    /**
     * 从网络下载图片
     * @param imageUrl 图片URL
     * @return Bitmap对象，失败返回null
     */
    private static Bitmap downloadBitmap(String imageUrl) {
        HttpURLConnection connection = null;
        InputStream input = null;
        
        try {
            Log.d(TAG, "downloadBitmap: 开始连接, URL=" + imageUrl);
            URL url = new URL(imageUrl);
            connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.setConnectTimeout(10000);
            connection.setReadTimeout(10000);
            connection.setRequestProperty("User-Agent", "Mozilla/5.0");
            Log.d(TAG, "downloadBitmap: 开始连接服务器");
            connection.connect();
            
            int responseCode = connection.getResponseCode();
            Log.d(TAG, "downloadBitmap: 响应码=" + responseCode + ", URL=" + imageUrl);
            if (responseCode == HttpURLConnection.HTTP_OK) {
                input = connection.getInputStream();
                Bitmap bitmap = BitmapFactory.decodeStream(input);
                Log.d(TAG, "downloadBitmap: 成功解码图片, bitmap=" + (bitmap != null ? "not null, size=" + bitmap.getWidth() + "x" + bitmap.getHeight() : "null"));
                return bitmap;
            } else {
                Log.e(TAG, "HTTP错误码: " + responseCode + ", URL: " + imageUrl);
                return null;
            }
        } catch (Exception e) {
            Log.e(TAG, "下载图片失败: " + imageUrl, e);
            return null;
        } finally {
            try {
                if (input != null) {
                    input.close();
                }
                if (connection != null) {
                    connection.disconnect();
                }
            } catch (Exception e) {
                Log.e(TAG, "关闭连接失败", e);
            }
        }
    }
    
    /**
     * 将Bitmap转换为圆形
     * @param bitmap 原始Bitmap
     * @return 圆形Bitmap
     */
    private static Bitmap getCircleBitmap(Bitmap bitmap) {
        if (bitmap == null) {
            return null;
        }
        
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int size = Math.min(width, height);
        
        // 创建圆形Bitmap
        Bitmap output = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
        
        // 绘制圆形
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        
        Rect rect = new Rect(0, 0, size, size);
        RectF rectF = new RectF(rect);
        
        canvas.drawOval(rectF, paint);
        
        // 使用PorterDuff模式实现圆形裁剪
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        
        // 计算裁剪区域（居中）
        int left = (width - size) / 2;
        int top = (height - size) / 2;
        Rect srcRect = new Rect(left, top, left + size, top + size);
        
        canvas.drawBitmap(bitmap, srcRect, rect, paint);
        
        return output;
    }
    
    /**
     * 加载图片到ImageView（支持最大高度限制）
     * @param imageUrl 图片URL
     * @param imageView 目标ImageView
     * @param maxHeight 最大高度（像素），0表示不限制
     */
    public static void loadImageWithMaxHeight(String imageUrl, ImageView imageView, int maxHeight) {
        if (imageView == null) {
            return;
        }
        
        if (imageUrl == null || imageUrl.isEmpty()) {
            return;
        }
        
        // 处理URL
        String finalImageUrl = imageUrl;
        if (!imageUrl.startsWith("http://") && !imageUrl.startsWith("https://")) {
            finalImageUrl = BASE_URL + (imageUrl.startsWith("/") ? imageUrl : "/" + imageUrl);
        }
        
        final String url = finalImageUrl;
        final ImageView targetView = imageView;
        
        executorService.execute(() -> {
            try {
                Bitmap bitmap = downloadBitmap(url);
                
                if (bitmap != null) {
                    // 如果设置了最大高度，进行缩放
                    if (maxHeight > 0 && bitmap.getHeight() > maxHeight) {
                        float scale = (float) maxHeight / bitmap.getHeight();
                        int newWidth = (int) (bitmap.getWidth() * scale);
                        bitmap = Bitmap.createScaledBitmap(bitmap, newWidth, maxHeight, true);
                    }
                    
                    final Bitmap finalBitmap = bitmap;
                    new Handler(Looper.getMainLooper()).post(() -> {
                        if (targetView != null) {
                            targetView.setImageBitmap(finalBitmap);
                            targetView.setAdjustViewBounds(true);
                            targetView.setMaxHeight(maxHeight > 0 ? maxHeight : Integer.MAX_VALUE);
                        }
                    });
                }
            } catch (Exception e) {
                Log.e(TAG, "加载图片失败: " + url, e);
            }
        });
    }
    
    /**
     * 取消所有正在进行的图片加载任务
     * 注意：这个方法会关闭线程池，谨慎使用
     */
    public static void shutdown() {
        executorService.shutdown();
    }
}

