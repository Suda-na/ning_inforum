package com.app.dao;

import com.niit.pojo.PostVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 安卓端二手集市Mapper接口
 */
@Mapper
public interface WSecondHandMapper {
    List<PostVO> selectSecondHandPosts(Map<String, Object> params);
    int countSecondHandPosts(Map<String, Object> params);
    PostVO selectSecondHandPostById(@Param("postId") Integer postId);
    void incrementViewCount(@Param("postId") Integer postId);
}

