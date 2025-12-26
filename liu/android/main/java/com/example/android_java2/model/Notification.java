package com.example.android_java2.model;

import java.util.List;

/**
 * 系统通知数据模型
 */
public class Notification {
    private final String id;
    private final String title;
    private final String content;
    private final String time;
    private final List<String> images; // 图片URL列表

    public Notification(String id, String title, String content, String time, List<String> images) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.time = time;
        this.images = images != null ? images : new java.util.ArrayList<>();
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public String getTime() {
        return time;
    }

    public List<String> getImages() {
        return images;
    }
}

