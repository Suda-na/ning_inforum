package com.app.controller;

import com.app.common.Result;
import com.app.service.WCircleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 安卓端圈子Controller
 * 处理圈子动态相关的请求
 */
@RestController
@RequestMapping("/app/circle")
@CrossOrigin(origins = "*", maxAge = 3600)
public class WCircleController {

    @Autowired
    private WCircleService circleService;

    /**
     * 获取圈子帖子列表
     * @param categoryId 分类ID（1=动态，2=跑腿，3=二手集市，4=失物招领）
     * @param page 页码，从1开始
     * @param pageSize 每页数量
     * @param tagId 标签ID（可选，用于筛选）
     * @param sortType 排序类型：newest(最新), hottest(热门), mostComments(最多评论)
     * @return 帖子列表
     */
    @GetMapping("/posts")
    public Result<Map<String, Object>> getPosts(
            @RequestParam(value = "categoryId", required = false) Integer categoryId,
            @RequestParam(value = "page", defaultValue = "1") Integer page,
            @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
            @RequestParam(value = "tagId", required = false) Integer tagId,
            @RequestParam(value = "sortType", defaultValue = "newest") String sortType) {
        try {
            Map<String, Object> result = circleService.getPosts(categoryId, page, pageSize, tagId, sortType);
            return Result.success(result);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("查询失败：" + e.getMessage());
        }
    }

    /**
     * 获取帖子详情
     * @param postId 帖子ID
     * @return 帖子详情
     */
    @GetMapping("/post/{postId}")
    public Result<Map<String, Object>> getPostDetail(
            @PathVariable("postId") Integer postId,
            @RequestParam(value = "userId", required = false) Integer userId) {
        try {
            Map<String, Object> result = circleService.getPostDetail(postId, userId);
            return Result.success(result);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("查询失败：" + e.getMessage());
        }
    }

    /**
     * 获取帖子评论列表
     * @param postId 帖子ID
     * @return 评论列表
     */
    @GetMapping("/post/{postId}/comments")
    public Result<Map<String, Object>> getPostComments(@PathVariable("postId") Integer postId) {
        try {
            Map<String, Object> result = circleService.getPostComments(postId);
            return Result.success(result);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("查询失败：" + e.getMessage());
        }
    }

    /**
     * 获取浏览量最高的帖子（用于今日最热）
     * @param limit 返回数量
     * @return 帖子列表
     */
    @GetMapping("/hot")
    public Result<Map<String, Object>> getHotPosts(
            @RequestParam(value = "limit", defaultValue = "1") Integer limit) {
        try {
            Map<String, Object> result = circleService.getHotPosts(limit);
            return Result.success(result);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("查询失败：" + e.getMessage());
        }
    }

    /**
     * 增加帖子浏览量
     * @param postId 帖子ID
     */
    @PostMapping("/post/{postId}/view")
    public Result<Void> incrementViewCount(@PathVariable("postId") Integer postId) {
        try {
            circleService.incrementViewCount(postId);
            return Result.success(null);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("操作失败：" + e.getMessage());
        }
    }

    /**
     * 添加评论
     * @param postId 帖子ID
     * @param userId 用户ID
     * @param content 评论内容
     * @param parentId 父评论ID（可选）
     */
    @PostMapping("/post/{postId}/comment")
    public Result<Map<String, Object>> addComment(
            @PathVariable("postId") Integer postId,
            @RequestParam("userId") Integer userId,
            @RequestParam("content") String content,
            @RequestParam(value = "parentId", required = false) Integer parentId) {
        try {
            Map<String, Object> result = circleService.addComment(postId, userId, content, parentId);
            return Result.success(result);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("发布评论失败：" + e.getMessage());
        }
    }

    /**
     * 点赞/取消点赞
     * @param postId 帖子ID
     * @param userId 用户ID
     */
    @PostMapping("/post/{postId}/like")
    public Result<Map<String, Object>> toggleLike(
            @PathVariable("postId") Integer postId,
            @RequestParam("userId") Integer userId) {
        try {
            Map<String, Object> result = circleService.toggleLike(postId, userId);
            return Result.success(result);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("操作失败：" + e.getMessage());
        }
    }

    /**
     * 获取统计数据
     */
    @GetMapping("/statistics")
    public Result<Map<String, Object>> getStatistics() {
        try {
            Map<String, Object> result = circleService.getStatistics();
            return Result.success(result);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("查询失败：" + e.getMessage());
        }
    }

    /**
     * 添加/取消收藏（toggle功能）
     * @param postId 帖子ID
     * @param userId 用户ID
     */
    @PostMapping("/post/{postId}/favorite")
    public Result<Map<String, Object>> addFavorite(
            @PathVariable("postId") Integer postId,
            @RequestParam("userId") Integer userId) {
        try {
            Map<String, Object> result = circleService.addFavorite(postId, userId);
            return Result.success(result);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("收藏失败：" + e.getMessage());
        }
    }

    /**
     * 获取用户的收藏列表
     * @param userId 用户ID
     * @param page 页码
     * @param pageSize 每页数量
     * @return 收藏列表
     */
    @GetMapping("/favorites")
    public Result<Map<String, Object>> getFavoritePosts(
            @RequestParam("userId") Integer userId,
            @RequestParam(value = "page", defaultValue = "1") Integer page,
            @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {
        try {
            Map<String, Object> result = circleService.getFavoritePosts(userId, page, pageSize);
            return Result.success(result);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("查询失败：" + e.getMessage());
        }
    }
}
