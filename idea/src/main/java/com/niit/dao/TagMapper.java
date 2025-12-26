package com.niit.dao;

import com.niit.pojo.Tag;
import org.apache.ibatis.annotations.Param;
import java.util.List;

/**
 * 标签Mapper接口
 */
public interface TagMapper {
    
    /**
     * 查询所有标签列表（带分类信息）
     * @param tagName 标签名称（可选，用于搜索）
     * @param categoryId 分类ID（可选，用于筛选）
     * @return 标签列表
     */
    List<Tag> selectAllTags(@Param("tagName") String tagName, 
                            @Param("categoryId") Integer categoryId);
    
    /**
     * 根据ID查询标签
     * @param tagId 标签ID
     * @return 标签对象
     */
    Tag selectTagById(@Param("tagId") Integer tagId);
    
    /**
     * 插入标签
     * @param tag 标签对象
     * @return 影响的行数
     */
    int insertTag(Tag tag);
    
    /**
     * 更新标签
     * @param tag 标签对象
     * @return 影响的行数
     */
    int updateTag(Tag tag);
    
    /**
     * 删除标签
     * @param tagId 标签ID
     * @return 影响的行数
     */
    int deleteTag(@Param("tagId") Integer tagId);
    
    /**
     * 批量删除标签
     * @param tagIds 标签ID列表
     * @return 影响的行数
     */
    int batchDeleteTags(@Param("tagIds") List<Integer> tagIds);
    
    /**
     * 根据名称查询标签（用于检查重复）
     * @param name 标签名称
     * @return 标签对象
     */
    Tag selectTagByName(@Param("name") String name);
    
    /**
     * 插入分类标签关联
     * @param categoryId 分类ID
     * @param tagId 标签ID
     * @return 影响的行数
     */
    int insertCategoryTag(@Param("categoryId") Integer categoryId, 
                         @Param("tagId") Integer tagId);
    
    /**
     * 删除标签的分类关联
     * @param tagId 标签ID
     * @return 影响的行数
     */
    int deleteCategoryTagByTagId(@Param("tagId") Integer tagId);
    
    /**
     * 批量删除标签的分类关联
     * @param tagIds 标签ID列表
     * @return 影响的行数
     */
    int batchDeleteCategoryTags(@Param("tagIds") List<Integer> tagIds);
    
    /**
     * 根据标签ID查询分类ID
     * @param tagId 标签ID
     * @return 分类ID
     */
    Integer selectCategoryIdByTagId(@Param("tagId") Integer tagId);
}

