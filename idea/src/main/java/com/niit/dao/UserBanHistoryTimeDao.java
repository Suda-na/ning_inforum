package com.niit.dao;

import com.niit.pojo.UserBanHistory;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 用户封禁历史时间相关DAO接口
 */
@Mapper
public interface UserBanHistoryTimeDao {
    /**
     * 查询过期的封禁记录（近1小时内结束的封禁）
     * @return 过期的封禁记录列表
     */
    List<UserBanHistory> selectExpiredBanRecords();
}
