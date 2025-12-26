package com.niit.service.impl;

import com.niit.dao.TagMapper;
import com.niit.pojo.Tag;
import com.niit.service.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 标签服务实现类
 */
@Service
public class TagServiceImpl implements TagService {
    
    @Autowired
    private TagMapper tagMapper;
    
    @Override
    public List<Tag> getAllTags(String tagName, Integer categoryId) {
        return tagMapper.selectAllTags(tagName, categoryId);
    }
    
    @Override
    public Tag getTagById(Integer tagId) {
        if (tagId == null) {
            return null;
        }
        return tagMapper.selectTagById(tagId);
    }
    
    @Override
    public boolean addTag(String tagName, Integer categoryId) {
        try {
            if (tagName == null || tagName.trim().isEmpty()) {
                return false;
            }
            
            // 检查标签名称是否已存在
            Tag existingTag = tagMapper.selectTagByName(tagName.trim());
            if (existingTag != null) {
                return false;
            }
            
            Tag tag = new Tag();
            tag.setName(tagName.trim());
            int result = tagMapper.insertTag(tag);
            if (result > 0 && tag.getTagId() != null && categoryId != null) {
                // 插入分类标签关联
                tagMapper.insertCategoryTag(categoryId, tag.getTagId());
            }
            return result > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    @Override
    public boolean updateTag(Integer tagId, String tagName, Integer categoryId) {
        try {
            if (tagId == null || tagName == null || tagName.trim().isEmpty()) {
                return false;
            }
            
            // 检查标签是否存在
            Tag existingTag = tagMapper.selectTagById(tagId);
            if (existingTag == null) {
                return false;
            }
            
            // 如果名称改变，检查新名称是否已存在
            if (!existingTag.getName().equals(tagName.trim())) {
                Tag tagWithSameName = tagMapper.selectTagByName(tagName.trim());
                if (tagWithSameName != null) {
                    return false;
                }
            }
            
            Tag tag = new Tag();
            tag.setTagId(tagId);
            tag.setName(tagName.trim());
            int result = tagMapper.updateTag(tag);
            
            // 更新分类关联
            if (result > 0 && categoryId != null) {
                // 先删除旧的关联
                tagMapper.deleteCategoryTagByTagId(tagId);
                // 再插入新的关联
                tagMapper.insertCategoryTag(categoryId, tagId);
            } else if (result > 0 && categoryId == null) {
                // 如果categoryId为null，删除关联
                tagMapper.deleteCategoryTagByTagId(tagId);
            }
            
            return result > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    @Override
    public boolean deleteTag(Integer tagId) {
        try {
            if (tagId == null) {
                return false;
            }
            // 先删除分类标签关联
            tagMapper.deleteCategoryTagByTagId(tagId);
            // 再删除标签（post_tag关联会通过外键约束自动删除）
            int result = tagMapper.deleteTag(tagId);
            return result > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    @Override
    public boolean batchDeleteTags(List<Integer> tagIds) {
        try {
            if (tagIds == null || tagIds.isEmpty()) {
                return false;
            }
            // 先批量删除分类标签关联
            tagMapper.batchDeleteCategoryTags(tagIds);
            // 再批量删除标签（post_tag关联会通过外键约束自动删除）
            int result = tagMapper.batchDeleteTags(tagIds);
            return result > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}

