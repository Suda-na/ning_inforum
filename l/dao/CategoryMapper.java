package com.niit.dao;

import com.niit.pojo.Category;
import java.util.List;

/**
 * 分类Mapper接口
 */
public interface CategoryMapper {
    
    /**
     * 查询所有分类列表
     * @return 分类列表
     */
    List<Category> selectAllCategories();
    
    /**
     * 根据ID查询分类
     * @param categoryId 分类ID
     * @return 分类对象
     */
    Category selectCategoryById(Integer categoryId);
    
    /**
     * 根据名称查询分类（用于模块名称映射）
     * @param name 分类名称
     * @return 分类对象
     */
    Category selectCategoryByName(String name);
}

