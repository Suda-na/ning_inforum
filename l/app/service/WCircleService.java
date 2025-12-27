package com.app.service;

import java.util.List;
import java.util.Map;

/**
 * 安卓端圈子Service接口
 */
public interface WCircleService {
    /**
     * 获取圈子帖子列表
     * @param categoryId 分类ID
     * @param page 页码
     * @param pageSize 每页数量
     * @param tagId 标签ID（可选）
     * @param sortType 排序类型
     * @return 帖子列表数据
     */
    Map<String, Object> getPosts(Integer categoryId, Integer page, Integer pageSize, Integer tagId, String sortType);

    /**
     * 获取圈子帖子列表（支持拉黑过滤）
     * @param categoryId 分类ID
     * @param page 页码
     * @param pageSize 每页数量
     * @param tagId 标签ID（可选）
     * @param sortType 排序类型
     * @param userId 用户ID（可选，用于拉黑过滤和点赞状态查询）
     * @return 帖子列表数据
     */
    Map<String, Object> getPosts(Integer categoryId, Integer page, Integer pageSize, Integer tagId, String sortType, Integer userId);

    /**
     * 获取帖子详情
     * @param postId 帖子ID
     * @param userId 用户ID（用于查询点赞状态）
     * @return 帖子详情
     */
    Map<String, Object> getPostDetail(Integer postId, Integer userId);

    /**
     * 获取帖子评论列表
     * @param postId 帖子ID
     * @return 评论列表
     */
    Map<String, Object> getPostComments(Integer postId);

    /**
     * 获取浏览量最高的帖子（用于今日最热）
     * @param limit 返回数量
     * @return 帖子列表
     */
    Map<String, Object> getHotPosts(Integer limit);

    /**
     * 增加帖子浏览量
     * @param postId 帖子ID
     */
    void incrementViewCount(Integer postId);

    /**
     * 添加评论
     * @param postId 帖子ID
     * @param userId 用户ID
     * @param content 评论内容
     * @param parentId 父评论ID（可选）
     * @return 评论ID
     */
    Map<String, Object> addComment(Integer postId, Integer userId, String content, Integer parentId);

    /**
     * 点赞/取消点赞
     * @param postId 帖子ID
     * @param userId 用户ID
     * @return 是否已点赞
     */
    Map<String, Object> toggleLike(Integer postId, Integer userId);

    /**
     * 获取统计数据（动态数量、用户数量）
     * @return 统计数据
     */
    Map<String, Object> getStatistics();

    /**
     * 添加收藏
     * @param postId 帖子ID
     * @param userId 用户ID
     * @return 操作结果
     */
    Map<String, Object> addFavorite(Integer postId, Integer userId);

    /**
     * 获取用户的收藏列表
     * @param userId 用户ID
     * @param page 页码
     * @param pageSize 每页数量
     * @return 收藏列表
     */
    Map<String, Object> getFavoritePosts(Integer userId, Integer page, Integer pageSize);
    
    /**
     * 删除评论
     * @param commentId 评论ID
     * @param userId 用户ID（用于验证权限）
     * @return 操作结果
     */
    Map<String, Object> deleteComment(Integer commentId, Integer userId);
    
    /**
     * 获取用户的评论列表
     * @param userId 用户ID
     * @return 评论列表
     */
    List<Map<String, Object>> getUserComments(Integer userId);
    
    /**
     * 获取用户的跑腿订单列表
     * @param userId 用户ID
     * @return 订单列表
     */
    List<Map<String, Object>> getUserOrders(Integer userId);
    
    /**
     * 获取用户发布的帖子列表
     * @param userId 用户ID
     * @param categoryId 分类ID（1=动态）
     * @return 帖子列表
     */
    List<Map<String, Object>> getUserPosts(Integer userId, Integer categoryId);
}

