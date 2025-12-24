package com.niit.service;

import com.niit.pojo.Tag;
import java.util.List;

/**
 * 标签服务接口
 */
public interface TagService {
    
    /**
     * 查询所有标签列表（带分类信息）
     * @param tagName 标签名称（可选，用于搜索）
     * @param categoryId 分类ID（可选，用于筛选）
     * @return 标签列表
     */
    List<Tag> getAllTags(String tagName, Integer categoryId);
    
    /**
     * 根据ID查询标签
     * @param tagId 标签ID
     * @return 标签对象
     */
    Tag getTagById(Integer tagId);
    
    /**
     * 添加标签
     * @param tagName 标签名称
     * @param categoryId 分类ID（可选）
     * @return 是否成功
     */
    boolean addTag(String tagName, Integer categoryId);
    
    /**
     * 更新标签
     * @param tagId 标签ID
     * @param tagName 标签名称
     * @param categoryId 分类ID（可选）
     * @return 是否成功
     */
    boolean updateTag(Integer tagId, String tagName, Integer categoryId);
    
    /**
     * 删除标签
     * @param tagId 标签ID
     * @return 是否成功
     */
    boolean deleteTag(Integer tagId);
    
    /**
     * 批量删除标签
     * @param tagIds 标签ID列表
     * @return 是否成功
     */
    boolean batchDeleteTags(List<Integer> tagIds);
}

