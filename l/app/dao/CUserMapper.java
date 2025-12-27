package com.app.dao;

import com.app.pojo.CUser;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface CUserMapper {
    // 登录查询
    CUser findByUsername(@Param("username") String username);

    // 手机号查重
    CUser findByPhone(@Param("phone") String phone);

    // 根据ID查询用户
    CUser findById(@Param("userId") Integer userId);

    // 插入新用户
    int insertCUser(CUser user);

    // 更新信息
    int updateCUser(CUser user);
    
    // 获取关注/粉丝列表
    List<CUser> selectRelationList(@Param("userId") Integer userId, @Param("type") Integer type);
    
    // 关注操作
    int insertFollow(@Param("followerId") Integer followerId, @Param("followingId") Integer followingId);
    
    // 取消关注/移除粉丝
    int deleteFollow(@Param("followerId") Integer followerId, @Param("followingId") Integer followingId);
    
    // 检查是否已关注
    Integer checkFollowExists(@Param("followerId") Integer followerId, @Param("followingId") Integer followingId);
    
    // 获取用户统计信息
    Integer countFollowing(@Param("userId") Integer userId);
    Integer countFans(@Param("userId") Integer userId);
    Integer countLikes(@Param("userId") Integer userId);
}


