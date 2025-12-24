package com.niit.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 用户实体类
 * gender 字段对应数据库 tinyint：0未知，1男，2女
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    private Integer userId;
    private String username;
    private String realName;
    private String email;
    private String phone;
    private String avatar;
    /**
     * 性别：0未知，1男，2女
     */
    private Integer gender;
    private Integer unreadCount;    // 未读消息数量
}

