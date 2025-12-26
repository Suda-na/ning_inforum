package com.app.service.impl;

import com.app.dao.SPostMapper;
import com.app.service.SPostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

/**
 * 安卓端帖子Service实现类（仅包含发布功能）
 */
@Service
@Transactional
public class SPostServiceImpl implements SPostService {

    @Autowired
    private SPostMapper sPostMapper;

    @Override
    public Integer publishPost(Map<String, Object> params) {
        try {
            System.out.println("SPostServiceImpl.publishPost - 开始发布帖子，参数: " + params);
            
            // 插入帖子（useGeneratedKeys="true" keyProperty="postId"会自动将生成的ID放入params）
            int result = sPostMapper.insertPost(params);
            System.out.println("SPostServiceImpl.publishPost - 插入帖子结果: " + result);
            
            if (result > 0) {
                // 从params中获取自动生成的postId（可能是BigInteger类型，需要安全转换）
                Object postIdObj = params.get("postId");
                Integer postId = null;
                if (postIdObj != null) {
                    if (postIdObj instanceof Integer) {
                        postId = (Integer) postIdObj;
                    } else if (postIdObj instanceof java.math.BigInteger) {
                        postId = ((java.math.BigInteger) postIdObj).intValue();
                    } else if (postIdObj instanceof Number) {
                        postId = ((Number) postIdObj).intValue();
                    }
                }
                
                System.out.println("SPostServiceImpl.publishPost - 获取到的postId: " + postId);
                
                if (postId != null) {
                    // 处理标签关联
                    String tagName = (String) params.get("tagName");
                    if (tagName != null && !tagName.trim().isEmpty()) {
                        System.out.println("SPostServiceImpl.publishPost - 处理标签: " + tagName);
                        Integer tagId = sPostMapper.selectTagIdByName(tagName);
                        System.out.println("SPostServiceImpl.publishPost - 查询到的tagId: " + tagId);
                        if (tagId != null) {
                            int tagResult = sPostMapper.insertPostTag(postId, tagId);
                            System.out.println("SPostServiceImpl.publishPost - 插入标签关联结果: " + tagResult);
                        } else {
                            System.out.println("SPostServiceImpl.publishPost - 警告: 标签不存在: " + tagName);
                        }
                    }
                    
                    System.out.println("SPostServiceImpl.publishPost - 发布成功，postId: " + postId);
                    return postId;
                } else {
                    System.err.println("SPostServiceImpl.publishPost - 错误: 无法获取postId");
                }
            } else {
                System.err.println("SPostServiceImpl.publishPost - 错误: 插入帖子失败，result=" + result);
            }
        } catch (Exception e) {
            System.err.println("SPostServiceImpl.publishPost - 异常: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }
}

