package com.niit.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.Date;

@NoArgsConstructor
@Data
@AllArgsConstructor
@ToString
/**
 * 用户封禁历史实体类，对应数据库中的user_ban_history表
 */
public class UserBanHistory {
    private Integer historyId;
    private Integer userId;
    private Integer adminId;
    private String actionType;
    private String restrictionsBefore;
    private String restrictionsAfter;
    private String reason;
    private Integer durationDays;
    private Date startTime;
    private Date endTime;
    private Integer isActive;
    private Date createTime;

    // Getter和Setter方法
    public Integer getHistoryId() {
        return historyId;
    }

    public void setHistoryId(Integer historyId) {
        this.historyId = historyId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getAdminId() {
        return adminId;
    }

    public void setAdminId(Integer adminId) {
        this.adminId = adminId;
    }

    public String getActionType() {
        return actionType;
    }

    public void setActionType(String actionType) {
        this.actionType = actionType;
    }

    public String getRestrictionsBefore() {
        return restrictionsBefore;
    }

    public void setRestrictionsBefore(String restrictionsBefore) {
        this.restrictionsBefore = restrictionsBefore;
    }

    public String getRestrictionsAfter() {
        return restrictionsAfter;
    }

    public void setRestrictionsAfter(String restrictionsAfter) {
        this.restrictionsAfter = restrictionsAfter;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public Integer getDurationDays() {
        return durationDays;
    }

    public void setDurationDays(Integer durationDays) {
        this.durationDays = durationDays;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public Integer getIsActive() {
        return isActive;
    }

    public void setIsActive(Integer isActive) {
        this.isActive = isActive;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}