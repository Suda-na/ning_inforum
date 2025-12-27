package com.app.dao;

import com.niit.pojo.PostVO;
import com.niit.pojo.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 安卓端搜索Mapper接口
 */
@Mapper
public interface WSearchMapper {
    /**
     * 搜索帖子
     * @param keyword 关键词
     * @param categoryId 分类ID
     * @param start 起始位置
     * @param size 数量
     * @return 帖子列表
     */
    List<PostVO> searchPosts(@Param("keyword") String keyword,
                             @Param("categoryId") Integer categoryId,
                             @Param("start") Integer start,
                             @Param("size") Integer size);

    /**
     * 统计搜索结果帖子数量
     * @param keyword 关键词
     * @param categoryId 分类ID
     * @return 数量
     */
    int countSearchPosts(@Param("keyword") String keyword,
                        @Param("categoryId") Integer categoryId);

    /**
     * 搜索用户
     * @param keyword 关键词
     * @param start 起始位置
     * @param size 数量
     * @return 用户列表
     */
    List<User> searchUsers(@Param("keyword") String keyword,
                           @Param("start") Integer start,
                           @Param("size") Integer size);

    /**
     * 统计搜索结果用户数量
     * @param keyword 关键词
     * @return 数量
     */
    int countSearchUsers(@Param("keyword") String keyword);
}

