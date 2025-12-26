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
 * 用户权限实体类，对应数据库中的user_permission表
 */
public class UserPermission {
    private Integer permissionId;
    private Integer userId;
    private Integer canPost;
    private Integer canComment;
    private Integer canLike;
    private Integer canFollow;
    private Integer canMessage;
    private Integer canBuy;
    private Integer canSell;
    private Integer canRunErrand;
    private Date updateTime;

    // Getter和Setter方法
    public Integer getPermissionId() {
        return permissionId;
    }

    public void setPermissionId(Integer permissionId) {
        this.permissionId = permissionId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getCanPost() {
        return canPost;
    }

    public void setCanPost(Integer canPost) {
        this.canPost = canPost;
    }

    public Integer getCanComment() {
        return canComment;
    }

    public void setCanComment(Integer canComment) {
        this.canComment = canComment;
    }

    public Integer getCanLike() {
        return canLike;
    }

    public void setCanLike(Integer canLike) {
        this.canLike = canLike;
    }

    public Integer getCanFollow() {
        return canFollow;
    }

    public void setCanFollow(Integer canFollow) {
        this.canFollow = canFollow;
    }

    public Integer getCanMessage() {
        return canMessage;
    }

    public void setCanMessage(Integer canMessage) {
        this.canMessage = canMessage;
    }

    public Integer getCanBuy() {
        return canBuy;
    }

    public void setCanBuy(Integer canBuy) {
        this.canBuy = canBuy;
    }

    public Integer getCanSell() {
        return canSell;
    }

    public void setCanSell(Integer canSell) {
        this.canSell = canSell;
    }

    public Integer getCanRunErrand() {
        return canRunErrand;
    }

    public void setCanRunErrand(Integer canRunErrand) {
        this.canRunErrand = canRunErrand;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }
}