package com.niit.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 帖子信息VO类，包含帖子信息和关联的用户名、分类名等
 */
@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class PostVO {
    private Integer postId;
    private Integer userId;
    private String username;
    private String avatar;
    private Integer categoryId;
    private String categoryName;
    private String title;
    private String content;
    private String contactInfo;
    private Date deadline;
    private BigDecimal price;
    private String itemInfo;
    private String startPoint;
    private String endPoint;
    private String image1;
    private String image2;
    private String image3;
    private Integer status; // 0待审核, 1已通过, 2已删除, 3已结束
    private Integer viewCount;
    private Integer likeCount;
    private Integer commentCount;
    private Integer favoriteCount;
    private Integer trendingLevel; // 0普通, 1热门
    private Integer review;
    private Date reviewTime;
    private Date createTime;
    private Date updateTime;
}

