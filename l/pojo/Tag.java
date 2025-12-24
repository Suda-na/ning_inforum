package com.niit.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * 标签实体类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Tag {
    private Integer tagId;
    private String name;
    private Date createTime;
    
    // 关联查询字段（用于显示所属分类）
    private Integer categoryId;
    private String categoryName;
}

