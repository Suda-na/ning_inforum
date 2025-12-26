package com.example.lnforum.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class CirclePost implements Serializable {
    private String id;
    private String author;
    private String avatar;
    private String time;
    private String title;
    private String content;
    private String tag;
    private int views;
    private int comments;
    private int likes;
    private boolean liked;
    private boolean watched;
    private boolean followed;
    private List<String> images;

    // ✅ No-args constructor (Crucial for serialization/deserialization)
    public CirclePost() {
        this.images = new ArrayList<>();
    }

    // Full-args constructor
    public CirclePost(String id, String author, String avatar, String time, String title, String content, String tag,
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
        this.images = images != null ? images : new ArrayList<>();
    }

    // ✅ Setters (Fixes 'Cannot resolve method setXxx')
    public void setId(String id) { this.id = id; }
    public void setAuthor(String author) { this.author = author; }
    public void setAvatar(String avatar) { this.avatar = avatar; }
    public void setTime(String time) { this.time = time; }
    public void setTitle(String title) { this.title = title; }
    public void setContent(String content) { this.content = content; }
    public void setTag(String tag) { this.tag = tag; }
    public void setViews(int views) { this.views = views; }
    public void setComments(int comments) { this.comments = comments; }
    public void setLikes(int likes) { this.likes = likes; }
    public void setLiked(boolean liked) { this.liked = liked; }
    public void setWatched(boolean watched) { this.watched = watched; }
    public void setFollowed(boolean followed) { this.followed = followed; }
    public void setImages(List<String> images) { this.images = images; }

    // Getters
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
    public boolean isWatched() { return watched; }
    public boolean isFollowed() { return followed; }
    public List<String> getImages() { return images; }

    // ✅ Business Logic Methods (Fixes 'Cannot resolve method toggleXxx')
    public void addView() { views++; }

    public void toggleLike() {
        liked = !liked;
        likes += liked ? 1 : -1;
        if (likes < 0) likes = 0;
    }

    public void toggleWatch() { watched = !watched; }

    public void toggleFollow() { followed = !followed; }
}