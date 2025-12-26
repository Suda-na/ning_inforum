package com.niit.dao;

import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

/**
 * 数字大屏统计 Dao接口
 * 用于数字大屏相关的统计查询
 */
@Mapper
public interface DashboardPostDao {
    /**
     * 统计当日发布的内容数量
     * @return 当日发布的内容数量
     */
    int countTodayPosts();

    /**
     * 统计待处理举报数量
     * @return 待处理举报数量（status=0）
     */
    int countPendingReports();

    /**
     * 统计已封禁账号数量
     * @return 已封禁账号数量（status=1）
     */
    int countBannedUsers();

    /**
     * 查询最近7天每日新增用户数量
     * @return 最近7天每日新增用户数量列表，每个Map包含date（日期，格式：MM-dd）和count（数量）
     */
    List<Map<String, Object>> getLast7DaysNewUsers();

    /**
     * 查询最近7天每日互动数量
     * @return 最近7天每日互动数量列表，每个Map包含date（日期，格式：MM-dd）和count（数量）
     */
    List<Map<String, Object>> getLast7DaysInteractions();

    /**
     * 查询最近7天每日新增举报数量
     * @return 最近7天每日新增举报数量列表，每个Map包含date（日期，格式：MM-dd）和count（数量）
     */
    List<Map<String, Object>> getLast7DaysNewReports();

    /**
     * 查询各类型举报数量（用于风险雷达图）
     * @return 各类型举报数量列表，每个Map包含reportType（举报类型）和count（数量）
     */
    List<Map<String, Object>> getReportTypeCounts();

    /**
     * 查询各分类帖子数量（用于内容板块热度图）
     * @return 各分类帖子数量列表，每个Map包含categoryName（分类名称）和count（数量）
     */
    List<Map<String, Object>> getCategoryPostCounts();

    /**
     * 查询用户性别统计（用于用户性别构成图）
     * @return 用户性别统计列表，每个Map包含gender（性别：0未知，1男，2女）和count（数量）
     */
    List<Map<String, Object>> getUserGenderCounts();

    /**
     * 查询动态状态统计（用于动态状态统计图）
     * @return 动态状态统计列表，每个Map包含status（状态：0未审核，1已通过，4未通过）和count（数量）
     */
    List<Map<String, Object>> getPostStatusCounts();

    /**
     * 查询最新注册的用户列表（用于大屏底部“最新用户”模块）
     * @return 用户列表，每个Map包含：userId, avatar, username, gender, phone, email, createTime, status
     */
    List<Map<String, Object>> getLatestUsers();

    /**
     * 查询封禁记录列表（用于大屏底部“封禁记录”模块）
     * @return 封禁记录列表，每个Map包含：historyId, adminName, bannedUserName, reason, startTime, createTime
     */
    List<Map<String, Object>> getBanRecords();
}

