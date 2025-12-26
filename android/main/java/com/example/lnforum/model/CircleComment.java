package com.example.lnforum.model;

public class CircleComment {
    private String id;
    private String postId;
    private String author;
    private String avatar;
    private String content;
    private String time;
    private int likeCount;
    private boolean isLiked;

    // ✅ No-args constructor
    public CircleComment() {
    }

    // ✅ Full-args constructor matching your usage in CircleDetailActivity
    public CircleComment(String id, String postId, String author, String avatar, String content, String time, int likeCount, boolean isLiked) {
        this.id = id;
        this.postId = postId;
        this.author = author;
        this.avatar = avatar;
        this.content = content;
        this.time = time;
        this.likeCount = likeCount;
        this.isLiked = isLiked;
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getPostId() { return postId; }
    public void setPostId(String postId) { this.postId = postId; }

    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }

    public String getAvatar() { return avatar; }
    public void setAvatar(String avatar) { this.avatar = avatar; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public String getTime() { return time; }
    public void setTime(String time) { this.time = time; }

    public int getLikeCount() { return likeCount; }
    public void setLikeCount(int likeCount) { this.likeCount = likeCount; }

    public boolean isLiked() { return isLiked; }
    public void setLiked(boolean liked) { isLiked = liked; }
}