package com.niit.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.Date;

/**
 * 举报实体类
 * 对应数据库中的report表
 */
@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class Report {
    private Integer reportId;
    private Integer reporterId;
    private String targetType;
    private Integer targetId;
    private String reportType;
    private String description;
    private String reportImage;
    private Integer status;
    private Integer adminId;
    private Integer result;
    private String feedback;
    private Date createTime;
    private Date processTime;
}