package com.example.lnforum.repository;

import android.util.Log;

import com.example.lnforum.model.WCircleComment;
import com.example.lnforum.model.WCirclePost;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.util.Log;

/**
 * 圈子动态仓库，从PC后端API获取数据
 */
public class WCircleRepository {
    private static final String TAG = "WCircleRepository";

    /**
     * 从后端获取帖子列表
     * @param categoryId 分类ID（1=动态，2=跑腿，3=二手集市，4=失物招领）
     * @param page 页码
     * @param pageSize 每页数量
     * @param tagId 标签ID（可选）
     * @param sortType 排序类型：newest(最新), hottest(热门), mostComments(最多评论)
     * @return 帖子列表
     */
    public static List<WCirclePost> getPosts(Integer categoryId, Integer page, Integer pageSize, Integer tagId, String sortType) {
        try {
            Map<String, String> params = new HashMap<>();
            if (categoryId != null) {
                params.put("categoryId", String.valueOf(categoryId));
            }
            params.put("page", String.valueOf(page));
            params.put("pageSize", String.valueOf(pageSize));
            if (tagId != null) {
                params.put("tagId", String.valueOf(tagId));
            }
            if (sortType != null) {
                params.put("sortType", sortType);
            }
            
            WApiClient.ApiResponse response = WApiClient.get("/app/circle/posts", params);
            if (response.success && response.getCode() == 200) {
                Object dataObj = response.getData();
                if (dataObj instanceof JSONObject) {
                    JSONObject data = (JSONObject) dataObj;
                    JSONArray postsArray = data.getJSONArray("posts");
                    List<WCirclePost> posts = new ArrayList<>();
                    
                    for (int i = 0; i < postsArray.length(); i++) {
                        JSONObject postJson = postsArray.getJSONObject(i);
                        WCirclePost post = parsePostFromJson(postJson);
                        posts.add(post);
                    }
                    return posts;
                }
            } else {
                Log.e(TAG, "获取帖子列表失败: " + response.getMessage());
            }
        } catch (Exception e) {
            Log.e(TAG, "获取帖子列表异常", e);
        }
        
        // 失败时返回空列表
        return new ArrayList<>();
    }

    /**
     * 获取帖子详情
     */
    public static WCirclePost getPost(String id) {
        return getPost(id, null);
    }

    /**
     * 获取帖子详情（带用户ID，用于查询点赞状态）
     */
    public static WCirclePost getPost(String id, Integer userId) {
        try {
            Map<String, String> params = new HashMap<>();
            if (userId != null) {
                params.put("userId", String.valueOf(userId));
            }
            WApiClient.ApiResponse response = WApiClient.get("/app/circle/post/" + id, params);
            if (response.success && response.getCode() == 200) {
                Object dataObj = response.getData();
                if (dataObj instanceof JSONObject) {
                    return parsePostFromJson((JSONObject) dataObj);
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "获取帖子详情异常", e);
        }
        return null;
    }

    /**
     * 从JSON解析帖子对象
     */
    private static WCirclePost parsePostFromJson(JSONObject json) throws Exception {
        String id = json.getString("id");
        String author = json.optString("author", "匿名");
        String avatar = json.optString("avatar", "");
        String time = json.optString("time", "刚刚");
        String title = json.optString("title", "");
        String content = json.optString("content", "");
        String tag = json.optString("tag", "");
        int views = json.optInt("views", 0);
        int comments = json.optInt("comments", 0);
        int likes = json.optInt("likes", 0);
        boolean liked = json.optBoolean("liked", false);
        boolean favorited = json.optBoolean("favorited", false);
        
        List<String> images = new ArrayList<>();
        if (json.has("images")) {
            JSONArray imagesArray = json.getJSONArray("images");
            for (int i = 0; i < imagesArray.length(); i++) {
                images.add(imagesArray.getString(i));
            }
        }
        
        WCirclePost post = new WCirclePost(id, author, avatar, time, title, content, tag, views, comments, likes, images);
        // 设置点赞状态和收藏状态
        post.setLiked(liked);
        post.setFavorited(favorited);
        return post;
    }

    /**
     * 获取评论列表（包括回复）
     */
    public static List<WCircleComment> getComments(String postId) {
        Log.d(TAG, "========== WCircleRepository.getComments 开始 ==========");
        Log.d(TAG, "请求参数 postId: " + postId);
        
        if (postId == null || postId.isEmpty()) {
            Log.e(TAG, "postId为空，无法获取评论");
            return new ArrayList<>();
        }
        
        try {
            String requestUrl = "/app/circle/post/" + postId + "/comments";
            Log.d(TAG, "请求URL: " + requestUrl);
            
            WApiClient.ApiResponse response = WApiClient.get(requestUrl, null);
            Log.d(TAG, "API响应 - code: " + response.getCode() + ", success: " + response.success);
            Log.d(TAG, "API响应 - message: " + response.getMessage());
            Log.d(TAG, "API响应 - data是否为null: " + (response.getData() == null));
            
            if (response.getData() != null) {
                Log.d(TAG, "API响应 - data类型: " + response.getData().getClass().getName());
                Log.d(TAG, "API响应 - data内容: " + response.getData().toString());
            }
            
            if (response.success && response.getCode() == 200) {
                Object dataObj = response.getData();
                Log.d(TAG, "dataObj类型: " + (dataObj != null ? dataObj.getClass().getName() : "null"));
                
                if (dataObj instanceof JSONObject) {
                    JSONObject data = (JSONObject) dataObj;
                    Log.d(TAG, "data完整内容: " + data.toString());
                    Log.d(TAG, "data的keys: " + data.keys().toString());
                    
                    if (data.has("comments")) {
                        Object commentsObj = data.get("comments");
                        Log.d(TAG, "commentsObj类型: " + (commentsObj != null ? commentsObj.getClass().getName() : "null"));
                        
                        if (commentsObj instanceof JSONArray) {
                            JSONArray commentsArray = (JSONArray) commentsObj;
                            Log.d(TAG, "成功获取comments数组，长度: " + commentsArray.length());
                            List<WCircleComment> comments = new ArrayList<>();
                            
                            // 递归解析评论和回复
                            for (int i = 0; i < commentsArray.length(); i++) {
                                try {
                                    Object commentObj = commentsArray.get(i);
                                    Log.d(TAG, "处理第" + (i+1) + "条顶级评论，类型: " + (commentObj != null ? commentObj.getClass().getName() : "null"));
                                    
                                    if (commentObj instanceof JSONObject) {
                                        JSONObject commentJson = (JSONObject) commentObj;
                                        Log.d(TAG, "第" + (i+1) + "条评论JSON: " + commentJson.toString());
                                        parseCommentRecursive(commentJson, postId, comments, null);
                                    } else {
                                        Log.e(TAG, "第" + (i+1) + "条评论不是JSONObject类型");
                                    }
                                } catch (Exception e) {
                                    Log.e(TAG, "解析第" + (i+1) + "条评论时异常", e);
                                    e.printStackTrace();
                                }
                            }
                            
                            Log.d(TAG, "========== 解析完成，共 " + comments.size() + " 条评论（包括回复） ==========");
                            for (int i = 0; i < comments.size(); i++) {
                                WCircleComment comment = comments.get(i);
                                Log.d(TAG, "返回评论[" + i + "]: id=" + comment.getId() + ", author=" + comment.getAuthor() + ", content长度=" + (comment.getContent() != null ? comment.getContent().length() : 0));
                            }
                            return comments;
                        } else {
                            Log.e(TAG, "commentsObj不是JSONArray类型: " + (commentsObj != null ? commentsObj.getClass().getName() : "null"));
                            if (commentsObj != null) {
                                Log.e(TAG, "commentsObj内容: " + commentsObj.toString());
                            }
                        }
                    } else {
                        Log.e(TAG, "data中没有comments字段");
                        Log.e(TAG, "data的所有keys: " + data.keys().toString());
                        Log.e(TAG, "data完整内容: " + data.toString());
                    }
                } else {
                    Log.e(TAG, "dataObj不是JSONObject类型: " + (dataObj != null ? dataObj.getClass().getName() : "null"));
                    if (dataObj != null) {
                        Log.e(TAG, "dataObj内容: " + dataObj.toString());
                    }
                }
            } else {
                Log.e(TAG, "获取评论列表失败: code=" + response.getCode() + ", message=" + response.getMessage());
            }
        } catch (Exception e) {
            Log.e(TAG, "获取评论列表异常", e);
            e.printStackTrace();
            Log.e(TAG, "异常堆栈: ", e);
        }
        
        Log.d(TAG, "========== WCircleRepository.getComments 返回空列表 ==========");
        return new ArrayList<>();
    }

    /**
     * 递归解析评论和回复
     */
    private static void parseCommentRecursive(JSONObject commentJson, String postId, 
                                              List<WCircleComment> result, String parentAuthor) throws Exception {
        Log.d(TAG, "parseCommentRecursive - 开始解析评论，parentAuthor: " + parentAuthor);
        Log.d(TAG, "parseCommentRecursive - commentJson: " + commentJson.toString());
        
        String id = commentJson.optString("id", "");
        if (id.isEmpty()) {
            id = String.valueOf(commentJson.optInt("commentId", 0));
        }
        Log.d(TAG, "parseCommentRecursive - id: " + id);
        
        String author = commentJson.optString("author", "匿名");
        Log.d(TAG, "parseCommentRecursive - author: " + author);
        
        String content = commentJson.optString("content", "");
        Log.d(TAG, "parseCommentRecursive - 原始content: " + content);
        
        String time = commentJson.optString("time", "刚刚");
        Log.d(TAG, "parseCommentRecursive - time: " + time);
        
        // 如果是回复，在内容前加上"回复 @xxx："
        if (parentAuthor != null && !parentAuthor.isEmpty()) {
            content = "回复 @" + parentAuthor + "：" + content;
            Log.d(TAG, "parseCommentRecursive - 添加回复前缀后的content: " + content);
        }
        
        WCircleComment comment = new WCircleComment(id, postId, author, content, time);
        result.add(comment);
        Log.d(TAG, "parseCommentRecursive - 已添加评论到结果列表，当前结果数量: " + result.size());
        
        // 处理回复 - 确保能正确解析replies数组
        if (commentJson.has("replies")) {
            Log.d(TAG, "parseCommentRecursive - 发现replies字段");
            try {
                Object repliesObj = commentJson.get("replies");
                Log.d(TAG, "parseCommentRecursive - repliesObj类型: " + (repliesObj != null ? repliesObj.getClass().getName() : "null"));
                
                if (repliesObj instanceof JSONArray) {
                    JSONArray repliesArray = (JSONArray) repliesObj;
                    Log.d(TAG, "parseCommentRecursive - 找到 " + repliesArray.length() + " 条回复");
                    for (int i = 0; i < repliesArray.length(); i++) {
                        Object replyObj = repliesArray.get(i);
                        if (replyObj instanceof JSONObject) {
                            JSONObject replyJson = (JSONObject) replyObj;
                            Log.d(TAG, "parseCommentRecursive - 解析第" + (i+1) + "条回复: " + replyJson.toString());
                            parseCommentRecursive(replyJson, postId, result, author);
                        } else {
                            Log.e(TAG, "parseCommentRecursive - 第" + (i+1) + "条回复不是JSONObject类型");
                        }
                    }
                } else if (repliesObj instanceof List) {
                    // 如果replies是List类型（从后端返回的Map转换而来）
                    Log.d(TAG, "parseCommentRecursive - replies是List类型");
                    @SuppressWarnings("unchecked")
                    List<Map<String, Object>> repliesList = (List<Map<String, Object>>) repliesObj;
                    Log.d(TAG, "parseCommentRecursive - repliesList大小: " + repliesList.size());
                    for (Map<String, Object> replyMap : repliesList) {
                        // 将Map转换为JSONObject进行解析
                        JSONObject replyJson = new JSONObject();
                        for (Map.Entry<String, Object> entry : replyMap.entrySet()) {
                            replyJson.put(entry.getKey(), entry.getValue());
                        }
                        parseCommentRecursive(replyJson, postId, result, author);
                    }
                } else {
                    Log.e(TAG, "parseCommentRecursive - repliesObj既不是JSONArray也不是List，类型: " + (repliesObj != null ? repliesObj.getClass().getName() : "null"));
                }
            } catch (Exception e) {
                Log.e(TAG, "parseCommentRecursive - 解析回复失败", e);
                e.printStackTrace();
            }
        } else {
            Log.d(TAG, "parseCommentRecursive - 没有replies字段");
        }
        
        Log.d(TAG, "parseCommentRecursive - 解析完成");
    }

    /**
     * 添加评论
     */
    public static boolean addComment(String postId, Integer userId, String content) {
        try {
            Map<String, String> params = new HashMap<>();
            params.put("userId", String.valueOf(userId));
            params.put("content", content);
            
            Log.d(TAG, "发布评论: postId=" + postId + ", userId=" + userId + ", content=" + content);
            WApiClient.ApiResponse response = WApiClient.post("/app/circle/post/" + postId + "/comment", params);
            Log.d(TAG, "发布评论响应: code=" + response.getCode() + ", success=" + response.success + ", message=" + response.getMessage());
            Log.d(TAG, "发布评论响应data: " + (response.getData() != null ? response.getData().toString() : "null"));
            
            if (response.success && response.getCode() == 200) {
                Log.d(TAG, "发布评论成功");
                return true;
            } else {
                Log.e(TAG, "发布评论失败: code=" + response.getCode() + ", message=" + response.getMessage());
            }
        } catch (Exception e) {
            Log.e(TAG, "发布评论异常", e);
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 点赞/取消点赞
     */
    public static boolean toggleLike(String postId, Integer userId) {
        try {
            Map<String, String> params = new HashMap<>();
            params.put("userId", String.valueOf(userId));
            
            WApiClient.ApiResponse response = WApiClient.post("/app/circle/post/" + postId + "/like", params);
            if (response.success && response.getCode() == 200) {
                return true;
            } else {
                Log.e(TAG, "点赞操作失败: " + response.getMessage());
            }
        } catch (Exception e) {
            Log.e(TAG, "点赞操作异常", e);
        }
        return false;
    }

    /**
     * 收藏/取消收藏
     */
    public static boolean toggleFavorite(String postId, Integer userId) {
        try {
            Map<String, String> params = new HashMap<>();
            params.put("userId", String.valueOf(userId));
            
            WApiClient.ApiResponse response = WApiClient.post("/app/circle/post/" + postId + "/favorite", params);
            if (response.success && response.getCode() == 200) {
                // 解析返回的收藏状态
                if (response.getData() != null && response.getData() instanceof JSONObject) {
                    JSONObject data = (JSONObject) response.getData();
                    boolean favorited = data.optBoolean("favorited", false);
                    Log.d(TAG, "收藏操作成功，当前状态: " + favorited);
                }
                return true;
            } else {
                Log.e(TAG, "收藏操作失败: " + response.getMessage());
            }
        } catch (Exception e) {
            Log.e(TAG, "收藏操作异常", e);
        }
        return false;
    }

    /**
     * 获取统计数据
     */
    public static Map<String, Integer> getStatistics() {
        try {
            WApiClient.ApiResponse response = WApiClient.get("/app/circle/statistics", null);
            if (response.success && response.getCode() == 200) {
                Object dataObj = response.getData();
                if (dataObj instanceof JSONObject) {
                    JSONObject data = (JSONObject) dataObj;
                    Map<String, Integer> stats = new HashMap<>();
                    stats.put("postCount", data.optInt("postCount", 0));
                    stats.put("userCount", data.optInt("userCount", 0));
                    return stats;
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "获取统计数据异常", e);
        }
        Map<String, Integer> stats = new HashMap<>();
        stats.put("postCount", 0);
        stats.put("userCount", 0);
        return stats;
    }
}

