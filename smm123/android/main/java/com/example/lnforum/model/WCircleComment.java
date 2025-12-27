package com.example.lnforum.model;

public class WCircleComment {
    private final String id;
    private final String postId;
    private final String author;
    private final String avatar;
    private final String content;
    private final String time;
    private Integer userId; // 评论作者的用户ID

    public WCircleComment(String id, String postId, String author, String content, String time) {
        this(id, postId, author, "", content, time);
    }

    public WCircleComment(String id, String postId, String author, String avatar, String content, String time) {
        this.id = id;
        this.postId = postId;
        this.author = author;
        this.avatar = avatar != null ? avatar : "";
        this.content = content;
        this.time = time;
    }

    public String getId() { return id; }
    public String getPostId() { return postId; }
    public String getAuthor() { return author; }
    public String getAvatar() { return avatar; }
    public String getContent() { return content; }
    public String getTime() { return time; }
    public Integer getUserId() { return userId; }
    public void setUserId(Integer userId) { this.userId = userId; }
}

