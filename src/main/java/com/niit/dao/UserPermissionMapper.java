package com.niit.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 用户权限DAO接口
 */
@Mapper
public interface UserPermissionMapper {
    /**
     * 更新用户权限
     * @param userId 用户ID
     * @param canPost 是否可以发帖
     * @param canComment 是否可以评论
     * @param canLike 是否可以点赞
     * @param canFollow 是否可以关注
     * @param canMessage 是否可以发送私信
     * @param canBuy 是否可以购买
     * @param canSell 是否可以出售
     * @param canRunErrand 是否可以跑腿
     * @return 更新的行数
     */
    int updatePermission(@Param("userId") Integer userId,
                        @Param("canPost") Integer canPost,
                        @Param("canComment") Integer canComment,
                        @Param("canLike") Integer canLike,
                        @Param("canFollow") Integer canFollow,
                        @Param("canMessage") Integer canMessage,
                        @Param("canBuy") Integer canBuy,
                        @Param("canSell") Integer canSell,
                        @Param("canRunErrand") Integer canRunErrand);

    /**
     * 根据用户ID查询权限（用于检查权限是否存在）
     * @param userId 用户ID
     * @return 权限记录数
     */
    int countByUserId(@Param("userId") Integer userId);

    /**
     * 插入用户权限记录（如果不存在）
     * @param userId 用户ID
     * @return 插入的行数
     */
    int insertDefaultPermission(@Param("userId") Integer userId);
}

