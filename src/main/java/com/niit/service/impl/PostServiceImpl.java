package com.niit.service.impl;

import com.niit.dao.PostMapper;
import com.niit.pojo.PostVO;
import com.niit.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 帖子Service实现类
 */
@Service
@Transactional
public class PostServiceImpl implements PostService {

    @Autowired
    private PostMapper postMapper;

    @Override
    public PostVO getPostById(Integer postId) {
        return postMapper.selectByPrimaryKey(postId);
    }

    @Override
    public List<PostVO> getAllPosts() {
        return postMapper.selectAll();
    }

    @Override
    public List<PostVO> searchPosts(Map<String, Object> params) {
        return postMapper.selectByCondition(params);
    }

    @Override
    public int countPosts(Map<String, Object> params) {
        return postMapper.countByCondition(params);
    }

    @Override
    public boolean updatePostStatus(Integer postId, Integer status) {
        Date reviewTime = new Date();
        return postMapper.updateStatus(postId, status, reviewTime) > 0;
    }

    @Override
    public int countPostsByStatus(Integer status) {
        return postMapper.countByStatus(status);
    }
}

