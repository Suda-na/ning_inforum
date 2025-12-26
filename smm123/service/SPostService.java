package com.app.service;

import com.app.pojo.SPostVO;

import java.util.List;
import java.util.Map;

/**
 * 安卓端帖子Service接口
 */
public interface SPostService {
    /**
     * 根据ID查询帖子详情
     * @param postId 帖子ID
     * @param currentUserId 当前用户ID
     * @return 帖子VO对象
     */
    SPostVO getPostById(Integer postId, Integer currentUserId);

    /**
     * 查询帖子列表（分页）
     * @param params 查询参数
     * @return 帖子VO列表
     */
    List<SPostVO> getPostList(Map<String, Object> params);

    /**
     * 发布帖子
     * @param params 帖子参数
     * @return 发布的帖子ID
     */
    Integer publishPost(Map<String, Object> params);

    /**
     * 更新帖子查看次数
     * @param postId 帖子ID
     */
    void updateViewCount(Integer postId);

    /**
     * 统计帖子数量
     * @param params 查询参数
     * @return 帖子数量
     */
    int countPosts(Map<String, Object> params);
}

