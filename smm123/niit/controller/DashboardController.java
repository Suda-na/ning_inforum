package com.niit.controller;

import com.niit.service.DashboardService;
import com.niit.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 数字大屏 Controller
 * 处理数字大屏相关的数据请求
 */
@Controller
public class DashboardController {
    @Autowired
    private UserService userService;
    
    @Autowired
    private DashboardService dashboardService;

    /**
     * 跳转到数字大屏页面
     * @return 视图名称，会跳转到 dashboard.html
     */
    @RequestMapping("/dashboard.html")
    public String dashboard() {
        return "dashboard";
    }

    /**
     * 获取用户总数
     * @return 用户总数
     */
    @ResponseBody
    @RequestMapping("/api/dashboard/userCount")
    public Map<String, Object> getUserCount() {
        Map<String, Object> result = new HashMap<>();
        try {
            int userCount = userService.findAllUsers().size();
            result.put("success", true);
            result.put("count", userCount);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "获取用户数量失败：" + e.getMessage());
        }
        return result;
    }

    /**
     * 获取当日发布的内容数量
     * @return 当日发布的内容数量
     */
    @ResponseBody
    @RequestMapping("/api/dashboard/todayPostCount")
    public Map<String, Object> getTodayPostCount() {
        Map<String, Object> result = new HashMap<>();
        try {
            int todayPostCount = dashboardService.countTodayPosts();
            result.put("success", true);
            result.put("count", todayPostCount);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "获取当日内容发布数量失败：" + e.getMessage());
        }
        return result;
    }

    /**
     * 获取待处理举报数量
     * @return 待处理举报数量（status=0）
     */
    @ResponseBody
    @RequestMapping("/api/dashboard/pendingReportCount")
    public Map<String, Object> getPendingReportCount() {
        Map<String, Object> result = new HashMap<>();
        try {
            int pendingReportCount = dashboardService.countPendingReports();
            result.put("success", true);
            result.put("count", pendingReportCount);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "获取待处理举报数量失败：" + e.getMessage());
        }
        return result;
    }

    /**
     * 获取已封禁账号数量
     * @return 已封禁账号数量（status=1）
     */
    @ResponseBody
    @RequestMapping("/api/dashboard/bannedUserCount")
    public Map<String, Object> getBannedUserCount() {
        Map<String, Object> result = new HashMap<>();
        try {
            int bannedUserCount = dashboardService.countBannedUsers();
            result.put("success", true);
            result.put("count", bannedUserCount);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "获取已封禁账号数量失败：" + e.getMessage());
        }
        return result;
    }

    /**
     * 获取最近7天每日新增用户数量
     * @return 最近7天每日新增用户数量
     */
    @ResponseBody
    @RequestMapping("/api/dashboard/last7DaysNewUsers")
    public Map<String, Object> getLast7DaysNewUsers() {
        Map<String, Object> result = new HashMap<>();
        try {
            List<Map<String, Object>> data = dashboardService.getLast7DaysNewUsers();
            // 调试日志：打印返回的数据
            System.out.println("=== 最近7天每日新增用户数量查询结果 ===");
            System.out.println("数据条数: " + (data != null ? data.size() : 0));
            if (data != null) {
                for (Map<String, Object> item : data) {
                    System.out.println("日期: " + item.get("date") + ", 数量: " + item.get("count"));
                }
            }
            result.put("success", true);
            result.put("data", data);
        } catch (Exception e) {
            e.printStackTrace();
            result.put("success", false);
            result.put("message", "获取最近7天每日新增用户数量失败：" + e.getMessage());
        }
        return result;
    }

    /**
     * 获取最近7天每日互动数量
     * @return 最近7天每日互动数量
     */
    @ResponseBody
    @RequestMapping("/api/dashboard/last7DaysInteractions")
    public Map<String, Object> getLast7DaysInteractions() {
        Map<String, Object> result = new HashMap<>();
        try {
            List<Map<String, Object>> data = dashboardService.getLast7DaysInteractions();
            result.put("success", true);
            result.put("data", data);
        } catch (Exception e) {
            e.printStackTrace();
            result.put("success", false);
            result.put("message", "获取最近7天每日互动数量失败：" + e.getMessage());
        }
        return result;
    }

    /**
     * 获取最近7天每日新增举报数量
     * @return 最近7天每日新增举报数量
     */
    @ResponseBody
    @RequestMapping("/api/dashboard/last7DaysNewReports")
    public Map<String, Object> getLast7DaysNewReports() {
        Map<String, Object> result = new HashMap<>();
        try {
            List<Map<String, Object>> data = dashboardService.getLast7DaysNewReports();
            result.put("success", true);
            result.put("data", data);
        } catch (Exception e) {
            e.printStackTrace();
            result.put("success", false);
            result.put("message", "获取最近7天每日新增举报数量失败：" + e.getMessage());
        }
        return result;
    }

    /**
     * 获取各类型举报数量（用于风险雷达图）
     * @return 各类型举报数量
     */
    @ResponseBody
    @RequestMapping("/api/dashboard/reportTypeCounts")
    public Map<String, Object> getReportTypeCounts() {
        Map<String, Object> result = new HashMap<>();
        try {
            List<Map<String, Object>> data = dashboardService.getReportTypeCounts();
            result.put("success", true);
            result.put("data", data);
        } catch (Exception e) {
            e.printStackTrace();
            result.put("success", false);
            result.put("message", "获取各类型举报数量失败：" + e.getMessage());
        }
        return result;
    }

    /**
     * 获取各分类帖子数量（用于内容板块热度图）
     * @return 各分类帖子数量
     */
    @ResponseBody
    @RequestMapping("/api/dashboard/categoryPostCounts")
    public Map<String, Object> getCategoryPostCounts() {
        Map<String, Object> result = new HashMap<>();
        try {
            List<Map<String, Object>> data = dashboardService.getCategoryPostCounts();
            result.put("success", true);
            result.put("data", data);
        } catch (Exception e) {
            e.printStackTrace();
            result.put("success", false);
            result.put("message", "获取各分类帖子数量失败：" + e.getMessage());
        }
        return result;
    }

    /**
     * 获取用户性别统计（用于用户性别构成图）
     * @return 用户性别统计
     */
    @ResponseBody
    @RequestMapping("/api/dashboard/userGenderCounts")
    public Map<String, Object> getUserGenderCounts() {
        Map<String, Object> result = new HashMap<>();
        try {
            List<Map<String, Object>> data = dashboardService.getUserGenderCounts();
            result.put("success", true);
            result.put("data", data);
            System.out.println("后端返回的用户性别统计数据: " + data); // 添加日志
        } catch (Exception e) {
            e.printStackTrace();
            result.put("success", false);
            result.put("message", "获取用户性别统计失败：" + e.getMessage());
        }
        return result;
    }

    /**
     * 获取动态状态统计（用于动态状态统计图）
     * @return 动态状态统计
     */
    @ResponseBody
    @RequestMapping("/api/dashboard/postStatusCounts")
    public Map<String, Object> getPostStatusCounts() {
        Map<String, Object> result = new HashMap<>();
        try {
            List<Map<String, Object>> data = dashboardService.getPostStatusCounts();
            result.put("success", true);
            result.put("data", data);
        } catch (Exception e) {
            e.printStackTrace();
            result.put("success", false);
            result.put("message", "获取动态状态统计失败：" + e.getMessage());
        }
        return result;
    }

    /**
     * 获取最新用户列表（用于大屏底部“最新用户”模块）
     */
    @ResponseBody
    @RequestMapping("/api/dashboard/latestUsers")
    public Map<String, Object> getLatestUsers() {
        Map<String, Object> result = new HashMap<>();
        try {
            List<Map<String, Object>> data = dashboardService.getLatestUsers();
            result.put("success", true);
            result.put("data", data);
        } catch (Exception e) {
            e.printStackTrace();
            result.put("success", false);
            result.put("message", "获取最新用户列表失败：" + e.getMessage());
        }
        return result;
    }

    /**
     * 获取封禁记录列表（用于大屏底部“封禁记录”模块）
     */
    @ResponseBody
    @RequestMapping("/api/dashboard/banRecords")
    public Map<String, Object> getBanRecords() {
        Map<String, Object> result = new HashMap<>();
        try {
            List<Map<String, Object>> data = dashboardService.getBanRecords();
            result.put("success", true);
            result.put("data", data);
        } catch (Exception e) {
            e.printStackTrace();
            result.put("success", false);
            result.put("message", "获取封禁记录列表失败：" + e.getMessage());
        }
        return result;
    }
}

