package com.niit.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * 消息实体类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Message {
    private Integer messageId;
    private Integer senderId;      // 发送者ID（NULL表示系统消息）
    private Integer receiverId;     // 接收者ID
    private Integer messageType;   // 类型：0私信，1系统通知
    private String title;           // 消息标题
    private String content;         // 消息内容
    private Integer msgFormat;      // 格式：0text，1img
    private String imageUrl;        // 图片地址
    private String relatedType;     // 关联类型
    private Integer isRead;         // 是否已读：0未读，1已读
    private Date readTime;          // 阅读时间
    private Date createTime;        // 发送时间
    
    // 关联查询字段
    private String senderName;      // 发送者姓名
    private String receiverName;    // 接收者姓名
    private String senderAvatar;    // 发送者头像
    private String receiverAvatar;  // 接收者头像
}

