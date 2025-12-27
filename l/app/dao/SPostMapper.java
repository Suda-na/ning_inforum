package com.app.dao;

import com.app.pojo.SPostVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 安卓端帖子Mapper接口（仅包含发布功能）
 */
@Mapper
public interface SPostMapper {
    /**
     * 插入新帖子
     * @param params 帖子参数
     * @return 影响的行数
     */
    int insertPost(Map<String, Object> params);

    /**
     * 插入帖子标签关联
     * @param postId 帖子ID
     * @param tagId 标签ID
     * @return 影响的行数
     */
    int insertPostTag(@Param("postId") Integer postId, @Param("tagId") Integer tagId);

    /**
     * 根据标签名称查询标签ID
     * @param tagName 标签名称
     * @return 标签ID
     */
    Integer selectTagIdByName(@Param("tagName") String tagName);
}

