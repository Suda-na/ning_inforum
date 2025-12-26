package com.niit.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.Date;
@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class UserBanHistoryTime {
    private Integer id;          // 主键
    private Integer userId;      // 被封禁用户ID
    private Date endTime;        // 封禁结束时间
    private Integer permission;
}
