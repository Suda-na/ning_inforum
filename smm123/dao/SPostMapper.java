package com.app.dao;

import com.app.pojo.SPostVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 安卓端帖子Mapper接口
 */
@Mapper
public interface SPostMapper {
    /**
     * 根据ID查询帖子详情
     * @param postId 帖子ID
     * @param currentUserId 当前用户ID
     * @return 帖子VO对象
     */
    SPostVO selectPostById(@Param("postId") Integer postId, @Param("currentUserId") Integer currentUserId);

    /**
     * 查询帖子列表（分页）
     * @param params 查询参数
     * @return 帖子VO列表
     */
    List<SPostVO> selectPostList(Map<String, Object> params);

    /**
     * 统计帖子数量
     * @param params 查询参数
     * @return 帖子数量
     */
    int countPosts(Map<String, Object> params);

    /**
     * 插入新帖子
     * @param params 帖子参数
     * @return 影响的行数
     */
    int insertPost(Map<String, Object> params);

    /**
     * 更新帖子查看次数
     * @param postId 帖子ID
     * @return 影响的行数
     */
    int updateViewCount(@Param("postId") Integer postId);

    /**
     * 插入帖子标签关联
     * @param postId 帖子ID
     * @param tagId 标签ID
     * @return 影响的行数
     */
    int insertPostTag(@Param("postId") Integer postId, @Param("tagId") Integer tagId);

    /**
     * 根据帖子ID查询标签列表
     * @param postId 帖子ID
     * @return 标签名称列表
     */
    List<String> selectTagsByPostId(@Param("postId") Integer postId);

    /**
     * 根据标签名称查询标签ID
     * @param tagName 标签名称
     * @return 标签ID
     */
    Integer selectTagIdByName(@Param("tagName") String tagName);
}

