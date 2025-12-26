package com.app.service;

import java.util.Map;

/**
 * 安卓端二手集市Service接口
 */
public interface WSecondHandService {
    /**
     * 获取二手商品列表
     * @param page 页码
     * @param pageSize 每页数量
     * @param tagId 标签ID（可选）
     * @return 商品列表
     */
    Map<String, Object> getItems(Integer page, Integer pageSize, Integer tagId, String sortType);
    
    /**
     * 获取单个商品详情
     * @param itemId 商品ID
     * @return 商品详情
     */
    Map<String, Object> getItem(Integer itemId);
    
    /**
     * 增加商品浏览量
     * @param itemId 商品ID
     */
    void incrementViewCount(Integer itemId);
}

