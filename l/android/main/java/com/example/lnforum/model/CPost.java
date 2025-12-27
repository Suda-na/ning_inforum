package com.example.lnforum.model;

import java.io.Serializable;

public class CPost implements Serializable {
    private Integer postId;
    private Integer userId;
    private String title;
    private String content;
    private String image1;

    private String createTime;

    // ✅ 新增：对应 SQL 中的 as authorName 和 as authorAvatar
    private String authorName;
    private String authorAvatar;

    // ✅ 新增：对应 SQL 中的统计数据
    private Integer views;
    private Integer likes;
    private Integer comments;

    // --- Getters and Setters ---
    public Integer getPostId() { return postId; }
    public void setPostId(Integer postId) { this.postId = postId; }

    public Integer getUserId() { return userId; }
    public void setUserId(Integer userId) { this.userId = userId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public String getImage1() { return image1; }
    public void setImage1(String image1) { this.image1 = image1; }

    public String getCreateTime() { return createTime; }
    public void setCreateTime(String createTime) { this.createTime = createTime; }

    public String getAuthorName() { return authorName; }
    public void setAuthorName(String authorName) { this.authorName = authorName; }

    public String getAuthorAvatar() { return authorAvatar; }
    public void setAuthorAvatar(String authorAvatar) { this.authorAvatar = authorAvatar; }

    public Integer getViews() { return views; }
    public void setViews(Integer views) { this.views = views; }

    public Integer getLikes() { return likes; }
    public void setLikes(Integer likes) { this.likes = likes; }

    public Integer getComments() { return comments; }
    public void setComments(Integer comments) { this.comments = comments; }
}