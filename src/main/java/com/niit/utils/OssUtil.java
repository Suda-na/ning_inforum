package com.niit.utils;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.model.PutObjectRequest;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

/**
 * 阿里云OSS工具类
 */
public class OssUtil {
    
    // OSS配置信息
    private static final String ENDPOINT = "oss-cn-wulanchabu.aliyuncs.com";
    private static final String ACCESS_KEY_ID = "LTAI5tMbUHZ1SwHRj6FdAcVa";
    private static final String ACCESS_KEY_SECRET = "Rs7VaSkK747thPO6rSTkZLZrcjAXy3";
    private static final String BUCKET_NAME = "inforum";
    
    /**
     * 上传文件到OSS
     * @param inputStream 文件输入流
     * @param originalFilename 原始文件名
     * @return 文件的访问URL
     */
    public static String uploadFile(InputStream inputStream, String originalFilename) {
        OSS ossClient = null;
        try {
            // 创建OSS客户端实例
            ossClient = new OSSClientBuilder().build(ENDPOINT, ACCESS_KEY_ID, ACCESS_KEY_SECRET);
            
            // 获取文件扩展名
            String extension = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                int lastDotIndex = originalFilename.lastIndexOf('.');
                extension = originalFilename.substring(lastDotIndex).toLowerCase();
            }
            
            // 生成唯一文件名：images/日期/时间戳_随机UUID.扩展名
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
            String dateDir = sdf.format(new Date());
            SimpleDateFormat timeSdf = new SimpleDateFormat("yyyyMMddHHmmss");
            String timestamp = timeSdf.format(new Date());
            String uuid = UUID.randomUUID().toString().replace("-", "").substring(0, 8);
            String objectName = "images/" + dateDir + "/" + timestamp + "_" + uuid + extension;
            
            // 创建PutObjectRequest对象
            PutObjectRequest putObjectRequest = new PutObjectRequest(BUCKET_NAME, objectName, inputStream);
            
            // 上传文件
            ossClient.putObject(putObjectRequest);
            
            // 返回文件的访问URL（使用外网访问地址）
            String fileUrl = "https://" + BUCKET_NAME + "." + ENDPOINT + "/" + objectName;
            
            // 上传成功，返回URL
            return fileUrl;
            
        } catch (Exception e) {
            // 上传失败，记录异常并抛出
            e.printStackTrace();
            String errorMsg = e.getMessage();
            if (errorMsg == null || errorMsg.trim().isEmpty()) {
                errorMsg = "未知错误";
            }
            throw new RuntimeException("文件上传到OSS失败：" + errorMsg, e);
        } finally {
            // 确保关闭OSSClient
            if (ossClient != null) {
                try {
                    ossClient.shutdown();
                } catch (Exception e) {
                    // 关闭客户端失败不影响上传结果
                    e.printStackTrace();
                }
            }
        }
    }
    
    /**
     * 上传头像到OSS
     * @param inputStream 文件输入流
     * @param originalFilename 原始文件名
     * @return 文件的访问URL
     */
    public static String uploadAvatar(InputStream inputStream, String originalFilename) {
        OSS ossClient = null;
        try {
            // 创建OSS客户端实例
            ossClient = new OSSClientBuilder().build(ENDPOINT, ACCESS_KEY_ID, ACCESS_KEY_SECRET);
            
            // 获取文件扩展名
            String extension = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                int lastDotIndex = originalFilename.lastIndexOf('.');
                extension = originalFilename.substring(lastDotIndex).toLowerCase();
            } else {
                extension = ".jpg"; // 默认使用jpg
            }
            
            // 生成唯一文件名：avatars/日期/时间戳_随机UUID.扩展名
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
            String dateDir = sdf.format(new Date());
            SimpleDateFormat timeSdf = new SimpleDateFormat("yyyyMMddHHmmss");
            String timestamp = timeSdf.format(new Date());
            String uuid = UUID.randomUUID().toString().replace("-", "").substring(0, 8);
            String objectName = "avatars/" + dateDir + "/" + timestamp + "_" + uuid + extension;
            
            // 创建PutObjectRequest对象
            PutObjectRequest putObjectRequest = new PutObjectRequest(BUCKET_NAME, objectName, inputStream);
            
            // 上传文件
            ossClient.putObject(putObjectRequest);
            
            // 返回文件的访问URL（使用外网访问地址）
            String fileUrl = "https://" + BUCKET_NAME + "." + ENDPOINT + "/" + objectName;
            
            // 上传成功，返回URL
            return fileUrl;
            
        } catch (Exception e) {
            // 上传失败，记录异常并抛出
            e.printStackTrace();
            String errorMsg = e.getMessage();
            if (errorMsg == null || errorMsg.trim().isEmpty()) {
                errorMsg = "未知错误";
            }
            throw new RuntimeException("头像上传到OSS失败：" + errorMsg, e);
        } finally {
            // 确保关闭OSSClient
            if (ossClient != null) {
                try {
                    ossClient.shutdown();
                } catch (Exception e) {
                    // 关闭客户端失败不影响上传结果
                    e.printStackTrace();
                }
            }
        }
    }
    
    /**
     * 删除OSS中的文件
     * @param fileUrl 文件的完整URL
     */
    public static void deleteFile(String fileUrl) {
        if (fileUrl == null || fileUrl.isEmpty()) {
            return;
        }
        
        OSS ossClient = null;
        try {
            // 创建OSS客户端实例
            ossClient = new OSSClientBuilder().build(ENDPOINT, ACCESS_KEY_ID, ACCESS_KEY_SECRET);
            
            // 从URL中提取objectName
            // URL格式: https://bucket.endpoint/objectName
            String objectName = fileUrl;
            if (fileUrl.startsWith("https://")) {
                String path = fileUrl.substring(fileUrl.indexOf("/", 8) + 1);
                objectName = path;
            } else if (fileUrl.startsWith("http://")) {
                String path = fileUrl.substring(fileUrl.indexOf("/", 7) + 1);
                objectName = path;
            }
            
            // 删除文件
            ossClient.deleteObject(BUCKET_NAME, objectName);
            
        } catch (Exception e) {
            e.printStackTrace();
            // 删除失败不抛出异常，只记录日志
            System.err.println("删除OSS文件失败：" + e.getMessage());
        } finally {
            // 关闭OSSClient
            if (ossClient != null) {
                ossClient.shutdown();
            }
        }
    }
}

