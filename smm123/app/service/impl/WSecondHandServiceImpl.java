package com.app.service.impl;

import com.app.dao.WSecondHandMapper;
import com.app.service.WSecondHandService;
import com.niit.pojo.PostVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 安卓端二手集市Service实现类
 */
@Service
public class WSecondHandServiceImpl implements WSecondHandService {

    @Autowired
    private WSecondHandMapper secondHandMapper;

    @Override
    public Map<String, Object> getItems(Integer page, Integer pageSize, Integer tagId, String sortType) {
        Map<String, Object> result = new HashMap<>();

        Map<String, Object> params = new HashMap<>();
        params.put("categoryId", 3); // 二手集市分类ID
        if (tagId != null) {
            params.put("tagId", tagId);
        }
        // 排序类型：newest(新发-按创建时间倒序), default(默认)
        if ("newest".equals(sortType)) {
            params.put("sortType", "newest");
        }

        int start = (page - 1) * pageSize;
        params.put("start", start);
        params.put("size", pageSize);

        List<PostVO> posts = secondHandMapper.selectSecondHandPosts(params);
        int total = secondHandMapper.countSecondHandPosts(params);
        int totalPages = (int) Math.ceil((double) total / pageSize);

        List<Map<String, Object>> itemList = new ArrayList<>();
        for (PostVO post : posts) {
            Map<String, Object> itemMap = convertPostToItemMap(post);
            itemList.add(itemMap);
        }

        result.put("items", itemList);
        result.put("total", total);
        result.put("page", page);
        result.put("pageSize", pageSize);
        result.put("totalPages", totalPages);

        return result;
    }

    @Override
    public Map<String, Object> getItem(Integer itemId) {
        // 增加浏览量
        secondHandMapper.incrementViewCount(itemId);
        
        PostVO post = secondHandMapper.selectSecondHandPostById(itemId);
        if (post == null) {
            return null;
        }
        return convertPostToItemMap(post);
    }

    @Override
    public void incrementViewCount(Integer itemId) {
        secondHandMapper.incrementViewCount(itemId);
    }

    private Map<String, Object> convertPostToItemMap(PostVO post) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", String.valueOf(post.getPostId()));
        map.put("postId", post.getPostId());
        map.put("title", post.getTitle() != null ? post.getTitle() : "");
        map.put("desc", post.getContent() != null ? post.getContent() : "");
        map.put("price", post.getPrice() != null ? post.getPrice().toString() : "0.00");
        map.put("tag", "二手");
        map.put("publisher", post.getUsername() != null ? post.getUsername() : "匿名");
        map.put("sellerId", post.getUserId()); // 添加卖家ID
        String avatar = post.getAvatar() != null ? post.getAvatar() : "";
        System.out.println("二手集市转换: postId=" + post.getPostId() + ", username=" + post.getUsername() + ", avatar=" + avatar + ", userId=" + post.getUserId());
        map.put("avatar", avatar);
        map.put("time", formatTime(post.getCreateTime()));
        map.put("views", post.getViewCount() != null ? post.getViewCount() : 0);

        List<String> images = new ArrayList<>();
        if (post.getImage1() != null && !post.getImage1().isEmpty()) {
            images.add(post.getImage1());
        }
        if (post.getImage2() != null && !post.getImage2().isEmpty()) {
            images.add(post.getImage2());
        }
        if (post.getImage3() != null && !post.getImage3().isEmpty()) {
            images.add(post.getImage3());
        }
        map.put("images", images);

        return map;
    }

    private String formatTime(Date date) {
        if (date == null) {
            return "刚刚";
        }

        long now = System.currentTimeMillis();
        long time = date.getTime();
        long diff = now - time;

        long seconds = diff / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;
        long days = hours / 24;

        if (seconds < 60) {
            return "刚刚";
        } else if (minutes < 60) {
            return minutes + "分钟前";
        } else if (hours < 24) {
            return hours + "小时前";
        } else if (days < 7) {
            return days + "天前";
        } else {
            SimpleDateFormat sdf = new SimpleDateFormat("MM-dd HH:mm");
            return sdf.format(date);
        }
    }
}

