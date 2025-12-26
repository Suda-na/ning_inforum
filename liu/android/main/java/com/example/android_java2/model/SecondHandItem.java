package com.example.android_java2.model;

import java.util.List;

public class SecondHandItem {
    private final String id;
    private final String seller;
    private final String time;
    private final String title;
    private final String desc;
    private final String tag;
    private final String price;
    private final String location;
    private final List<String> images;
    private int views;

    public SecondHandItem(String id, String seller, String time, String title, String desc,
                          String tag, String price, String location, int views, List<String> images) {
        this.id = id;
        this.seller = seller;
        this.time = time;
        this.title = title;
        this.desc = desc;
        this.tag = tag;
        this.price = price;
        this.location = location;
        this.views = views;
        this.images = images != null ? images : new java.util.ArrayList<>();
    }

    public String getId() { return id; }
    public String getSeller() { return seller; }
    public String getTime() { return time; }
    public String getTitle() { return title; }
    public String getDesc() { return desc; }
    public String getTag() { return tag; }
    public String getPrice() { return price; }
    public String getLocation() { return location; }
    public List<String> getImages() { return images; }
    public int getViews() { return views; }
    public void addView() { views++; }
}


