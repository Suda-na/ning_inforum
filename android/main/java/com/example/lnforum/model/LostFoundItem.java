package com.example.lnforum.model;

import java.util.List;

public class LostFoundItem {
    private final String id;
    private final String user;
    private final String time;
    private final String title;
    private final String desc;
    private final String tag; // 失物 / 招领
    private final String location;
    private final List<String> images;
    private int views;

    public LostFoundItem(String id, String user, String time, String title, String desc,
                         String tag, String location, int views, List<String> images) {
        this.id = id;
        this.user = user;
        this.time = time;
        this.title = title;
        this.desc = desc;
        this.tag = tag;
        this.location = location;
        this.views = views;
        this.images = images != null ? images : new java.util.ArrayList<>();
    }

    public String getId() { return id; }
    public String getUser() { return user; }
    public String getTime() { return time; }
    public String getTitle() { return title; }
    public String getDesc() { return desc; }
    public String getTag() { return tag; }
    public String getLocation() { return location; }
    public List<String> getImages() { return images; }
    public int getViews() { return views; }
    public void addView() { views++; }
}


