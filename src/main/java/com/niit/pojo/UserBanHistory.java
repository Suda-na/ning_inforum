package com.niit.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.Date;

/**
 * 用户封禁历史实体类
 * 对应数据库中的user_ban_history表
 */
@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
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
}