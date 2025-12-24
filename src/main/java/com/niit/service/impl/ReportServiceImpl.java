package com.niit.service.impl;

import com.niit.dao.ReportMapper;
import com.niit.dao.UserBanHistoryMapper;
import com.niit.dao.UserMapperWang;
import com.niit.dao.UserPermissionMapper;
import com.niit.dao.MessageMapperWang;
import com.niit.pojo.Report;
import com.niit.pojo.ReportVO;
import com.niit.pojo.UserBanHistory;
import com.niit.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * 举报Service实现类
 */
@Service
@Transactional
public  class ReportServiceImpl implements ReportService {

    @Autowired
    private ReportMapper reportMapper;

    @Autowired
    private UserBanHistoryMapper userBanHistoryMapper;

    @Autowired
    private UserPermissionMapper userPermissionMapper;

    @Autowired
    private UserMapperWang userMapperWang;

    @Autowired
    private MessageMapperWang messageMapperWang;

    @Override
    public ReportVO getReportById(Integer reportId) {
        return reportMapper.selectByPrimaryKey(reportId);
    }

    @Override
    public List<ReportVO> getAllReports() {
        return reportMapper.selectAll();
    }

    @Override
    public List<ReportVO> getReportsByCondition(Report report) {
        // 将Report对象转换为Map以保持兼容性
        java.util.Map<String, Object> params = new java.util.HashMap<>();
        if (report.getReportId() != null) {
            params.put("reportId", report.getReportId());
        }
        if (report.getReporterId() != null) {
            params.put("reporterId", report.getReporterId());
        }
        if (report.getTargetType() != null && !report.getTargetType().isEmpty()) {
            params.put("targetType", report.getTargetType());
        }
        if (report.getTargetId() != null) {
            params.put("targetId", report.getTargetId());
        }
        if (report.getReportType() != null && !report.getReportType().isEmpty()) {
            params.put("reportType", report.getReportType());
        }
        if (report.getStatus() != null) {
            params.put("status", report.getStatus());
        }
        if (report.getResult() != null) {
            params.put("result", report.getResult());
        }
        return reportMapper.selectByCondition(params);
    }

    @Override
    public List<ReportVO> searchReports(String keyword, String targetType, String resultFilter, Integer status, String date) {
        // 构建查询参数Map
        java.util.Map<String, Object> params = new java.util.HashMap<>();
        if (keyword != null && !keyword.trim().isEmpty()) {
            params.put("keyword", keyword.trim());
        }
        if (targetType != null && !targetType.equals("all")) {
            // 前端传的是post/comment/user，需要转换为数据库中的值
            if (targetType.equals("post")) {
                params.put("targetType", "帖子");
            } else if (targetType.equals("comment")) {
                params.put("targetType", "评论");
            } else if (targetType.equals("user")) {
                params.put("targetType", "用户");
            } else {
                params.put("targetType", targetType);
            }
        }
        if (resultFilter != null && !resultFilter.equals("all")) {
            params.put("resultFilter", resultFilter);
        }
        if (status != null) {
            // status已经是Integer类型，直接使用
            params.put("status", status);
        }
        if (date != null && !date.trim().isEmpty()) {
            params.put("date", date.trim());
        }
        
        // 如果没有任何条件，返回空列表（或者可以返回所有记录）
        // 这里返回所有记录，让用户看到全部数据
        if (params.isEmpty()) {
            return reportMapper.selectAll();
        }
        
        return reportMapper.selectByCondition(params);
    }

    @Override
    public boolean addReport(Report report) {
        report.setCreateTime(new Date());
        report.setStatus(0); // 默认为待处理状态
        return reportMapper.insert(report) > 0;
    }

    @Override
    public boolean updateReport(Report report) {
        return reportMapper.updateByPrimaryKey(report) > 0;
    }

    @Override
    public boolean deleteReport(Integer reportId) {
        return reportMapper.deleteByPrimaryKey(reportId) > 0;
    }

    @Override
    public List<ReportVO> getReportsByStatus(Integer status) {
        return reportMapper.selectByStatus(status);
    }

    @Override
    public List<ReportVO> getReportsByTargetType(String targetType) {
        return reportMapper.selectByTargetType(targetType);
    }

    @Override
    public List<ReportVO> getReportsByReportType(String reportType) {
        return reportMapper.selectByReportType(reportType);
    }

    @Override
    public int countAllReports() {
        return reportMapper.countAll();
    }

    @Override
    public int countReportsByStatus(Integer status) {
        return reportMapper.countByStatus(status);
    }

    @Override
    public boolean processReport(Integer reportId, Integer adminId, Integer result, String feedback) {
        ReportVO reportVO = reportMapper.selectByPrimaryKey(reportId);
        if (reportVO == null) {
            return false;
        }
        // 创建Report对象，复制需要更新的字段
        Report report = new Report();
        report.setReportId(reportVO.getReportId());
        report.setReporterId(reportVO.getReporterId());
        report.setTargetType(reportVO.getTargetType());
        report.setTargetId(reportVO.getTargetId());
        report.setReportType(reportVO.getReportType());
        report.setDescription(reportVO.getDescription());
        report.setReportImage(reportVO.getReportImage());
        report.setStatus(1); // 已处理状态
        report.setAdminId(adminId);
        report.setResult(result);
        report.setFeedback(feedback);
        report.setCreateTime(reportVO.getCreateTime());
        Date processTime = new Date();
        report.setProcessTime(processTime);

        boolean updated = reportMapper.updateByPrimaryKey(report) > 0;

        // 处理完成后，给举报人发站内信（私信）
        if (updated) {
            String resultText = (result != null && result == 1)
                    ? "已通过并采取相应措施"
                    : "未通过，如有疑问请联系管理员";
            String content;
            if (feedback != null && !feedback.isEmpty()) {
                content = "您的举报已处理（结果：" + resultText + "）。反馈：" + feedback;
            } else {
                content = "您的举报已处理（结果：" + resultText + "）。感谢您的反馈，我们已完成审核。";
            }
            try {
                messageMapperWang.insertMessage(adminId, reportVO.getReporterId(), content, processTime);
            } catch (Exception e) {
                System.err.println("[ProcessReport] insert message failed: " + e.getMessage());
                e.printStackTrace();
                // 即使消息发送失败，也返回更新结果
            }
        }

        return updated;
    }

    @Override
    public boolean banUser(Integer userId, Integer adminId, String reason, Integer durationDays, String restrictionType) {
        System.out.println("[BanUser] enter service | userId=" + userId + ", adminId=" + adminId
                + ", durationDays=" + durationDays + ", restrictionType=" + restrictionType
                + ", reason=" + reason);
        // 创建封禁历史记录
        UserBanHistory banHistory = new UserBanHistory();
        banHistory.setUserId(userId);
        banHistory.setAdminId(adminId);
        banHistory.setActionType("封禁");
        banHistory.setReason(reason);
        banHistory.setDurationDays(durationDays);
        banHistory.setIsActive(1);
        banHistory.setCreateTime(new Date());
        banHistory.setStartTime(new Date());
        
        // 计算结束时间
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, durationDays);
        banHistory.setEndTime(calendar.getTime());
        
        // 设置封禁前后的权限
        // 这里根据不同的封禁类型设置不同的权限
        String restrictionsBefore = "{\"can_buy\": 1, \"can_like\": 1, \"can_post\": 1, \"can_sell\": 1, \"can_follow\": 1, \"can_comment\": 1, \"can_message\": 1, \"can_run_errand\": 1}";
        String restrictionsAfter = restrictionsBefore;
        
        // 根据封禁类型更新user_permission表
        // 先确保user_permission表中存在该用户的记录（通常由触发器自动创建，但为了安全还是检查一下）
        if (userPermissionMapper.countByUserId(userId) == 0) {
            // 如果不存在，先创建默认权限记录（所有权限为1）
            userPermissionMapper.insertDefaultPermission(userId);
        }
        
        if ("post".equals(restrictionType)) {
            // 封禁发动态权限
            restrictionsAfter = "{\"can_buy\": 1, \"can_like\": 1, \"can_post\": 0, \"can_sell\": 1, \"can_follow\": 1, \"can_comment\": 1, \"can_message\": 1, \"can_run_errand\": 1}";
            userPermissionMapper.updatePermission(userId, 0, null, null, null, null, null, null, null);
        } else if ("comment".equals(restrictionType)) {
            // 封禁发评论权限
            restrictionsAfter = "{\"can_buy\": 1, \"can_like\": 1, \"can_post\": 1, \"can_sell\": 1, \"can_follow\": 1, \"can_comment\": 0, \"can_message\": 1, \"can_run_errand\": 1}";
            userPermissionMapper.updatePermission(userId, null, 0, null, null, null, null, null, null);
        } else if ("login".equals(restrictionType)) {
            // 封禁账户（所有权限）
            restrictionsAfter = "{\"can_buy\": 0, \"can_like\": 0, \"can_post\": 0, \"can_sell\": 0, \"can_follow\": 0, \"can_comment\": 0, \"can_message\": 0, \"can_run_errand\": 0}";
            userPermissionMapper.updatePermission(userId, 0, 0, 0, 0, 0, 0, 0, 0);
            // 同时更新user表的status字段为1（禁用）
            userMapperWang.updateStatus(userId, 1);
        }
        
        banHistory.setRestrictionsBefore(restrictionsBefore);
        banHistory.setRestrictionsAfter(restrictionsAfter);
        
        // 插入封禁记录
        boolean historyInserted = userBanHistoryMapper.insert(banHistory) > 0;
        if (historyInserted) {
            try {
                userMapperWang.incrementWarningCount(userId);
            } catch (Exception e) {
                System.err.println("[BanUser] increment warning_count failed: " + e.getMessage());
            }
        }
        return historyInserted;
    }

    @Override
    public List<java.util.Map<String, Object>> getReportStatsByDate(String targetType, String interval, Integer count) {
        // 转换目标类型：前端传的是post/comment/user，需要转换为数据库中的值
        String dbTargetType = null;
        if (targetType != null && !targetType.isEmpty()) {
            if ("post".equals(targetType)) {
                dbTargetType = "帖子";
            } else if ("comment".equals(targetType)) {
                dbTargetType = "评论";
            } else if ("user".equals(targetType)) {
                dbTargetType = "用户";
            } else {
                dbTargetType = targetType;
            }
        }
        // 对于day间隔，需要调整count值以匹配前端生成的日期范围
        // 前端生成的是从(count-1)天前到今天，共count个日期点
        // 但SQL查询需要包含这个范围，所以使用count即可（因为SQL会查询到CURDATE()）
        System.out.println("[getReportStatsByDate] targetType=" + targetType + ", dbTargetType=" + dbTargetType + ", interval=" + interval + ", count=" + count);
        List<java.util.Map<String, Object>> result = reportMapper.countReportsByDate(dbTargetType, interval, count);
        System.out.println("[getReportStatsByDate] result size=" + (result != null ? result.size() : "null"));
        if (result != null && !result.isEmpty()) {
            System.out.println("[getReportStatsByDate] first item: " + result.get(0));
        } else {
            System.out.println("[getReportStatsByDate] result is empty or null");
        }
        return result;
    }
}