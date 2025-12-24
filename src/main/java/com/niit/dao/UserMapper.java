package com.niit.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

/**
 * 用户DAO接口
 */
@Mapper
public interface UserMapper {
    /**
     * 更新用户状态
     * @param userId 用户ID
     * @param status 状态：0正常, 1禁用, 2注销
     * @return 更新的行数
     */
    @Update("UPDATE user SET status = #{status} WHERE user_id = #{userId}")
    int updateStatus(@Param("userId") Integer userId, @Param("status") Integer status);

    @Update("UPDATE user SET warning_count = warning_count + 1 WHERE user_id = #{userId}")
    int incrementWarningCount(@Param("userId") Integer userId);
}

