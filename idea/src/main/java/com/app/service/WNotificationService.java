package com.app.service;

import java.util.Map;

/**
 * 安卓端系统通知Service接口
 */
public interface WNotificationService {
    /**
     * 获取最新系统通知
     * @return 最新通知
     */
    Map<String, Object> getLatestNotification();

    /**
     * 根据ID获取通知详情
     * @param messageId 通知ID
     * @return 通知详情
     */
    Map<String, Object> getNotificationById(Integer messageId);

    /**
     * 获取系统通知列表
     * @param page 页码
     * @param pageSize 每页数量
     * @return 通知列表
     */
    Map<String, Object> getNotificationList(Integer page, Integer pageSize);
}

