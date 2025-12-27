package com.example.lnforum.model;

import java.io.Serializable;

public class CTradeTask implements Serializable {
    private Integer taskId;
    private String title;
    private String description;
    private Double amount;
    private Integer taskStatus; // 0:进行中, 1:已完成
    private Integer creatorId;  // 发布者ID (用于区分我的发布)
    private Integer acceptorId; // 接单者ID (用于区分我的接单)
    private String createTime;

    // --- Getters and Setters ---
    public Integer getTaskId() { return taskId; }
    public void setTaskId(Integer taskId) { this.taskId = taskId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Double getAmount() { return amount; }
    public void setAmount(Double amount) { this.amount = amount; }

    public Integer getTaskStatus() { return taskStatus; }
    public void setTaskStatus(Integer taskStatus) { this.taskStatus = taskStatus; }

    public Integer getCreatorId() { return creatorId; }
    public void setCreatorId(Integer creatorId) { this.creatorId = creatorId; }

    public Integer getAcceptorId() { return acceptorId; }
    public void setAcceptorId(Integer acceptorId) { this.acceptorId = acceptorId; }

    public String getCreateTime() { return createTime; }
    public void setCreateTime(String createTime) { this.createTime = createTime; }
}