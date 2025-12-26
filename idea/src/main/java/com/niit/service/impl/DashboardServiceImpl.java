package com.niit.service.impl;

import com.niit.dao.DashboardPostDao;
import com.niit.dao.UserDao;
import com.niit.pojo.User;
import com.niit.pojo.UserBanHistory;
import com.niit.service.DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 数字大屏服务实现类，实现DashboardService接口
 */
@Service
public class DashboardServiceImpl implements DashboardService {
    @Autowired
    private DashboardPostDao dashboardPostDao;

    @Autowired
    private UserDao userDao;

    @Override
    public int countTodayPosts() {
        return dashboardPostDao.countTodayPosts();
    }

    @Override
    public int countPendingReports() {
        return dashboardPostDao.countPendingReports();
    }

    @Override
    public int countBannedUsers() {
        return dashboardPostDao.countBannedUsers();
    }

    @Override
    public List<Map<String, Object>> getLast7DaysNewUsers() {
        return dashboardPostDao.getLast7DaysNewUsers();
    }

    @Override
    public List<Map<String, Object>> getLast7DaysInteractions() {
        return dashboardPostDao.getLast7DaysInteractions();
    }

    @Override
    public List<Map<String, Object>> getLast7DaysNewReports() {
        return dashboardPostDao.getLast7DaysNewReports();
    }

    @Override
    public List<Map<String, Object>> getReportTypeCounts() {
        return dashboardPostDao.getReportTypeCounts();
    }

    @Override
    public List<Map<String, Object>> getCategoryPostCounts() {
        return dashboardPostDao.getCategoryPostCounts();
    }

    @Override
    public List<Map<String, Object>> getUserGenderCounts() {
        return dashboardPostDao.getUserGenderCounts();
    }

    @Override
    public List<Map<String, Object>> getPostStatusCounts() {
        return dashboardPostDao.getPostStatusCounts();
    }

    @Override
    public List<Map<String, Object>> getLatestUsers() {
        // 说明：
        // 这里不再直接使用 DashboardPostMapper 的自定义 SQL，
        // 而是复用已经稳定运行的 UserDao 映射，避免潜在的 SQL / 映射问题导致 500。
        List<User> allUsers = userDao.findAllUsers();
        if (allUsers == null || allUsers.isEmpty()) {
            return new ArrayList<>();
        }

        return allUsers.stream()
                .sorted((u1, u2) -> {
                    if (u1.getCreateTime() == null && u2.getCreateTime() == null) return 0;
                    if (u1.getCreateTime() == null) return 1;
                    if (u2.getCreateTime() == null) return -1;
                    return u2.getCreateTime().compareTo(u1.getCreateTime());
                })
                .limit(50)
                .map(u -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("userId", u.getUserId());
                    map.put("avatar", u.getAvatar());
                    map.put("username", u.getUsername());
                    map.put("gender", u.getGender());
                    map.put("phone", u.getPhone());
                    map.put("email", u.getEmail());
                    map.put("createTime", u.getCreateTime());
                    map.put("status", u.getStatus());
                    return map;
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<Map<String, Object>> getBanRecords() {
        // 使用现有的 UserDao 映射查询封禁历史，避免新增 Mapper 带来的 500 问题
        List<User> users = userDao.findAllUsers();
        if (users == null || users.isEmpty()) {
            return new ArrayList<>();
        }

        // 先构建一个 userId -> User 的缓存，方便后面查操作人和被封禁用户名称
        Map<Integer, User> userMap = users.stream()
                .collect(Collectors.toMap(User::getUserId, u -> u, (a, b) -> a));

        List<Map<String, Object>> records = new ArrayList<>();
        ObjectMapper mapper = new ObjectMapper();

        for (User user : users) {
            List<UserBanHistory> historyList = userDao.findBanHistoryByUserId(user.getUserId());
            if (historyList == null || historyList.isEmpty()) {
                continue;
            }

            for (UserBanHistory h : historyList) {
                Map<String, Object> map = new HashMap<>();
                map.put("historyId", h.getHistoryId());

                User admin = userMap.get(h.getAdminId());
                map.put("adminName", admin != null ? admin.getUsername() : ("管理员ID:" + h.getAdminId()));

                map.put("bannedUserName", user.getUsername());
                map.put("reason", h.getReason());
                map.put("startTime", h.getStartTime());
                map.put("createTime", h.getCreateTime());

                // 封禁天数（0 表示永久）
                map.put("durationDays", h.getDurationDays());

                // 封禁权限摘要（解析 restrictions_after）
                String banPermissions = "";
                try {
                    if (h.getRestrictionsAfter() != null && !h.getRestrictionsAfter().isEmpty()) {
                        Map<String, Object> afterMap = mapper.readValue(
                                h.getRestrictionsAfter(),
                                new TypeReference<Map<String, Object>>() {});
                        List<String> denied = new ArrayList<>();
                        addIfDenied(afterMap, denied, "can_post", "发帖");
                        addIfDenied(afterMap, denied, "can_comment", "评论");
                        addIfDenied(afterMap, denied, "can_like", "点赞");
                        addIfDenied(afterMap, denied, "can_follow", "关注");
                        addIfDenied(afterMap, denied, "can_message", "私信");
                        addIfDenied(afterMap, denied, "can_buy", "购买");
                        addIfDenied(afterMap, denied, "can_sell", "出售");
                        addIfDenied(afterMap, denied, "can_run_errand", "跑腿");
                        banPermissions = denied.isEmpty() ? "未解析" : String.join("、", denied);
                    }
                } catch (Exception ignore) {
                    banPermissions = "未解析";
                }
                map.put("banPermissions", banPermissions);

                records.add(map);
            }
        }

        // 按创建时间倒序，取最新 50 条
        return records.stream()
                .sorted((m1, m2) -> {
                    Object t1 = m1.get("createTime");
                    Object t2 = m2.get("createTime");
                    if (t1 == null && t2 == null) return 0;
                    if (t1 == null) return 1;
                    if (t2 == null) return -1;
                    return ((java.util.Date) t2).compareTo((java.util.Date) t1);
                })
                .limit(50)
                .collect(Collectors.toList());
    }

    private void addIfDenied(Map<String, Object> map, List<String> denied, String key, String label) {
        if (map == null) return;
        Object v = map.get(key);
        if (v == null) {
            // 再试驼峰
            String camel = toCamelCase(key);
            v = map.get(camel);
        }
        if (v != null) {
            try {
                int iv = Integer.parseInt(v.toString());
                if (iv == 0) {
                    denied.add(label);
                }
            } catch (Exception ignored) {}
        }
    }

    /**
     * 将下划线命名转换为驼峰命名（简易版）。
     */
    private String toCamelCase(String snake) {
        if (snake == null || snake.isEmpty()) return snake;
        StringBuilder sb = new StringBuilder();
        boolean upperNext = false;
        for (char c : snake.toCharArray()) {
            if (c == '_') {
                upperNext = true;
                continue;
            }
            if (upperNext) {
                sb.append(Character.toUpperCase(c));
                upperNext = false;
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }
}

