package com.app.service.Impl;

import com.app.dao.SPostMapper;
import com.app.pojo.SPostVO;
import com.app.service.SPostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 安卓端帖子Service实现类
 */
@Service
@Transactional
public class SPostServiceImpl implements SPostService {

    @Autowired
    private SPostMapper sPostMapper;

    @Override
    public SPostVO getPostById(Integer postId, Integer currentUserId) {
        return sPostMapper.selectPostById(postId, currentUserId);
    }

    @Override
    public List<SPostVO> getPostList(Map<String, Object> params) {
        return sPostMapper.selectPostList(params);
    }

    @Override
    public Integer publishPost(Map<String, Object> params) {
        // 插入帖子（useGeneratedKeys="true" keyProperty="postId"会自动将生成的ID放入params）
        int result = sPostMapper.insertPost(params);
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
            
            if (postId != null) {
                // 处理标签关联
                String tagName = (String) params.get("tagName");
                if (tagName != null && !tagName.trim().isEmpty()) {
                    Integer tagId = sPostMapper.selectTagIdByName(tagName);
                    if (tagId != null) {
                        sPostMapper.insertPostTag(postId, tagId);
                    }
                }
                
                return postId;
            }
        }
        return null;
    }

    @Override
    public void updateViewCount(Integer postId) {
        sPostMapper.updateViewCount(postId);
    }

    @Override
    public int countPosts(Map<String, Object> params) {
        return sPostMapper.countPosts(params);
    }
}

