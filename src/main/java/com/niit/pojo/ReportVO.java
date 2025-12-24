package com.niit.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.Date;

/**
 * 举报信息VO类，包含举报信息和关联的用户名
 */
@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class ReportVO {
    private Integer reportId;
    private Integer reporterId;
    private String reporterName;
    private String targetType;
    private Integer targetId;
    private String targetName;
    // 实际被举报用户的ID（动态作者/评论作者/用户）
    private Integer targetUserId;
    private String reportType;
    private String description;
    private String reportImage;
    private Integer status;
    private Integer adminId;
    private String adminName;
    private Integer result;
    private String feedback;
    private Date createTime;
    private Date processTime;
    // 添加帖子内容和评论内容字段，用于显示真实的举报内容
    private String postContent;
    private String commentContent;
    // 添加帖子图片字段（最多3张）
    private String postImage1;
    private String postImage2;
    private String postImage3;
}