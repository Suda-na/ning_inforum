package com.niit.service.impl;

import com.niit.dao.CategoryMapper;
import com.niit.pojo.Category;
import com.niit.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 分类服务实现类
 */
@Service
public class CategoryServiceImpl implements CategoryService {
    
    @Autowired
    private CategoryMapper categoryMapper;
    
    @Override
    public List<Category> getAllCategories() {
        return categoryMapper.selectAllCategories();
    }
    
    @Override
    public Category getCategoryById(Integer categoryId) {
        if (categoryId == null) {
            return null;
        }
        return categoryMapper.selectCategoryById(categoryId);
    }
    
    @Override
    public Category getCategoryByName(String name) {
        if (name == null || name.trim().isEmpty()) {
            return null;
        }
        return categoryMapper.selectCategoryByName(name.trim());
    }
}

