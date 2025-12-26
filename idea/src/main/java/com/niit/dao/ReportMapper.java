package com.niit.dao;

import com.niit.pojo.Report;
import com.niit.pojo.ReportVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 举报DAO接口
 */
@Mapper
public interface ReportMapper {
    /**
     * 根据ID查询举报信息
     * @param reportId 举报ID
     * @return 举报对象
     */
    ReportVO selectByPrimaryKey(Integer reportId);

    /**
     * 查询所有举报信息
     * @return 举报列表
     */
    List<ReportVO> selectAll();

    /**
     * 根据条件查询举报信息
     * @param params 查询参数，可以是Report对象或Map
     * @return 举报列表
     */
    List<ReportVO> selectByCondition(Object params);

    /**
     * 插入举报信息
     * @param report 举报对象
     * @return 插入的行数
     */
    int insert(Report report);

    /**
     * 更新举报信息
     * @param report 举报对象
     * @return 更新的行数
     */
    int updateByPrimaryKey(Report report);

    /**
     * 删除举报信息
     * @param reportId 举报ID
     * @return 删除的行数
     */
    int deleteByPrimaryKey(Integer reportId);

    /**
     * 根据状态查询举报信息
     * @param status 状态：0待处理, 1已处理
     * @return 举报列表
     */
    List<ReportVO> selectByStatus(@Param("status") Integer status);

    /**
     * 根据目标类型查询举报信息
     * @param targetType 目标类型：用户, 帖子, 评论
     * @return 举报列表
     */
    List<ReportVO> selectByTargetType(@Param("targetType") String targetType);

    /**
     * 根据举报类型查询举报信息
     * @param reportType 举报类型：垃圾信息, 色情低俗, 违法违规, 欺诈, 侵权, 其他
     * @return 举报列表
     */
    List<ReportVO> selectByReportType(@Param("reportType") String reportType);

    /**
     * 统计举报数量
     * @return 举报总数
     */
    int countAll();

    /**
     * 根据状态统计举报数量
     * @param status 状态：0待处理, 1已处理
     * @return 举报数量
     */
    int countByStatus(@Param("status") Integer status);

    /**
     * 按日期统计举报数量（按目标类型分组）
     * @param targetType 目标类型：帖子、评论、用户
     * @param interval 时间间隔：day（日）、month（月）、year（年）
     * @param count 返回的数据点数量
     * @return 统计数据列表，每个元素包含日期和数量
     */
    List<java.util.Map<String, Object>> countReportsByDate(
            @Param("targetType") String targetType,
            @Param("interval") String interval,
            @Param("count") Integer count);
}