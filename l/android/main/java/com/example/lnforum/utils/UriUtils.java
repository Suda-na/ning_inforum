package com.example.lnforum.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;
import android.webkit.MimeTypeMap;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

public class UriUtils {

    /**
     * 将 Uri 内容复制到 APP 私有缓存目录，防止 Android 10+ 无法直接读取文件路径的问题
     */
    public static File getFileFromUri(Context context, Uri uri) {
        if (uri == null) return null;

        try {
            ContentResolver resolver = context.getContentResolver();

            // 1. 获取正确的文件扩展名 (如 png, jpg, gif)
            String extension = getFileExtension(context, uri);
            if (extension == null || extension.isEmpty()) {
                extension = "jpg"; // 默认兜底
            }

            // 2. 生成唯一文件名：temp_avatar_时间戳.jpg
            String fileName = "temp_avatar_" + System.currentTimeMillis() + "." + extension;

            // 3. 在缓存目录创建空文件
            File file = new File(context.getCacheDir(), fileName);

            // 4. 使用 try-with-resources 自动关闭流 (Java 7+支持，Android API 19+支持)
            // 如果你的 minSdk < 19，需要用 try-catch-finally 手动关闭
            try (InputStream inputStream = resolver.openInputStream(uri);
                 OutputStream outputStream = new FileOutputStream(file)) {

                if (inputStream == null) return null;

                byte[] buffer = new byte[4096]; // 4KB 缓冲区
                int length;
                while ((length = inputStream.read(buffer)) > 0) {
                    outputStream.write(buffer, 0, length);
                }
                outputStream.flush();
            }

            return file;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // 获取文件扩展名的辅助方法
    private static String getFileExtension(Context context, Uri uri) {
        ContentResolver contentResolver = context.getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        // 根据 Uri 获取 MIME 类型 (例如 image/png)
        String type = contentResolver.getType(uri);
        // 根据 MIME 类型获取后缀 (例如 png)
        return mimeTypeMap.getExtensionFromMimeType(type);
    }
}