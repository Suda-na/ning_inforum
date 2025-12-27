package com.niit.service.impl;

import com.niit.dao.UserDao;
import com.niit.pojo.User;
import com.niit.pojo.UserBanHistory;
import com.niit.pojo.UserPermission;
import com.niit.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.fasterxml.jackson.databind.JsonNode;

/**
 * 用户服务实现类，实现UserService接口
 */
@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserDao userDao;

    private ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public List<User> findAllUsers() {
        return userDao.findAllUsers();
    }

    @Override
    public User findUserById(Integer userId) {
        return userDao.findUserById(userId);
    }

    @Override
    public User findUserByUsername(String username) {
        return userDao.findUserByUsername(username);
    }

    @Override
    public int addUser(User user) {
        return userDao.insertUser(user);
    }

    @Override
    public int updateUser(User user) {
        return userDao.updateUser(user);
    }

    @Override
    public int deleteUser(Integer userId) {
        return userDao.deleteUser(userId);
    }

    @Override
    public List<User> findUsersByStatus(Integer status) {
        return userDao.findUsersByStatus(status);
    }

    @Override
    public int banUser(Integer userId) {
        // 直接调用updateUserStatus方法，只更新状态字段
        return userDao.updateUserStatus(userId, 1); // 1表示已封禁
    }

    @Override
    public int unbanUser(Integer userId) {
        // 直接调用updateUserStatus方法，只更新状态字段
        return userDao.updateUserStatus(userId, 0); // 0表示正常
    }

    @Override
    public int resetPassword(Integer userId) {
        // 重置密码为123456
        return userDao.resetPassword(userId, "123456");
    }

    @Override
    public UserPermission findUserPermissionByUserId(Integer userId) {
        return userDao.findUserPermissionByUserId(userId);
    }

    @Override
    public int updateUserPostPermission(Integer userId, Integer canPost, Integer adminId, String reason, Integer durationDays) {
        // 获取用户当前权限
        UserPermission currentPermission = userDao.findUserPermissionByUserId(userId);
        if (currentPermission == null) {
            return 0;
        }

        // 生成权限前后对比的JSON
        String restrictionsBefore = generatePermissionJson(currentPermission);

        // 更新权限
        int result = userDao.updateUserPostPermission(userId, canPost);

        // 获取更新后的权限
        UserPermission updatedPermission = userDao.findUserPermissionByUserId(userId);
        String restrictionsAfter = generatePermissionJson(updatedPermission);

        // 只有封禁操作才插入封禁记录，恢复操作（canPost=1）不插入
        if (canPost == 0) {
            // 如果对同一权限进行了多次修改，先将之前对此权限的激活记录设置为非激活（is_active=0）
            // 这样新插入的记录就是唯一激活的记录，刷新页面时会正确显示新的封禁天数
            userDao.deactivateActiveBanHistory(userId, "post");
            insertBanHistory(userId, adminId, "封禁", restrictionsBefore, restrictionsAfter, reason, durationDays);
            // 确保user_permission表的权限状态与is_active=1的最新记录的restrictions_after保持一致
            syncPermissionFromActiveBanHistory(userId);
        }

        return result;
    }

    @Override
    public int updateUserCommentPermission(Integer userId, Integer canComment, Integer adminId, String reason, Integer durationDays) {
        // 获取用户当前权限
        UserPermission currentPermission = userDao.findUserPermissionByUserId(userId);
        if (currentPermission == null) {
            return 0;
        }

        // 生成权限前后对比的JSON
        String restrictionsBefore = generatePermissionJson(currentPermission);

        // 更新权限
        int result = userDao.updateUserCommentPermission(userId, canComment);

        // 获取更新后的权限
        UserPermission updatedPermission = userDao.findUserPermissionByUserId(userId);
        String restrictionsAfter = generatePermissionJson(updatedPermission);

        // 只有封禁操作才插入封禁记录，恢复操作（canComment=1）不插入
        if (canComment == 0) {
            // 如果对同一权限进行了多次修改，先将之前对此权限的激活记录设置为非激活（is_active=0）
            // 这样新插入的记录就是唯一激活的记录，刷新页面时会正确显示新的封禁天数
            userDao.deactivateActiveBanHistory(userId, "comment");
            insertBanHistory(userId, adminId, "封禁", restrictionsBefore, restrictionsAfter, reason, durationDays);
            // 确保user_permission表的权限状态与is_active=1的最新记录的restrictions_after保持一致
            syncPermissionFromActiveBanHistory(userId);
        }

        return result;
    }

    @Override
    public int updateUserMessagePermission(Integer userId, Integer canMessage, Integer adminId, String reason, Integer durationDays) {
        // 获取用户当前权限
        UserPermission currentPermission = userDao.findUserPermissionByUserId(userId);
        if (currentPermission == null) {
            return 0;
        }

        // 生成权限前后对比的JSON
        String restrictionsBefore = generatePermissionJson(currentPermission);

        // 更新权限
        int result = userDao.updateUserMessagePermission(userId, canMessage);

        // 获取更新后的权限
        UserPermission updatedPermission = userDao.findUserPermissionByUserId(userId);
        String restrictionsAfter = generatePermissionJson(updatedPermission);

        // 只有封禁操作才插入封禁记录，恢复操作（canMessage=1）不插入
        if (canMessage == 0) {
            // 如果对同一权限进行了多次修改，先将之前对此权限的激活记录设置为非激活（is_active=0）
            // 这样新插入的记录就是唯一激活的记录，刷新页面时会正确显示新的封禁天数
            userDao.deactivateActiveBanHistory(userId, "message");
            insertBanHistory(userId, adminId, "封禁", restrictionsBefore, restrictionsAfter, reason, durationDays);
            // 确保user_permission表的权限状态与is_active=1的最新记录的restrictions_after保持一致
            syncPermissionFromActiveBanHistory(userId);
        }

        return result;
    }

    @Override
    public List<UserBanHistory> findBanHistoryByUserId(Integer userId) {
        return userDao.findBanHistoryByUserId(userId);
    }

    /**
     * 生成权限JSON字符串
     * @param permission 用户权限对象
     * @return JSON格式的权限字符串
     */
    private String generatePermissionJson(UserPermission permission) {
        try {
            return objectMapper.writeValueAsString(permission);
        } catch (Exception e) {
            // 如果JSON序列化失败，返回简单的权限字符串
            return String.format("{\"can_post\":%d,\"can_comment\":%d,\"can_message\":%d}", 
                    permission.getCanPost(), permission.getCanComment(), permission.getCanMessage());
        }
    }

    /**
     * 插入封禁记录
     * @param userId 用户ID
     * @param adminId 管理员ID
     * @param actionType 操作类型：封禁或恢复
     * @param restrictionsBefore 封禁前权限
     * @param restrictionsAfter 封禁后权限
     * @param reason 封禁原因
     * @param durationDays 封禁天数
     */
    private void insertBanHistory(Integer userId, Integer adminId, String actionType, 
                                  String restrictionsBefore, String restrictionsAfter, 
                                  String reason, Integer durationDays) {
        UserBanHistory banHistory = new UserBanHistory();
        banHistory.setUserId(userId);
        banHistory.setAdminId(adminId);
        banHistory.setActionType(actionType);
        banHistory.setRestrictionsBefore(restrictionsBefore);
        banHistory.setRestrictionsAfter(restrictionsAfter);
        banHistory.setReason(reason);
        banHistory.setDurationDays(durationDays);
        banHistory.setStartTime(new Date());
        banHistory.setIsActive(1);
        banHistory.setCreateTime(new Date());

        // 计算结束时间
        if (durationDays > 0) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(new Date());
            calendar.add(Calendar.DAY_OF_YEAR, durationDays);
            banHistory.setEndTime(calendar.getTime());
        }

        // 插入封禁记录
        userDao.insertBanHistory(banHistory);
    }

    /**
     * 根据is_active=1的封禁记录的restrictions_after同步user_permission表的权限状态
     * 确保user_permission表的权限状态与is_active=1的最新记录的restrictions_after保持一致
     * @param userId 用户ID
     */
    private void syncPermissionFromActiveBanHistory(Integer userId) {
        try {
            // 查询所有is_active=1的封禁记录，按时间倒序排列（SQL已过滤）
            List<UserBanHistory> banHistories = userDao.findBanHistoryByUserId(userId);
            
            // 找到每个权限对应的最新的is_active=1的记录
            // 由于查询结果已经按start_time DESC排序，第一个匹配的记录就是最新的
            UserBanHistory latestPostBan = null;
            UserBanHistory latestCommentBan = null;
            UserBanHistory latestMessageBan = null;
            
            for (UserBanHistory banHistory : banHistories) {
                // 查询已过滤is_active=1，这里只需要判断action_type
                if ("封禁".equals(banHistory.getActionType())) {
                    String restrictionsAfter = banHistory.getRestrictionsAfter();
                    if (restrictionsAfter != null) {
                        try {
                            JsonNode afterNode = objectMapper.readTree(restrictionsAfter);
                            
                            // 查找发帖权限的最新封禁记录
                            if (latestPostBan == null) {
                                JsonNode canPostNode = afterNode.has("can_post") ? afterNode.get("can_post") : afterNode.get("canPost");
                                if (canPostNode != null && canPostNode.asInt() == 0) {
                                    latestPostBan = banHistory;
                                }
                            }
                            
                            // 查找评论权限的最新封禁记录
                            if (latestCommentBan == null) {
                                JsonNode canCommentNode = afterNode.has("can_comment") ? afterNode.get("can_comment") : afterNode.get("canComment");
                                if (canCommentNode != null && canCommentNode.asInt() == 0) {
                                    latestCommentBan = banHistory;
                                }
                            }
                            
                            // 查找私信权限的最新封禁记录
                            if (latestMessageBan == null) {
                                JsonNode canMessageNode = afterNode.has("can_message") ? afterNode.get("can_message") : afterNode.get("canMessage");
                                if (canMessageNode != null && canMessageNode.asInt() == 0) {
                                    latestMessageBan = banHistory;
                                }
                            }
                        } catch (Exception e) {
                            // JSON解析失败时，使用字符串匹配
                            if (latestPostBan == null && 
                                (restrictionsAfter.contains("\"can_post\":0") || restrictionsAfter.contains("\"canPost\":0"))) {
                                latestPostBan = banHistory;
                            }
                            if (latestCommentBan == null && 
                                (restrictionsAfter.contains("\"can_comment\":0") || restrictionsAfter.contains("\"canComment\":0"))) {
                                latestCommentBan = banHistory;
                            }
                            if (latestMessageBan == null && 
                                (restrictionsAfter.contains("\"can_message\":0") || restrictionsAfter.contains("\"canMessage\":0"))) {
                                latestMessageBan = banHistory;
                            }
                        }
                    }
                }
            }
            
            // 根据找到的最新封禁记录的restrictions_after来更新user_permission表
            UserPermission currentPermission = userDao.findUserPermissionByUserId(userId);
            if (currentPermission != null) {
                // 更新发帖权限
                if (latestPostBan != null) {
                    try {
                        JsonNode afterNode = objectMapper.readTree(latestPostBan.getRestrictionsAfter());
                        JsonNode canPostNode = afterNode.has("can_post") ? afterNode.get("can_post") : afterNode.get("canPost");
                        if (canPostNode != null && canPostNode.asInt() != currentPermission.getCanPost()) {
                            userDao.updateUserPostPermission(userId, canPostNode.asInt());
                        }
                    } catch (Exception e) {
                        // 解析失败时不更新
                    }
                }
                
                // 更新评论权限
                if (latestCommentBan != null) {
                    try {
                        JsonNode afterNode = objectMapper.readTree(latestCommentBan.getRestrictionsAfter());
                        JsonNode canCommentNode = afterNode.has("can_comment") ? afterNode.get("can_comment") : afterNode.get("canComment");
                        if (canCommentNode != null && canCommentNode.asInt() != currentPermission.getCanComment()) {
                            userDao.updateUserCommentPermission(userId, canCommentNode.asInt());
                        }
                    } catch (Exception e) {
                        // 解析失败时不更新
                    }
                }
                
                // 更新私信权限
                if (latestMessageBan != null) {
                    try {
                        JsonNode afterNode = objectMapper.readTree(latestMessageBan.getRestrictionsAfter());
                        JsonNode canMessageNode = afterNode.has("can_message") ? afterNode.get("can_message") : afterNode.get("canMessage");
                        if (canMessageNode != null && canMessageNode.asInt() != currentPermission.getCanMessage()) {
                            userDao.updateUserMessagePermission(userId, canMessageNode.asInt());
                        }
                    } catch (Exception e) {
                        // 解析失败时不更新
                    }
                }
            }
        } catch (Exception e) {
            // 同步失败时不影响主流程，记录错误即可
            e.printStackTrace();
        }
    }
}
