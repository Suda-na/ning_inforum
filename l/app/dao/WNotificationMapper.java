package com.app.dao;

import com.niit.pojo.Message;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 安卓端系统通知Mapper接口
 */
@Mapper
public interface WNotificationMapper {
    /**
     * 查询最新系统通知（全局最新）
     * @return 最新通知
     */
    Message selectLatestSystemNotification();

    /**
     * 查询用户自己的最新系统通知
     * @param userId 用户ID
     * @return 最新通知
     */
    Message selectLatestUserSystemNotification(@Param("userId") Integer userId);

    /**
     * 根据ID查询通知详情
     * @param messageId 通知ID
     * @return 通知详情
     */
    Message selectNotificationById(@Param("messageId") Integer messageId);

    /**
     * 查询系统通知列表（分页）
     * @param start 起始位置
     * @param size 数量
     * @return 通知列表
     */
    List<Message> selectSystemNotifications(@Param("start") Integer start, @Param("size") Integer size);

    /**
     * 统计系统通知数量
     * @return 通知数量
     */
    int countSystemNotifications();
}

