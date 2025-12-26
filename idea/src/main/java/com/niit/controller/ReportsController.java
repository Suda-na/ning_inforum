package com.niit.controller;

import com.niit.pojo.Report;
import com.niit.pojo.ReportVO;
import com.niit.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 举报管理 Controller
 */
@Controller
@RequestMapping("/admin")
public class ReportsController {

    @Autowired
    private ReportService reportService;

    /**
     * 跳转到举报列表页面
     * @return 视图名称，会跳转到 report_pending.html
     */
    @RequestMapping("/reports")
    public String reports(Model model) {
        // 获取所有举报信息
        List<ReportVO> reports = reportService.getAllReports();
        model.addAttribute("reports", reports);
        // 统计待处理举报数量
        int pendingCount = reportService.countReportsByStatus(0);
        model.addAttribute("pendingCount", pendingCount);
        return "report_pending";
    }

    /**
     * 根据状态查询举报
     * @param status 状态：0待处理, 1已处理
     * @param model 模型
     * @return 视图名称
     */
    @RequestMapping("/reports/status/{status}")
    public String reportsByStatus(@PathVariable("status") Integer status, Model model) {
        List<ReportVO> reports = reportService.getReportsByStatus(status);
        model.addAttribute("reports", reports);
        int pendingCount = reportService.countReportsByStatus(0);
        model.addAttribute("pendingCount", pendingCount);
        return "report_pending";
    }

    /**
     * 获取举报详情
     * @param reportId 举报ID
     * @param model 模型
     * @return 举报对象
     */
    @RequestMapping("/report/detail/{reportId}")
    @ResponseBody
    public ReportVO getReportDetail(@PathVariable("reportId") Integer reportId, Model model) {
        return reportService.getReportById(reportId);
    }

    /**
     * 处理举报
     * @param reportId 举报ID
     * @param adminId 管理员ID
     * @param result 结果：0未通过, 1通过
     * @param feedback 处理反馈
     * @return 处理结果
     */
    @RequestMapping(value = "/report/process", method = RequestMethod.POST)
    @ResponseBody
    public String processReport(@RequestParam("reportId") Integer reportId, 
                              @RequestParam("adminId") Integer adminId, 
                              @RequestParam("result") Integer result, 
                              @RequestParam("feedback") String feedback) {
        boolean success = reportService.processReport(reportId, adminId, result, feedback);
        if (success) {
            return "success";
        } else {
            return "error";
        }
    }

    /**
     * 删除举报
     * @param reportId 举报ID
     * @return 删除结果
     */
    @RequestMapping("/report/delete/{reportId}")
    @ResponseBody
    public String deleteReport(@PathVariable("reportId") Integer reportId) {
        boolean success = reportService.deleteReport(reportId);
        if (success) {
            return "success";
        } else {
            return "error";
        }
    }

    /**
     * 封禁用户
     * @param userId 用户ID
     * @param adminId 管理员ID
     * @param reason 封禁原因
     * @param durationDays 封禁天数
     * @param restrictionType 封禁类型：post, comment, login
     * @return 封禁结果
     */
    @RequestMapping(value = "/user/ban", method = RequestMethod.POST)
    @ResponseBody
    public String banUser(@RequestParam(value = "userId", required = false) String userIdStr,
                         @RequestParam(value = "adminId", required = false) String adminIdStr,
                         @RequestParam(value = "reason", required = false) String reason,
                         @RequestParam(value = "durationDays", required = false) String durationDaysStr,
                         @RequestParam(value = "restrictionType", required = false) String restrictionType) {
        System.out.println("[BanUser] enter controller raw | userId=" + userIdStr + ", adminId=" + adminIdStr
                + ", durationDays=" + durationDaysStr + ", restrictionType=" + restrictionType
                + ", reason=" + reason);

        Integer userId = null;
        Integer adminId = null;
        Integer durationDays = null;
        try {
            userId = (userIdStr != null && !userIdStr.isEmpty()) ? Integer.parseInt(userIdStr) : null;
        }
        catch (Exception e) {
        }
        try { adminId = (adminIdStr != null && !adminIdStr.isEmpty()) ? Integer.parseInt(adminIdStr) : null; } catch (Exception e) {}
        try { durationDays = (durationDaysStr != null && !durationDaysStr.isEmpty()) ? Integer.parseInt(durationDaysStr) : null; } catch (Exception e) {}

        System.out.println("[BanUser] parsed | userId=" + userId + ", adminId=" + adminId
                + ", durationDays=" + durationDays + ", restrictionType=" + restrictionType
                + ", reason=" + reason);

        if (userId == null || adminId == null || durationDays == null || restrictionType == null) {
            System.out.println("[BanUser] param missing -> return error");
            return "error";
        }

        boolean success = reportService.banUser(userId, adminId, reason, durationDays, restrictionType);
        System.out.println("[BanUser] controller result: " + success);
        return success ? "success" : "error";
    }

    /**
     * 搜索举报信息
     * @param keyword 关键词（在举报描述、举报人、被举报人、帖子内容、评论内容中搜索）
     * @param type 目标类型：post(动态), comment(评论), user(用户), all(全部)
     * @param result 处理结果筛选：ban(已封禁), no-ban(未封禁), other(其他), all(全部)
     * @param status 状态：pending(未处理), resolved(已处理), all(全部)
     * @param date 日期（格式：yyyy-MM-dd）
     * @return 举报列表
     */
    @RequestMapping("/reports/search")
    @ResponseBody
    public List<ReportVO> searchReports(@RequestParam(value = "keyword", required = false) String keyword,
                                       @RequestParam(value = "type", required = false) String type,
                                       @RequestParam(value = "result", required = false) String result,
                                       @RequestParam(value = "status", required = false) String status,
                                       @RequestParam(value = "date", required = false) String date) {
        // 转换状态参数
        Integer statusInt = null;
        if (status != null && !status.equals("all") && !status.isEmpty()) {
            if (status.equals("pending")) {
                statusInt = 0;
            } else if (status.equals("resolved")) {
                statusInt = 1;
            } else {
                try {
                    statusInt = Integer.parseInt(status);
                } catch (NumberFormatException e) {
                    // 忽略无效的状态值
                }
            }
        }
        
        // 调用新的搜索方法
        return reportService.searchReports(keyword, type, result, statusInt, date);
    }

    /**
     * 获取举报统计数据
     * @return 统计数据
     */
    @RequestMapping("/report/stats")
    @ResponseBody
    public String getReportStats() {
        // 这里可以根据需要返回各类统计数据
        int total = reportService.countAllReports();
        int pending = reportService.countReportsByStatus(0);
        int processed = reportService.countReportsByStatus(1);
        return "{\"total\":" + total + ",\"pending\":" + pending + ",\"processed\":" + processed + "}";
    }

    /**
     * 获取举报统计数据（用于折线图）
     * @param interval 时间间隔：day（日）、month（月）、year（年）
     * @param count 返回的数据点数量
     * @return 统计数据，包含三个目标类型的数据
     */
    @RequestMapping("/report/stats/chart")
    @ResponseBody
    public Map<String, Object> getReportStatsForChart(
            @RequestParam(value = "interval", defaultValue = "day") String interval,
            @RequestParam(value = "count", defaultValue = "10") Integer count) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            System.out.println("[getReportStatsForChart] interval=" + interval + ", count=" + count);
            
            // 获取三种类型的统计数据
            List<Map<String, Object>> postStats = reportService.getReportStatsByDate("post", interval, count);
            System.out.println("[getReportStatsForChart] postStats size=" + (postStats != null ? postStats.size() : "null"));
            
            List<Map<String, Object>> commentStats = reportService.getReportStatsByDate("comment", interval, count);
            System.out.println("[getReportStatsForChart] commentStats size=" + (commentStats != null ? commentStats.size() : "null"));
            
            List<Map<String, Object>> userStats = reportService.getReportStatsByDate("user", interval, count);
            System.out.println("[getReportStatsForChart] userStats size=" + (userStats != null ? userStats.size() : "null"));
            
            result.put("posts", postStats != null ? postStats : new ArrayList<>());
            result.put("comments", commentStats != null ? commentStats : new ArrayList<>());
            result.put("users", userStats != null ? userStats : new ArrayList<>());
            
        } catch (Exception e) {
            System.err.println("[getReportStatsForChart] Error: " + e.getMessage());
            e.printStackTrace();
            // 返回空数据，避免前端报错
            result.put("posts", new ArrayList<>());
            result.put("comments", new ArrayList<>());
            result.put("users", new ArrayList<>());
        }
        
        return result;
    }
}
