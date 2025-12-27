package com.app.dao;

import com.niit.pojo.Tag;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 安卓端标签Mapper接口
 */
@Mapper
public interface WTagMapper {
    /**
     * 根据分类ID查询标签列表
     * @param categoryId 分类ID
     * @return 标签列表
     */
    List<Tag> selectTagsByCategory(@Param("categoryId") Integer categoryId);
}

