package com.app.dao;

import com.niit.pojo.PostVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 安卓端跑腿Mapper接口
 */
@Mapper
public interface WErrandMapper {
    /**
     * 查询跑腿订单列表
     * @param statusList 状态列表（0或1=未接单，3=已完成）
     * @param start 起始位置
     * @param size 数量
     * @return 订单列表
     */
    List<PostVO> selectErrandOrders(@Param("statusList") List<Integer> statusList,
                                    @Param("start") Integer start,
                                    @Param("size") Integer size);

    /**
     * 统计跑腿订单数量
     * @param statusList 状态列表
     * @return 数量
     */
    int countErrandOrders(@Param("statusList") List<Integer> statusList);

    /**
     * 根据ID查询跑腿订单详情
     * @param postId 帖子ID
     * @return 订单详情
     */
    PostVO selectErrandOrderById(@Param("postId") Integer postId);
    
    /**
     * 更新帖子状态为进行中（status=5）
     * @param postId 帖子ID
     * @return 更新行数
     */
    int updatePostStatusToInProgress(@Param("postId") Integer postId);
    
    /**
     * 创建trade_task记录
     * @param taskNo 任务编号
     * @param postId 帖子ID
     * @param creatorId 创建者ID
     * @param acceptorId 接受者ID
     * @param title 标题
     * @param description 描述
     * @param amount 金额
     * @param startLocation 起点
     * @param endLocation 终点
     * @return 插入行数
     */
    int insertTradeTask(@Param("taskNo") String taskNo,
                       @Param("postId") Integer postId,
                       @Param("creatorId") Integer creatorId,
                       @Param("acceptorId") Integer acceptorId,
                       @Param("title") String title,
                       @Param("description") String description,
                       @Param("amount") java.math.BigDecimal amount,
                       @Param("startLocation") String startLocation,
                       @Param("endLocation") String endLocation);
}

