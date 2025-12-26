package com.example.lnforum.model;

import java.util.List;

public class WCirclePost {
    private final String id;
    private final String author;
    private final String avatar;
    private final String time;
    private final String title;
    private final String content;
    private final String tag;
    private int views;
    private int comments;
    private int likes;
    private boolean liked;
    private boolean favorited; // 收藏
    private boolean watched; // 插眼
    private boolean followed; // 关注
    private final List<String> images;

    public WCirclePost(String id, String author, String avatar, String time, String title, String content, String tag,
                      int views, int comments, int likes, List<String> images) {
        this.id = id;
        this.author = author;
        this.avatar = avatar;
        this.time = time;
        this.title = title;
        this.content = content;
        this.tag = tag;
        this.views = views;
        this.comments = comments;
        this.likes = likes;
        this.images = images != null ? images : new java.util.ArrayList<>();
    }

    public String getId() { return id; }
    public String getAuthor() { return author; }
    public String getAvatar() { return avatar; }
    public String getTime() { return time; }
    public String getTitle() { return title; }
    public String getContent() { return content; }
    public String getTag() { return tag; }
    public int getViews() { return views; }
    public int getComments() { return comments; }
    public int getLikes() { return likes; }
    public boolean isLiked() { return liked; }
    public boolean isFavorited() { return favorited; }
    public boolean isWatched() { return watched; }
    public boolean isFollowed() { return followed; }
    public List<String> getImages() { return images; }

    public void addView() { views++; }
    public void toggleLike() {
        liked = !liked;
        likes += liked ? 1 : -1;
        if (likes < 0) likes = 0;
    }
    
    public void setLiked(boolean liked) {
        this.liked = liked;
    }

    public void setFavorited(boolean favorited) {
        this.favorited = favorited;
    }

    public void toggleWatch() { watched = !watched; }
    public void toggleFollow() { followed = !followed; }
    public void setComments(int count) { comments = count; }
}

