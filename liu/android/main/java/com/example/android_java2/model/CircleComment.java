package com.example.android_java2.model;

public class CircleComment {
    private final String id;
    private final String postId;
    private final String author;
    private final String content;
    private final String time;

    public CircleComment(String id, String postId, String author, String content, String time) {
        this.id = id;
        this.postId = postId;
        this.author = author;
        this.content = content;
        this.time = time;
    }

    public String getId() { return id; }
    public String getPostId() { return postId; }
    public String getAuthor() { return author; }
    public String getContent() { return content; }
    public String getTime() { return time; }
}


