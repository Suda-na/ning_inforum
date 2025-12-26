package com.niit.service;

import com.niit.pojo.Report;
import com.niit.pojo.ReportVO;
import java.util.List;

/**
 * 举报Service接口
 */
public interface ReportService {
    /**
     * 根据ID查询举报信息
     * @param reportId 举报ID
     * @return 举报对象
     */
    ReportVO getReportById(Integer reportId);

    /**
     * 查询所有举报信息
     * @return 举报列表
     */
    List<ReportVO> getAllReports();

    /**
     * 根据条件查询举报信息
     *
     * @param report 举报对象，包含查询条件
     * @return 举报列表
     */
    List<ReportVO> getReportsByCondition(Report report);

    /**
     * 根据条件搜索举报信息（支持关键词、日期、处理结果等）
     *
     * @param keyword 关键词
     * @param targetType 目标类型
     * @param resultFilter 处理结果筛选：ban(已封禁), no-ban(未封禁), other(其他), all(全部)
     * @param status 状态：0待处理, 1已处理
     * @param date 日期（格式：yyyy-MM-dd）
     * @return 举报列表
     */
    List<ReportVO> searchReports(String keyword, String targetType, String resultFilter, Integer status, String date);

    /**
     * 添加举报信息
     * @param report 举报对象
     * @return 添加是否成功
     */
    boolean addReport(Report report);

    /**
     * 更新举报信息
     * @param report 举报对象
     * @return 更新是否成功
     */
    boolean updateReport(Report report);

    /**
     * 删除举报信息
     * @param reportId 举报ID
     * @return 删除是否成功
     */
    boolean deleteReport(Integer reportId);

    /**
     * 根据状态查询举报信息
     * @param status 状态：0待处理, 1已处理
     * @return 举报列表
     */
    List<ReportVO> getReportsByStatus(Integer status);

    /**
     * 根据目标类型查询举报信息
     * @param targetType 目标类型：用户, 帖子, 评论
     * @return 举报列表
     */
    List<ReportVO> getReportsByTargetType(String targetType);

    /**
     * 根据举报类型查询举报信息
     * @param reportType 举报类型：垃圾信息, 色情低俗, 违法违规, 欺诈, 侵权, 其他
     * @return 举报列表
     */
    List<ReportVO> getReportsByReportType(String reportType);

    /**
     * 统计举报数量
     * @return 举报总数
     */
    int countAllReports();

    /**
     * 根据状态统计举报数量
     * @param status 状态：0待处理, 1已处理
     * @return 举报数量
     */
    int countReportsByStatus(Integer status);

    /**
     * 处理举报
     * @param reportId 举报ID
     * @param adminId 管理员ID
     * @param result 结果：0未通过, 1通过
     * @param feedback 处理反馈
     * @return 处理是否成功
     */
    boolean processReport(Integer reportId, Integer adminId, Integer result, String feedback);

    /**
     * 封禁用户
     * @param userId 用户ID
     * @param adminId 管理员ID
     * @param reason 封禁原因
     * @param durationDays 封禁天数
     * @param restrictionType 封禁类型：post, comment, login
     * @return 封禁是否成功
     */
    boolean banUser(Integer userId, Integer adminId, String reason, Integer durationDays, String restrictionType);

    /**
     * 按日期统计举报数量
     * @param targetType 目标类型：帖子、评论、用户
     * @param interval 时间间隔：day（日）、month（月）、year（年）
     * @param count 返回的数据点数量
     * @return 统计数据列表，每个元素包含日期标签和数量
     */
    List<java.util.Map<String, Object>> getReportStatsByDate(String targetType, String interval, Integer count);
}