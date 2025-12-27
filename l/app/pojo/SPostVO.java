package com.app.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 安卓端帖子信息VO类
 */
@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class SPostVO {
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
    private Integer status; // 0待审核, 1已通过, 2已删除, 3已结束, 4未通过
    private Integer viewCount;
    private Integer likeCount;
    private Integer commentCount;
    private Integer favoriteCount;
    private Integer trendingLevel; // 0普通, 1热门
    private Date createTime;
    private Date updateTime;
    private Integer isLiked; // 当前用户是否点赞
    private Integer isFavorited; // 当前用户是否收藏
    private Integer isFollowed; // 当前用户是否关注
}

