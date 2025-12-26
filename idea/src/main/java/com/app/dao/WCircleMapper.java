package com.app.dao;

import com.niit.pojo.PostVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 安卓端圈子Mapper接口
 */
@Mapper
public interface WCircleMapper {
    /**
     * 根据条件查询帖子列表
     * @param params 查询参数
     * @return 帖子列表
     */
    List<PostVO> selectPostsByCondition(Map<String, Object> params);

    /**
     * 根据条件统计帖子数量
     * @param params 查询参数
     * @return 帖子数量
     */
    int countPostsByCondition(Map<String, Object> params);

    /**
     * 根据ID查询帖子详情
     * @param postId 帖子ID
     * @return 帖子详情
     */
    PostVO selectPostById(@Param("postId") Integer postId);

    /**
     * 根据帖子ID查询标签名称列表
     * @param postId 帖子ID
     * @return 标签名称列表
     */
    List<String> selectTagNamesByPostId(@Param("postId") Integer postId);

    /**
     * 根据帖子ID查询评论列表
     * @param postId 帖子ID
     * @return 评论列表
     */
    List<Map<String, Object>> selectCommentsByPostId(@Param("postId") Integer postId);

    /**
     * 查询浏览量最高的帖子
     * @param limit 返回数量
     * @return 帖子列表
     */
    List<PostVO> selectHotPosts(@Param("limit") Integer limit);

    /**
     * 增加帖子浏览量
     * @param postId 帖子ID
     */
    void incrementViewCount(@Param("postId") Integer postId);

    /**
     * 添加评论
     * @param postId 帖子ID
     * @param userId 用户ID
     * @param content 评论内容
     * @param parentId 父评论ID（可选）
     * @return 影响的行数
     */
    int insertComment(@Param("postId") Integer postId, 
                      @Param("userId") Integer userId, 
                      @Param("content") String content, 
                      @Param("parentId") Integer parentId);

    /**
     * 获取最后插入的评论ID
     * @return 评论ID
     */
    int getLastInsertCommentId();

    /**
     * 根据帖子ID和用户ID查询评论ID（用于获取已存在的评论记录）
     * @param postId 帖子ID
     * @param userId 用户ID
     * @return 评论ID，如果不存在返回null
     */
    Integer getCommentIdByPostAndUser(@Param("postId") Integer postId, @Param("userId") Integer userId);

    /**
     * 更新帖子评论数
     * @param postId 帖子ID
     */
    void incrementCommentCount(@Param("postId") Integer postId);

    /**
     * 检查用户是否已点赞（只查询status=1的）
     * @param postId 帖子ID
     * @param userId 用户ID
     * @return 点赞记录ID，如果未点赞返回null
     */
    Integer checkLikeExists(@Param("postId") Integer postId, @Param("userId") Integer userId);

    /**
     * 检查是否存在点赞记录（包括status=0的）
     * @param postId 帖子ID
     * @param userId 用户ID
     * @return 点赞记录ID，如果不存在返回null
     */
    Integer checkLikeRecordExists(@Param("postId") Integer postId, @Param("userId") Integer userId);

    /**
     * 添加点赞
     * @param postId 帖子ID
     * @param userId 用户ID
     */
    void insertLike(@Param("postId") Integer postId, @Param("userId") Integer userId);

    /**
     * 恢复点赞（将status从0改为1）
     * @param postId 帖子ID
     * @param userId 用户ID
     */
    void restoreLike(@Param("postId") Integer postId, @Param("userId") Integer userId);

    /**
     * 取消点赞
     * @param postId 帖子ID
     * @param userId 用户ID
     */
    void deleteLike(@Param("postId") Integer postId, @Param("userId") Integer userId);

    /**
     * 更新帖子点赞数
     * @param postId 帖子ID
     * @param increment 增量（1或-1）
     */
    void updateLikeCount(@Param("postId") Integer postId, @Param("increment") int increment);

    /**
     * 统计用户数量
     * @return 用户数量
     */
    int countUsers();

    /**
     * 添加收藏
     * @param postId 帖子ID
     * @param userId 用户ID
     */
    void insertFavorite(@Param("postId") Integer postId, @Param("userId") Integer userId);

    /**
     * 检查用户是否已收藏
     * @param postId 帖子ID
     * @param userId 用户ID
     * @return 收藏记录ID，如果未收藏返回null
     */
    Integer checkFavoriteExists(@Param("postId") Integer postId, @Param("userId") Integer userId);

    /**
     * 更新帖子收藏数
     * @param postId 帖子ID
     * @param increment 增量（1或-1）
     */
    void updateFavoriteCount(@Param("postId") Integer postId, @Param("increment") int increment);

    /**
     * 检查是否存在收藏记录（包括status=0的）
     * @param postId 帖子ID
     * @param userId 用户ID
     * @return 收藏记录ID，如果不存在返回null
     */
    Integer checkFavoriteRecordExists(@Param("postId") Integer postId, @Param("userId") Integer userId);

    /**
     * 取消收藏（将status从1改为0）
     * @param postId 帖子ID
     * @param userId 用户ID
     */
    void deleteFavorite(@Param("postId") Integer postId, @Param("userId") Integer userId);

    /**
     * 恢复收藏（将status从0改为1）
     * @param postId 帖子ID
     * @param userId 用户ID
     */
    void restoreFavorite(@Param("postId") Integer postId, @Param("userId") Integer userId);

    /**
     * 根据用户ID查询收藏的帖子列表
     * @param userId 用户ID
     * @param start 起始位置
     * @param size 数量
     * @return 帖子列表
     */
    List<PostVO> selectFavoritePostsByUserId(@Param("userId") Integer userId, @Param("start") Integer start, @Param("size") Integer size);

    /**
     * 统计用户收藏的帖子数量
     * @param userId 用户ID
     * @return 收藏数量
     */
    int countFavoritePostsByUserId(@Param("userId") Integer userId);
}

