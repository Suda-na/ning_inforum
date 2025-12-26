package com.app.dao;

import com.app.pojo.CUser;
import org.apache.ibatis.annotations.Param;

public interface CUserMapper {
    // 登录查询
    CUser findByUsername(@Param("username") String username);

    // 手机号查重
    CUser findByPhone(@Param("phone") String phone);

    // 插入新用户
    int insertCUser(CUser user);

    // 更新信息
    int updateCUser(CUser user);
}