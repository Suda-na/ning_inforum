package com.app.service;

import java.util.List;
import java.util.Map;

/**
 * 安卓端标签Service接口
 */
public interface WTagService {
    /**
     * 根据分类ID获取标签列表
     * @param categoryId 分类ID
     * @return 标签列表
     */
    List<Map<String, Object>> getTagsByCategory(Integer categoryId);
}

