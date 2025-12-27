package com.example.lnforum.model;

public class ErrandOrder {
    // 1. 去掉 final
    private String id;
    private String tag;
    private String title;
    private String desc;
    private String from;
    private String to;
    private double price; // 建议改成 double 方便计算
    private String status;

    // 2. ✅ 添加无参构造函数
    public ErrandOrder() {
    }

    public ErrandOrder(String id, String tag, String title, String desc,
                       String from, String to, double price, String status) {
        this.id = id;
        this.tag = tag;
        this.title = title;
        this.desc = desc;
        this.from = from;
        this.to = to;
        this.price = price;
        this.status = status;
    }

    // 3. ✅ 添加 Setters
    public void setId(String id) { this.id = id; }
    public void setTag(String tag) { this.tag = tag; }
    public void setTitle(String title) { this.title = title; }
    public void setDesc(String desc) { this.desc = desc; }
    public void setFrom(String from) { this.from = from; }
    public void setTo(String to) { this.to = to; }
    public void setPrice(double price) { this.price = price; }
    public void setStatus(String status) { this.status = status; }

    // Getters
    public String getId() { return id; }
    public String getTag() { return tag; }
    public String getTitle() { return title; }
    public String getDesc() { return desc; }
    public String getFrom() { return from; }
    public String getTo() { return to; }
    public double getPrice() { return price; }
    public String getStatus() { return status; }
}