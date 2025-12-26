package com.app.service.impl;

import com.app.dao.WCircleMapper;
import com.app.service.WCircleService;
import com.niit.pojo.PostVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * 安卓端圈子Service实现类
 */
@Service
public class WCircleServiceImpl implements WCircleService {

    @Autowired
    private WCircleMapper circleMapper;

    @Override
    public Map<String, Object> getPosts(Integer categoryId, Integer page, Integer pageSize, Integer tagId, String sortType) {
        Map<String, Object> result = new HashMap<>();
        
        // 构建查询参数
        Map<String, Object> params = new HashMap<>();
        if (categoryId != null) {
            params.put("categoryId", categoryId);
        }
        if (tagId != null) {
            params.put("tagId", tagId);
        }
        params.put("sortType", sortType);
        
        // 计算分页参数
        int start = (page - 1) * pageSize;
        params.put("start", start);
        params.put("size", pageSize);
        
        // 查询数据
        List<PostVO> posts = circleMapper.selectPostsByCondition(params);
        int total = circleMapper.countPostsByCondition(params);
        int totalPages = (int) Math.ceil((double) total / pageSize);
        
        // 转换为安卓端需要的格式
        List<Map<String, Object>> postList = new ArrayList<>();
        for (PostVO post : posts) {
            Map<String, Object> postMap = convertPostToMap(post);
            postList.add(postMap);
        }
        
        result.put("posts", postList);
        result.put("total", total);
        result.put("page", page);
        result.put("pageSize", pageSize);
        result.put("totalPages", totalPages);
        
        return result;
    }

    @Override
    public Map<String, Object> getPostDetail(Integer postId, Integer userId) {
        // 增加浏览量
        circleMapper.incrementViewCount(postId);
        
        PostVO post = circleMapper.selectPostById(postId);
        if (post == null) {
            return null;
        }
        Map<String, Object> postMap = convertPostToMap(post);
        
        // 查询用户是否已点赞
        if (userId != null) {
            Integer likeId = circleMapper.checkLikeExists(postId, userId);
            postMap.put("liked", likeId != null);
            // 查询用户是否已收藏
            Integer favoriteId = circleMapper.checkFavoriteExists(postId, userId);
            postMap.put("favorited", favoriteId != null);
        } else {
            postMap.put("liked", false);
            postMap.put("favorited", false);
        }
        
        return postMap;
    }

    @Override
    public Map<String, Object> getPostComments(Integer postId) {
        Map<String, Object> result = new HashMap<>();
        System.out.println("查询评论: postId=" + postId);
        List<Map<String, Object>> comments = circleMapper.selectCommentsByPostId(postId);
        System.out.println("从数据库查询到 " + comments.size() + " 条评论记录");
        
        // 格式化评论数据，并构建层级结构（评论和回复）
        List<Map<String, Object>> commentList = new ArrayList<>();
        Map<Integer, Map<String, Object>> commentMap = new HashMap<>(); // 用于快速查找父评论
        
        // 第一遍：处理所有评论，建立映射
        for (Map<String, Object> comment : comments) {
            try {
                System.out.println("开始处理评论: " + comment);
                System.out.println("createTime类型: " + (comment.get("createTime") != null ? comment.get("createTime").getClass().getName() : "null"));
                
                Map<String, Object> commentData = new HashMap<>();
                commentData.put("id", String.valueOf(comment.get("id")));
                commentData.put("commentId", comment.get("commentId"));
                commentData.put("author", comment.get("author") != null ? comment.get("author") : "匿名");
                commentData.put("avatar", comment.get("avatar") != null ? comment.get("avatar") : "");
                commentData.put("content", comment.get("content") != null ? comment.get("content") : "");
                commentData.put("time", formatTimeFromObject(comment.get("createTime")));
                commentData.put("likeCount", comment.get("likeCount") != null ? comment.get("likeCount") : 0);
                Object parentIdObj = comment.get("parentId");
                Integer parentId = parentIdObj != null ? (Integer) parentIdObj : null;
                commentData.put("parentId", parentId);
                commentData.put("replies", new ArrayList<Map<String, Object>>()); // 初始化回复列表
                
                Integer commentId = (Integer) comment.get("commentId");
                commentMap.put(commentId, commentData);
                System.out.println("成功处理评论: id=" + commentId + ", parentId=" + parentId + ", author=" + comment.get("author"));
            } catch (Exception e) {
                System.err.println("处理评论时发生异常: " + e.getMessage());
                e.printStackTrace();
            }
        }
        
        // 第二遍：构建层级结构
        for (Map<String, Object> commentData : commentMap.values()) {
            Integer parentId = (Integer) commentData.get("parentId");
            if (parentId == null) {
                // 顶级评论
                commentList.add(commentData);
                System.out.println("添加顶级评论: " + commentData.get("commentId"));
            } else {
                // 回复，添加到父评论的replies中
                Map<String, Object> parentComment = commentMap.get(parentId);
                if (parentComment != null) {
                    @SuppressWarnings("unchecked")
                    List<Map<String, Object>> replies = (List<Map<String, Object>>) parentComment.get("replies");
                    replies.add(commentData);
                    System.out.println("添加回复到父评论 " + parentId + ": " + commentData.get("commentId"));
                } else {
                    // 如果找不到父评论，作为顶级评论显示
                    commentList.add(commentData);
                    System.out.println("父评论不存在，作为顶级评论显示: " + commentData.get("commentId"));
                }
            }
        }
        
        System.out.println("最终返回 " + commentList.size() + " 条顶级评论（包含回复）");
        result.put("comments", commentList);
        result.put("total", comments.size());
        return result;
    }

    @Override
    public Map<String, Object> getHotPosts(Integer limit) {
        Map<String, Object> result = new HashMap<>();
        List<PostVO> posts = circleMapper.selectHotPosts(limit);
        
        // 转换为安卓端需要的格式
        List<Map<String, Object>> postList = new ArrayList<>();
        for (PostVO post : posts) {
            Map<String, Object> postMap = convertPostToMap(post);
            postList.add(postMap);
        }
        
        result.put("posts", postList);
        return result;
    }

    /**
     * 将PostVO转换为Map格式（适配安卓端）
     */
    private Map<String, Object> convertPostToMap(PostVO post) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", String.valueOf(post.getPostId()));
        map.put("postId", post.getPostId());
        map.put("author", post.getUsername() != null ? post.getUsername() : "匿名");
        // 确保avatar字段正确返回，如果为空则返回默认头像路径或空字符串
        String avatar = post.getAvatar();
        // 添加调试日志
        System.out.println("转换帖子数据 - postId: " + post.getPostId() + ", username: " + post.getUsername() + ", avatar: " + avatar);
        if (avatar == null || avatar.trim().isEmpty()) {
            avatar = ""; // 或者可以设置一个默认头像路径，如 "/default-avatar.png"
        }
        map.put("avatar", avatar);
        map.put("time", formatTime(post.getCreateTime()));
        map.put("title", post.getTitle() != null ? post.getTitle() : "");
        map.put("content", post.getContent() != null ? post.getContent() : "");
        
        // 获取标签（需要查询post_tag表）
        List<String> tagNames = circleMapper.selectTagNamesByPostId(post.getPostId());
        map.put("tag", tagNames.isEmpty() ? "" : tagNames.get(0)); // 取第一个标签
        map.put("tags", tagNames);
        
        map.put("views", post.getViewCount() != null ? post.getViewCount() : 0);
        map.put("comments", post.getCommentCount() != null ? post.getCommentCount() : 0);
        map.put("likes", post.getLikeCount() != null ? post.getLikeCount() : 0);
        // 点赞状态需要根据用户ID查询，暂时设为false（在详情页会重新查询）
        map.put("liked", false);
        
        // 图片列表
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
        
        map.put("categoryId", post.getCategoryId());
        map.put("categoryName", post.getCategoryName());
        map.put("userId", post.getUserId());
        
        return map;
    }

    /**
     * 格式化时间（相对时间）
     */
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
            // 超过7天显示具体日期
            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("MM-dd HH:mm");
            return sdf.format(date);
        }
    }

    /**
     * 从Object格式化时间（支持Date和LocalDateTime）
     */
    private String formatTimeFromObject(Object timeObj) {
        if (timeObj == null) {
            System.out.println("formatTimeFromObject: timeObj为null，返回'刚刚'");
            return "刚刚";
        }
        
        System.out.println("formatTimeFromObject: 时间类型=" + timeObj.getClass().getName() + ", 值=" + timeObj);
        
        Date date = null;
        
        // 处理LocalDateTime类型
        if (timeObj instanceof java.time.LocalDateTime) {
            java.time.LocalDateTime localDateTime = (java.time.LocalDateTime) timeObj;
            // 转换为Date
            java.time.ZoneId zoneId = java.time.ZoneId.systemDefault();
            java.time.ZonedDateTime zonedDateTime = localDateTime.atZone(zoneId);
            date = Date.from(zonedDateTime.toInstant());
            System.out.println("formatTimeFromObject: LocalDateTime转换为Date成功");
        } else if (timeObj instanceof Date) {
            // 直接使用Date类型
            date = (Date) timeObj;
            System.out.println("formatTimeFromObject: 直接使用Date类型");
        } else {
            // 其他类型，尝试转换为字符串
            System.err.println("formatTimeFromObject: 未知的时间类型: " + timeObj.getClass().getName() + ", 值: " + timeObj);
            return "刚刚";
        }
        
        String formattedTime = formatTime(date);
        System.out.println("formatTimeFromObject: 格式化后时间=" + formattedTime);
        return formattedTime;
    }

    @Override
    public void incrementViewCount(Integer postId) {
        circleMapper.incrementViewCount(postId);
    }

    @Override
    public Map<String, Object> addComment(Integer postId, Integer userId, String content, Integer parentId) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            System.out.println("添加评论: postId=" + postId + ", userId=" + userId + ", content=" + content + ", parentId=" + parentId);
            
            // 直接插入评论（允许多条评论）
            circleMapper.insertComment(postId, userId, content, parentId);
            // 获取最后插入的评论ID
            int commentId = circleMapper.getLastInsertCommentId();
            System.out.println("评论插入成功，commentId=" + commentId);
            
            // 更新帖子评论数
            circleMapper.incrementCommentCount(postId);
            System.out.println("评论数更新成功");
            
            // 查询更新后的评论数，以便前端立即显示
            PostVO post = circleMapper.selectPostById(postId);
            int updatedCommentCount = post != null && post.getCommentCount() != null ? post.getCommentCount() : 0;
            
            result.put("commentId", commentId);
            result.put("commentCount", updatedCommentCount);
            result.put("success", true);
            return result;
        } catch (Exception e) {
            System.err.println("添加评论失败: " + e.getMessage());
            e.printStackTrace();
            result.put("success", false);
            result.put("error", e.getMessage());
            return result;
        }
    }

    @Override
    public Map<String, Object> toggleLike(Integer postId, Integer userId) {
        Map<String, Object> result = new HashMap<>();
        
        // 检查是否已点赞（status=1）
        Integer likeId = circleMapper.checkLikeExists(postId, userId);
        
        if (likeId != null) {
            // 已点赞，取消点赞
            circleMapper.deleteLike(postId, userId);
            circleMapper.updateLikeCount(postId, -1);
            result.put("liked", false);
        } else {
            // 检查是否存在记录（可能之前取消过点赞，status=0）
            Integer recordId = circleMapper.checkLikeRecordExists(postId, userId);
            if (recordId != null) {
                // 存在记录但status=0，恢复点赞
                circleMapper.restoreLike(postId, userId);
                circleMapper.updateLikeCount(postId, 1);
            } else {
                // 不存在记录，插入新点赞
                circleMapper.insertLike(postId, userId);
                circleMapper.updateLikeCount(postId, 1);
            }
            result.put("liked", true);
        }
        
        result.put("success", true);
        return result;
    }

    @Override
    public Map<String, Object> getStatistics() {
        Map<String, Object> result = new HashMap<>();
        
        // 统计所有帖子数量（status=1的帖子，不限制分类）
        Map<String, Object> params = new HashMap<>();
        // 不设置categoryId，统计所有分类的帖子
        int postCount = circleMapper.countPostsByCondition(params);
        
        // 统计用户数量（status=0的正常用户）
        int userCount = circleMapper.countUsers();
        
        result.put("postCount", postCount);
        result.put("userCount", userCount);
        
        return result;
    }

    @Override
    public Map<String, Object> addFavorite(Integer postId, Integer userId) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            // 检查是否已收藏
            Integer favoriteId = circleMapper.checkFavoriteExists(postId, userId);
            
            if (favoriteId != null) {
                // 已收藏，取消收藏（类似点赞的toggle功能）
                // 更新status为0（软删除）
                circleMapper.deleteFavorite(postId, userId);
                circleMapper.updateFavoriteCount(postId, -1);
                result.put("success", true);
                result.put("favorited", false);
                result.put("message", "取消收藏成功");
                return result;
            }
            
            // 检查是否存在记录（可能之前取消过收藏，status=0）
            Integer recordId = circleMapper.checkFavoriteRecordExists(postId, userId);
            if (recordId != null) {
                // 存在记录但status=0，恢复收藏
                circleMapper.restoreFavorite(postId, userId);
                circleMapper.updateFavoriteCount(postId, 1);
            } else {
                // 不存在记录，插入新收藏
                circleMapper.insertFavorite(postId, userId);
                circleMapper.updateFavoriteCount(postId, 1);
            }
            
            result.put("success", true);
            result.put("favorited", true);
            result.put("message", "收藏成功");
            return result;
        } catch (Exception e) {
            System.err.println("收藏操作失败: " + e.getMessage());
            e.printStackTrace();
            result.put("success", false);
            result.put("error", e.getMessage());
            return result;
        }
    }

    @Override
    public Map<String, Object> getFavoritePosts(Integer userId, Integer page, Integer pageSize) {
        Map<String, Object> result = new HashMap<>();
        
        // 计算分页参数
        int start = (page - 1) * pageSize;
        
        // 查询收藏的帖子
        List<PostVO> posts = circleMapper.selectFavoritePostsByUserId(userId, start, pageSize);
        int total = circleMapper.countFavoritePostsByUserId(userId);
        int totalPages = (int) Math.ceil((double) total / pageSize);
        
        // 转换为安卓端需要的格式
        List<Map<String, Object>> postList = new ArrayList<>();
        for (PostVO post : posts) {
            Map<String, Object> postMap = convertPostToMap(post);
            // 收藏列表中的帖子都是已收藏的
            postMap.put("favorited", true);
            postList.add(postMap);
        }
        
        result.put("posts", postList);
        result.put("total", total);
        result.put("page", page);
        result.put("pageSize", pageSize);
        result.put("totalPages", totalPages);
        
        return result;
    }
}

