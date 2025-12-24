package com.niit.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * 分类实体类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Category {
    private Integer categoryId;
    private String name;
    private String description;
    private Integer sortOrder;
    private Integer status;  // 0禁用，1启用
    private Date createTime;
}

