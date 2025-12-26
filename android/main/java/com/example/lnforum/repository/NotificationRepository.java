package com.example.lnforum.repository;

import com.example.lnforum.model.Notification;

import java.util.ArrayList;
import java.util.List;

/**
 * 系统通知数据仓库
 * 后续可替换为从SSM/数据库获取数据
 */
public class NotificationRepository {
    
    private static NotificationRepository instance;
    private List<Notification> notifications;
    
    private NotificationRepository() {
        loadSampleData();
    }
    
    public static NotificationRepository getInstance() {
        if (instance == null) {
            instance = new NotificationRepository();
        }
        return instance;
    }
    
    /**
     * 加载示例数据
     * 后续可替换为从SSM/数据库获取
     */
    private void loadSampleData() {
        notifications = new ArrayList<>();
        
        // 示例通知1
        List<String> images1 = new ArrayList<>();
        images1.add("https://example.com/image1.jpg");
        notifications.add(new Notification(
            "1",
            "重要通知：校园论坛系统升级",
            "亲爱的用户，校园论坛系统将于本周末进行升级维护，期间可能无法访问，请提前做好准备。升级后将带来更好的用户体验和更快的响应速度。",
            "2024-01-15 10:00",
            images1
        ));
        
        // 示例通知2
        List<String> images2 = new ArrayList<>();
        images2.add("https://example.com/image2.jpg");
        images2.add("https://example.com/image3.jpg");
        notifications.add(new Notification(
            "2",
            "新功能上线：跑腿服务",
            "校园论坛新增跑腿服务功能，同学们可以发布跑腿需求，也可以注册成为跑腿员。详情请查看跑腿板块。",
            "2024-01-14 14:30",
            images2
        ));
        
        // 示例通知3
        notifications.add(new Notification(
            "3",
            "关于失物招领板块的使用说明",
            "失物招领板块已优化，现在可以更方便地发布和查找失物信息。请同学们在发布时尽量提供详细信息和图片。",
            "2024-01-13 09:15",
            new ArrayList<>()
        ));
    }
    
    /**
     * 获取所有通知
     * 后续可替换为从SSM/数据库获取
     */
    public List<Notification> getAllNotifications() {
        return new ArrayList<>(notifications);
    }
    
    /**
     * 根据ID获取通知
     * 后续可替换为从SSM/数据库获取
     */
    public Notification getNotificationById(String id) {
        for (Notification notification : notifications) {
            if (notification.getId().equals(id)) {
                return notification;
            }
        }
        return null;
    }
    
    /**
     * 获取最新通知（用于轮播显示）
     * 后续可替换为从SSM/数据库获取
     */
    public Notification getLatestNotification() {
        if (notifications.isEmpty()) {
            return null;
        }
        return notifications.get(0);
    }
}

