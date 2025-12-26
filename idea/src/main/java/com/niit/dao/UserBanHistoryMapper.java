package com.niit.dao;

import com.niit.pojo.UserBanHistory;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户封禁历史DAO接口
 */
@Mapper
public interface UserBanHistoryMapper {
    /**
     * 插入封禁历史记录
     * @param banHistory 封禁历史对象
     * @return 插入的行数
     */
    int insert(UserBanHistory banHistory);
}