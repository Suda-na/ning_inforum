package com.niit.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.niit.dao.UserBanHistoryTimeDao;
import com.niit.dao.UserMapper;
import com.niit.dao.UserPermissionMapper;
import com.niit.pojo.UserBanHistory;
import com.niit.service.UserBanHistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class UserBanHistoryServiceImpl implements UserBanHistoryService {

    @Autowired
    private UserBanHistoryTimeDao userBanHistoryDao;

    @Autowired
    private UserPermissionMapper userPermissionMapper;

    @Autowired
    private UserMapper userMapper;

    @Override
    public void recoverExpiredBanPermission() {
        // 查询过期的封禁记录
        List<UserBanHistory> expiredRecords = userBanHistoryDao.selectExpiredBanRecords();
        
        if (expiredRecords == null || expiredRecords.isEmpty()) {
            return;
        }

        ObjectMapper objectMapper = new ObjectMapper();
        
        for (UserBanHistory record : expiredRecords) {
            try {
                // 解析封禁后的权限JSON
                String restrictionsAfter = record.getRestrictionsAfter();
                if (restrictionsAfter == null || restrictionsAfter.isEmpty()) {
                    continue;
                }
                
                Map<String, Object> restrictionsMap = objectMapper.readValue(restrictionsAfter, 
                    new com.fasterxml.jackson.core.type.TypeReference<Map<String, Object>>() {});
                
                // 判断是否是封禁所有权限（登录权限）
                boolean isAllBanned = true;
                for (Object value : restrictionsMap.values()) {
                    if (value != null) {
                        int intValue = value instanceof Integer ? (Integer) value : Integer.parseInt(value.toString());
                        if (intValue == 1) {
                            isAllBanned = false;
                            break;
                        }
                    }
                }
                
                // 恢复权限：根据restrictions_before恢复
                String restrictionsBefore = record.getRestrictionsBefore();
                if (restrictionsBefore != null && !restrictionsBefore.isEmpty()) {
                    Map<String, Object> beforeMap = objectMapper.readValue(restrictionsBefore, 
                        new com.fasterxml.jackson.core.type.TypeReference<Map<String, Object>>() {});
                    
                    // 确保user_permission表中存在该用户的记录
                    if (userPermissionMapper.countByUserId(record.getUserId()) == 0) {
                        userPermissionMapper.insertDefaultPermission(record.getUserId());
                    }
                    
                    // 恢复所有权限
                    userPermissionMapper.updatePermission(
                        record.getUserId(),
                        getIntValue(beforeMap, "can_post"),
                        getIntValue(beforeMap, "can_comment"),
                        getIntValue(beforeMap, "can_like"),
                        getIntValue(beforeMap, "can_follow"),
                        getIntValue(beforeMap, "can_message"),
                        getIntValue(beforeMap, "can_buy"),
                        getIntValue(beforeMap, "can_sell"),
                        getIntValue(beforeMap, "can_run_errand")
                    );
                    
                    // 如果是封禁所有权限（登录权限），还需要恢复user表的status
                    if (isAllBanned) {
                        userMapper.updateStatus(record.getUserId(), 0);
                    }
                }
                
                // 标记封禁记录为已失效
                // 这里可以更新is_active字段，但需要添加更新方法
                // 暂时先不更新，避免影响其他功能
                
            } catch (Exception e) {
                // 记录错误但继续处理其他记录
                System.err.println("解封用户权限失败，用户ID: " + record.getUserId() + ", 错误: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    /**
     * 辅助方法：安全地从Map中获取Integer值
     */
    private Integer getIntValue(Map<String, Object> map, String key) {
        Object value = map.get(key);
        if (value == null) return null;
        if (value instanceof Integer) return (Integer) value;
        try {
            return Integer.parseInt(value.toString());
        } catch (Exception e) {
            return null;
        }
    }
}
