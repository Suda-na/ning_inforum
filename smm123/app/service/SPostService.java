package com.app.service;

import com.app.pojo.SPostVO;

import java.util.List;
import java.util.Map;

/**
 * 安卓端帖子Service接口（仅包含发布功能）
 */
public interface SPostService {
    /**
     * 发布帖子
     * @param params 帖子参数
     * @return 发布的帖子ID
     */
    Integer publishPost(Map<String, Object> params);
}

