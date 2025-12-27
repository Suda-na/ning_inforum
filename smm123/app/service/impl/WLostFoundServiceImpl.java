package com.app.service.impl;

import com.app.dao.WLostFoundMapper;
import com.app.dao.WCircleMapper;
import com.app.service.WLostFoundService;
import com.niit.pojo.PostVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 安卓端失物招领Service实现类
 */
@Service
public class WLostFoundServiceImpl implements WLostFoundService {

    @Autowired
    private WLostFoundMapper lostFoundMapper;

    @Autowired
    private WCircleMapper circleMapper;

    @Override
    public Map<String, Object> getItems(String type, Integer page, Integer pageSize) {
        Map<String, Object> result = new HashMap<>();

        Map<String, Object> params = new HashMap<>();
        params.put("categoryId", 4); // 失物招领分类ID
        params.put("type", type); // lost或found，通过标签筛选

        int start = (page - 1) * pageSize;
        params.put("start", start);
        params.put("size", pageSize);

        System.out.println("========== 失物招领查询开始 ==========");
        System.out.println("查询参数: type=" + type + ", params=" + params);
        
        List<PostVO> posts = lostFoundMapper.selectLostFoundPosts(params);
        int total = lostFoundMapper.countLostFoundPosts(params);
        int totalPages = (int) Math.ceil((double) total / pageSize);

        System.out.println("数据库查询结果: 找到 " + posts.size() + " 条记录, 总数=" + total);

        List<Map<String, Object>> itemList = new ArrayList<>();
        for (int i = 0; i < posts.size(); i++) {
            PostVO post = posts.get(i);
            try {
                System.out.println("开始转换第" + (i+1) + "条记录: postId=" + post.getPostId() + ", title=" + post.getTitle());
                Map<String, Object> itemMap = convertPostToItemMap(post);
                itemList.add(itemMap);
                System.out.println("成功转换第" + (i+1) + "条记录: id=" + itemMap.get("id") + ", title=" + itemMap.get("title"));
            } catch (Exception e) {
                System.err.println("转换第" + (i+1) + "条记录失败: postId=" + post.getPostId() + ", error=" + e.getMessage());
                e.printStackTrace();
            }
        }
        System.out.println("转换完成，共转换 " + itemList.size() + " 条记录");
        System.out.println("返回结果: items.size()=" + itemList.size() + ", total=" + total);
        System.out.println("========== 失物招领查询结束 ==========");

        result.put("items", itemList);
        result.put("total", total);
        result.put("page", page);
        result.put("pageSize", pageSize);
        result.put("totalPages", totalPages);

        return result;
    }

    private Map<String, Object> convertPostToItemMap(PostVO post) {
        System.out.println("转换失物招领: postId=" + post.getPostId() + ", title=" + post.getTitle());
        
        Map<String, Object> map = new HashMap<>();
        map.put("id", String.valueOf(post.getPostId()));
        map.put("postId", post.getPostId());
        map.put("title", post.getTitle() != null ? post.getTitle() : "");
        map.put("desc", post.getContent() != null ? post.getContent() : "");
        
        // 根据标签判断是"失物"还是"招领"
        String tag = "失物"; // 默认值
        try {
            List<String> tagNames = circleMapper.selectTagNamesByPostId(post.getPostId());
            System.out.println("帖子 " + post.getPostId() + " 的标签: " + tagNames);
            for (String tagName : tagNames) {
                if ("失物".equals(tagName)) {
                    tag = "失物";
                    break;
                } else if ("招领".equals(tagName)) {
                    tag = "招领";
                    break;
                }
            }
        } catch (Exception e) {
            System.err.println("查询标签失败: postId=" + post.getPostId() + ", error=" + e.getMessage());
            e.printStackTrace();
            // 如果查询标签失败，使用默认值"失物"
        }
        map.put("tag", tag);
        System.out.println("最终标签: " + tag);
        
        map.put("publisher", post.getUsername() != null ? post.getUsername() : "匿名");
        String avatar = post.getAvatar() != null ? post.getAvatar() : "";
        System.out.println("失物招领转换: postId=" + post.getPostId() + ", username=" + post.getUsername() + ", avatar=" + avatar);
        map.put("avatar", avatar);
        map.put("time", formatTime(post.getCreateTime()));
        map.put("location", ""); // 位置信息，暂时为空
        map.put("views", post.getViewCount() != null ? post.getViewCount() : 0);

        List<String> images = new ArrayList<>();
        if (post.getImage1() != null && !post.getImage1().isEmpty()) {
            images.add(post.getImage1());
            System.out.println("添加图片1: " + post.getImage1());
        }
        if (post.getImage2() != null && !post.getImage2().isEmpty()) {
            images.add(post.getImage2());
            System.out.println("添加图片2: " + post.getImage2());
        }
        if (post.getImage3() != null && !post.getImage3().isEmpty()) {
            images.add(post.getImage3());
            System.out.println("添加图片3: " + post.getImage3());
        }
        map.put("images", images);
        System.out.println("失物招领转换完成，图片数量: " + images.size());

        return map;
    }

    @Override
    public Map<String, Object> getItemDetail(Integer itemId) {
        PostVO post = lostFoundMapper.selectLostFoundPostById(itemId);
        if (post == null) {
            return null;
        }
        Map<String, Object> itemMap = convertPostToItemMap(post);
        // 添加更多详情字段
        itemMap.put("contactInfo", post.getContactInfo() != null ? post.getContactInfo() : "");
        itemMap.put("itemInfo", post.getItemInfo() != null ? post.getItemInfo() : "");
        return itemMap;
    }

    @Override
    public void incrementViewCount(Integer itemId) {
        lostFoundMapper.incrementViewCount(itemId);
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

