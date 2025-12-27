package com.app.service;

import java.util.Map;

/**
 * 安卓端失物招领Service接口
 */
public interface WLostFoundService {
    /**
     * 获取失物招领列表
     * @param type 类型：lost(失物), found(招领)
     * @param page 页码
     * @param pageSize 每页数量
     * @return 列表
     */
    Map<String, Object> getItems(String type, Integer page, Integer pageSize);
    
    /**
     * 获取失物招领详情
     * @param itemId 物品ID（帖子ID）
     * @return 详情
     */
    Map<String, Object> getItemDetail(Integer itemId);
    
    /**
     * 增加失物招领浏览量
     * @param itemId 物品ID（帖子ID）
     */
    void incrementViewCount(Integer itemId);
}

