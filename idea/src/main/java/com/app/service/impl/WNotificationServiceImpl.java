package com.app.service.impl;

import com.app.dao.WNotificationMapper;
import com.app.service.WNotificationService;
import com.niit.pojo.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 安卓端系统通知Service实现类
 */
@Service
public class WNotificationServiceImpl implements WNotificationService {

    @Autowired
    private WNotificationMapper notificationMapper;

    @Override
    public Map<String, Object> getLatestNotification() {
        Message message = notificationMapper.selectLatestSystemNotification();
        if (message == null) {
            return null;
        }
        return convertMessageToMap(message);
    }

    @Override
    public Map<String, Object> getNotificationById(Integer messageId) {
        Message message = notificationMapper.selectNotificationById(messageId);
        if (message == null) {
            return null;
        }
        return convertMessageToMap(message);
    }

    @Override
    public Map<String, Object> getNotificationList(Integer page, Integer pageSize) {
        Map<String, Object> result = new HashMap<>();
        
        int start = (page - 1) * pageSize;
        List<Message> messages = notificationMapper.selectSystemNotifications(start, pageSize);
        int total = notificationMapper.countSystemNotifications();
        int totalPages = (int) Math.ceil((double) total / pageSize);
        
        List<Map<String, Object>> notificationList = new ArrayList<>();
        for (Message message : messages) {
            notificationList.add(convertMessageToMap(message));
        }
        
        result.put("notifications", notificationList);
        result.put("total", total);
        result.put("page", page);
        result.put("pageSize", pageSize);
        result.put("totalPages", totalPages);
        
        return result;
    }

    /**
     * 将Message转换为Map格式（适配安卓端）
     */
    private Map<String, Object> convertMessageToMap(Message message) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", String.valueOf(message.getMessageId()));
        map.put("messageId", message.getMessageId());
        map.put("title", message.getTitle() != null ? message.getTitle() : "");
        map.put("content", message.getContent() != null ? message.getContent() : "");
        map.put("time", formatTime(message.getCreateTime()));
        
        // 图片列表（兼容imageUrl和images字段）
        List<String> images = new ArrayList<>();
        if (message.getImageUrl() != null && !message.getImageUrl().isEmpty()) {
            images.add(message.getImageUrl());
        }
        map.put("images", images);
        map.put("imageUrl", message.getImageUrl() != null ? message.getImageUrl() : ""); // 兼容旧字段
        
        return map;
    }

    /**
     * 格式化时间
     */
    private String formatTime(Date date) {
        if (date == null) {
            return "";
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        return sdf.format(date);
    }
}

