package com.app.dao;

import com.niit.pojo.PostVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 安卓端失物招领Mapper接口
 */
@Mapper
public interface WLostFoundMapper {
    List<PostVO> selectLostFoundPosts(Map<String, Object> params);
    int countLostFoundPosts(Map<String, Object> params);
    
    /**
     * 根据ID查询失物招领详情
     * @param postId 帖子ID
     * @return 详情
     */
    PostVO selectLostFoundPostById(@Param("postId") Integer postId);
    
    /**
     * 增加失物招领浏览量
     * @param postId 帖子ID
     */
    void incrementViewCount(@Param("postId") Integer postId);
}

