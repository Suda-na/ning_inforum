package com.app.service;

import java.util.Map;

/**
 * 安卓端跑腿Service接口
 */
public interface WErrandService {
    /**
     * 获取跑腿订单列表
     * @param status 状态：unaccepted(未接单), completed(已完成)
     * @param page 页码
     * @param pageSize 每页数量
     * @return 订单列表
     */
    Map<String, Object> getOrders(String status, Integer page, Integer pageSize);
    
    /**
     * 检查用户是否是跑腿员
     * @param userId 用户ID
     * @return 是否是跑腿员
     */
    boolean isErrandRunner(Integer userId);
    
    /**
     * 获取跑腿订单详情
     * @param orderId 订单ID（帖子ID）
     * @return 订单详情
     */
    Map<String, Object> getOrderDetail(Integer orderId);
}

