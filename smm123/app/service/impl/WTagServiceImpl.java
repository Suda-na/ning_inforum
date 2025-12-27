package com.app.service.impl;

import com.app.dao.WTagMapper;
import com.app.service.WTagService;
import com.niit.pojo.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 安卓端标签Service实现类
 */
@Service
public class WTagServiceImpl implements WTagService {

    @Autowired
    private WTagMapper tagMapper;

    @Override
    public List<Map<String, Object>> getTagsByCategory(Integer categoryId) {
        List<Tag> tags = tagMapper.selectTagsByCategory(categoryId);
        List<Map<String, Object>> result = new ArrayList<>();
        
        for (Tag tag : tags) {
            Map<String, Object> map = new HashMap<>();
            map.put("tagId", tag.getTagId());
            map.put("name", tag.getName());
            map.put("categoryId", tag.getCategoryId());
            map.put("categoryName", tag.getCategoryName());
            result.add(map);
        }
        
        return result;
    }
}

