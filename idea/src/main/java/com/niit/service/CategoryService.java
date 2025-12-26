package com.niit.service;

import com.niit.pojo.Category;
import java.util.List;

/**
 * 分类服务接口
 */
public interface CategoryService {
    
    /**
     * 查询所有分类列表
     * @return 分类列表
     */
    List<Category> getAllCategories();
    
    /**
     * 根据ID查询分类
     * @param categoryId 分类ID
     * @return 分类对象
     */
    Category getCategoryById(Integer categoryId);
    
    /**
     * 根据名称查询分类
     * @param name 分类名称
     * @return 分类对象
     */
    Category getCategoryByName(String name);
}

