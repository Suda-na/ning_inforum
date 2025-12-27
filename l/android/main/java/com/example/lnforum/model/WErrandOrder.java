package com.example.lnforum.model;

import java.util.List;

public class WErrandOrder {
    private final String id;
    private final String tag;
    private final String title;
    private final String desc;
    private final String from;
    private final String to;
    private final String price;
    private final String status;
    private final List<String> images;

    public WErrandOrder(String id, String tag, String title, String desc,
                       String from, String to, String price, String status) {
        this(id, tag, title, desc, from, to, price, status, null);
    }

    public WErrandOrder(String id, String tag, String title, String desc,
                       String from, String to, String price, String status, List<String> images) {
        this.id = id;
        this.tag = tag;
        this.title = title;
        this.desc = desc;
        this.from = from;
        this.to = to;
        this.price = price;
        this.status = status;
        this.images = images != null ? images : new java.util.ArrayList<>();
    }

    public String getId() {
        return id;
    }

    public String getTag() {
        return tag;
    }

    public String getTitle() {
        return title;
    }

    public String getDesc() {
        return desc;
    }

    public String getFrom() {
        return from;
    }

    public String getTo() {
        return to;
    }

    public String getPrice() {
        return price;
    }

    public String getStatus() {
        return status;
    }

    public List<String> getImages() {
        return images;
    }
}

