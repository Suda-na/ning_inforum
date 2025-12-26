package com.niit.service;

import com.niit.pojo.Post;
import com.niit.pojo.PostVO;

import java.util.List;
import java.util.Map;

/**
 * 帖子Service接口
 */
public interface PostService {
    /**
     * 根据ID查询帖子详情
     * @param postId 帖子ID
     * @return 帖子VO对象
     */
    PostVO getPostById(Integer postId);

    /**
     * 查询所有帖子
     * @return 帖子VO列表
     */
    List<PostVO> getAllPosts();

    /**
     * 根据条件查询帖子（分页）
     * @param params 查询参数
     * @return 帖子VO列表
     */
    List<PostVO> searchPosts(Map<String, Object> params);

    /**
     * 根据条件统计帖子数量
     * @param params 查询参数
     * @return 帖子数量
     */
    int countPosts(Map<String, Object> params);

    /**
     * 更新帖子状态
     * @param postId 帖子ID
     * @param status 状态：0待审核, 1已通过, 2已删除, 3已结束
     * @return 是否成功
     */
    boolean updatePostStatus(Integer postId, Integer status);

    /**
     * 根据状态统计帖子数量
     * @param status 状态：0待审核, 1已通过, 2已删除, 3已结束
     * @return 帖子数量
     */
    int countPostsByStatus(Integer status);
}

