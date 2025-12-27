package com.app.service;

import java.util.Map;

/**
 * 安卓端搜索Service接口
 */
public interface WSearchService {
    /**
     * 搜索帖子
     * @param keyword 关键词
     * @param categoryId 分类ID
     * @param page 页码
     * @param pageSize 每页数量
     * @return 搜索结果
     */
    Map<String, Object> searchPosts(String keyword, Integer categoryId, Integer page, Integer pageSize);

    /**
     * 搜索用户
     * @param keyword 关键词
     * @param page 页码
     * @param pageSize 每页数量
     * @return 搜索结果
     */
    Map<String, Object> searchUsers(String keyword, Integer page, Integer pageSize);
}

