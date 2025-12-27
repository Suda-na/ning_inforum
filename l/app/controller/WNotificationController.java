package com.app.controller;

import com.app.common.Result;
import com.app.service.WNotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 安卓端系统通知Controller
 */
@RestController
@RequestMapping("/app/notification")
@CrossOrigin(origins = "*", maxAge = 3600)
public class WNotificationController {

    @Autowired
    private WNotificationService notificationService;

    /**
     * 获取最新系统通知（用于首页轮播）
     * @param userId 用户ID（可选，如果提供则返回用户自己的最新通知）
     * @return 最新通知
     */
    @GetMapping("/latest")
    public Result<Map<String, Object>> getLatestNotification(
            @RequestParam(value = "userId", required = false) Integer userId) {
        try {
            Map<String, Object> result;
            if (userId != null) {
                // 返回用户自己的最新通知
                result = notificationService.getLatestUserNotification(userId);
            } else {
                // 返回全局最新通知
                result = notificationService.getLatestNotification();
            }
            if (result == null) {
                return Result.success(null);
            }
            return Result.success(result);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("查询失败：" + e.getMessage());
        }
    }

    /**
     * 根据ID获取通知详情
     * @param messageId 通知ID
     * @return 通知详情
     */
    @GetMapping("/{messageId}")
    public Result<Map<String, Object>> getNotificationById(@PathVariable("messageId") Integer messageId) {
        try {
            Map<String, Object> result = notificationService.getNotificationById(messageId);
            if (result == null) {
                return Result.error("通知不存在");
            }
            return Result.success(result);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("查询失败：" + e.getMessage());
        }
    }

    /**
     * 获取系统通知列表
     * @param page 页码
     * @param pageSize 每页数量
     * @return 通知列表
     */
    @GetMapping("/list")
    public Result<Map<String, Object>> getNotificationList(
            @RequestParam(value = "page", defaultValue = "1") Integer page,
            @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {
        try {
            Map<String, Object> result = notificationService.getNotificationList(page, pageSize);
            return Result.success(result);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("查询失败：" + e.getMessage());
        }
    }
}

