package com.app.service.impl;

import com.app.dao.WCircleMapper;
import com.app.dao.CUserMapper;
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
    
    @Autowired
    private CUserMapper cUserMapper;

    @Override
    public Map<String, Object> getPosts(Integer categoryId, Integer page, Integer pageSize, Integer tagId, String sortType) {
        return getPosts(categoryId, page, pageSize, tagId, sortType, null);
    }

    @Override
    public Map<String, Object> getPosts(Integer categoryId, Integer page, Integer pageSize, Integer tagId, String sortType, Integer userId) {
        return getPostsWithPostUserId(categoryId, page, pageSize, tagId, sortType, userId, null);
    }
    
    /**
     * 获取帖子列表（支持指定发布者）
     */
    private Map<String, Object> getPostsWithPostUserId(Integer categoryId, Integer page, Integer pageSize, Integer tagId, String sortType, Integer userId, Integer postUserId) {
        Map<String, Object> result = new HashMap<>();
        
        // 构建查询参数
        Map<String, Object> params = new HashMap<>();
        if (categoryId != null) {
            params.put("categoryId", categoryId);
        }
        if (tagId != null) {
            params.put("tagId", tagId);
        }
        if (userId != null) {
            params.put("userId", userId);
        }
        if (postUserId != null) {
            params.put("postUserId", postUserId);
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
            // 如果用户已登录，查询点赞状态
            if (userId != null) {
                Integer likeId = circleMapper.checkLikeExists(post.getPostId(), userId);
                postMap.put("liked", likeId != null);
                Integer favoriteId = circleMapper.checkFavoriteExists(post.getPostId(), userId);
                postMap.put("favorited", favoriteId != null);
            } else {
                postMap.put("liked", false);
                postMap.put("favorited", false);
            }
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
            // 查询用户是否已关注作者
            Integer authorId = post.getUserId();
            if (authorId != null && !authorId.equals(userId)) {
                Integer followId = cUserMapper.checkFollowExists(userId, authorId);
                postMap.put("followed", followId != null);
            } else {
                postMap.put("followed", false);
            }
        } else {
            postMap.put("liked", false);
            postMap.put("favorited", false);
            postMap.put("followed", false);
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
                commentData.put("userId", comment.get("userId"));
                
                // 检查评论状态
                String commentStatus = comment.get("commentStatus") != null ? 
                    (String) comment.get("commentStatus") : "正常";
                boolean isDeleted = "已删除".equals(commentStatus);
                
                if (isDeleted) {
                    // 已删除的评论：不显示用户和时间
                    commentData.put("author", "");
                    commentData.put("avatar", "");
                    commentData.put("content", "该评论已被删除");
                    commentData.put("time", "");
                } else {
                    // 正常评论：显示用户和时间
                    commentData.put("author", comment.get("author") != null ? comment.get("author") : "匿名");
                    commentData.put("avatar", comment.get("avatar") != null ? comment.get("avatar") : "");
                    commentData.put("content", comment.get("content") != null ? comment.get("content") : "");
                    commentData.put("time", formatTimeFromObject(comment.get("createTime")));
                }
                
                commentData.put("likeCount", comment.get("likeCount") != null ? comment.get("likeCount") : 0);
                Object parentIdObj = comment.get("parentId");
                Integer parentId = parentIdObj != null ? (Integer) parentIdObj : null;
                commentData.put("parentId", parentId);
                commentData.put("replies", new ArrayList<Map<String, Object>>()); // 初始化回复列表
                commentData.put("commentStatus", commentStatus); // 保存评论状态
                
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
        
        // 价格字段（用于跑腿订单等）
        if (post.getPrice() != null) {
            map.put("price", post.getPrice().doubleValue());
        }
        
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
    
    @Override
    public Map<String, Object> deleteComment(Integer commentId, Integer userId) {
        Map<String, Object> result = new HashMap<>();
        try {
            // 验证权限：只有评论作者可以删除自己的评论
            Integer commentUserId = circleMapper.getCommentUserId(commentId);
            if (commentUserId == null) {
                result.put("success", false);
                result.put("message", "评论不存在");
                return result;
            }
            if (!commentUserId.equals(userId)) {
                result.put("success", false);
                result.put("message", "无权删除此评论");
                return result;
            }
            
            // 获取评论的帖子ID（用于更新评论数）
            // 这里需要从评论中获取postId，可以通过查询interaction表获取
            // 为了简化，我们直接删除评论，评论数的更新可以在删除时通过触发器或应用层处理
            // 但为了保持一致性，我们需要先查询postId
            Integer postId = circleMapper.getPostIdByCommentId(commentId);
            
            // 删除评论（软删除）
            circleMapper.deleteComment(commentId);
            
            // 更新帖子评论数
            if (postId != null) {
                circleMapper.decrementCommentCount(postId);
            }
            
            result.put("success", true);
            result.put("message", "删除成功");
            return result;
        } catch (Exception e) {
            System.err.println("删除评论失败: " + e.getMessage());
            e.printStackTrace();
            result.put("success", false);
            result.put("error", e.getMessage());
            return result;
        }
    }
    
    @Override
    public List<Map<String, Object>> getUserComments(Integer userId) {
        List<Map<String, Object>> comments = circleMapper.selectCommentsByUserId(userId, 0, 100);
        return comments;
    }
    
    @Override
    public List<Map<String, Object>> getUserOrders(Integer userId) {
        // 查询用户的跑腿订单（categoryId=2）
        Map<String, Object> result = getPostsWithPostUserId(2, 1, 100, null, "newest", null, userId);
        
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> posts = (List<Map<String, Object>>) result.get("posts");
        if (posts == null) {
            posts = new ArrayList<>();
        }
        
        // 转换为订单格式
        List<Map<String, Object>> orders = new ArrayList<>();
        for (Map<String, Object> post : posts) {
            Map<String, Object> order = new HashMap<>();
            order.put("taskId", post.get("postId"));
            order.put("title", post.get("title"));
            order.put("description", post.get("content"));
            order.put("amount", post.get("price"));
            order.put("taskStatus", 0); // 默认为进行中
            order.put("creatorId", post.get("userId"));
            order.put("acceptorId", null); // 需要从其他地方查询接单者
            order.put("createTime", post.get("time"));
            orders.add(order);
        }
        
        return orders;
    }
    
    @Override
    public List<Map<String, Object>> getUserPosts(Integer userId, Integer categoryId) {
        // 查询用户发布的帖子
        Map<String, Object> result = getPostsWithPostUserId(categoryId, 1, 100, null, "newest", null, userId);
        
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> posts = (List<Map<String, Object>>) result.get("posts");
        if (posts == null) {
            posts = new ArrayList<>();
        }
        
        // 转换为CPost格式
        List<Map<String, Object>> cPostList = new ArrayList<>();
        for (Map<String, Object> post : posts) {
            Map<String, Object> cPost = new HashMap<>();
            cPost.put("postId", post.get("postId"));
            cPost.put("userId", post.get("userId"));
            cPost.put("title", post.get("title"));
            cPost.put("content", post.get("content"));
            
            // 图片列表：保持原有的 images 数组格式
            Object imagesObj = post.get("images");
            if (imagesObj instanceof List) {
                cPost.put("images", imagesObj); // 保留完整的图片数组
                // 同时保留 image1 用于兼容
                if (((List<?>) imagesObj).size() > 0) {
                    cPost.put("image1", ((List<?>) imagesObj).get(0));
                } else {
                    cPost.put("image1", null);
                }
            } else {
                cPost.put("images", new ArrayList<>());
                cPost.put("image1", null);
            }
            
            cPost.put("createTime", post.get("time"));
            cPost.put("authorName", post.get("author"));
            cPost.put("authorAvatar", post.get("avatar"));
            cPost.put("views", post.get("views"));
            cPost.put("likes", post.get("likes"));
            cPost.put("comments", post.get("comments"));
            
            // 添加标签信息
            Object tagObj = post.get("tag");
            if (tagObj != null) {
                cPost.put("tag", tagObj);
            }
            Object tagsObj = post.get("tags");
            if (tagsObj instanceof List) {
                cPost.put("tags", tagsObj);
            }
            
            // 添加点赞和收藏状态
            Object likedObj = post.get("liked");
            if (likedObj != null) {
                cPost.put("liked", likedObj);
            }
            Object favoritedObj = post.get("favorited");
            if (favoritedObj != null) {
                cPost.put("favorited", favoritedObj);
            }
            
            cPostList.add(cPost);
        }
        
        return cPostList;
    }
}

