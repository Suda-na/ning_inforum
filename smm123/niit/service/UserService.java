package com.niit.service;

import com.niit.pojo.User;
import com.niit.pojo.UserBanHistory;
import com.niit.pojo.UserPermission;
import java.util.List;

/**
 * 用户服务接口，定义用户相关的业务逻辑
 */
public interface UserService {
    /**
     * 查询所有用户
     * @return 用户列表
     */
    List<User> findAllUsers();

    /**
     * 根据ID查询用户
     * @param userId 用户ID
     * @return 用户对象
     */
    User findUserById(Integer userId);

    /**
     * 根据用户名查询用户
     * @param username 用户名
     * @return 用户对象
     */
    User findUserByUsername(String username);

    /**
     * 添加用户
     * @param user 用户对象
     * @return 影响的行数
     */
    int addUser(User user);

    /**
     * 更新用户信息
     * @param user 用户对象
     * @return 影响的行数
     */
    int updateUser(User user);

    /**
     * 删除用户
     * @param userId 用户ID
     * @return 影响的行数
     */
    int deleteUser(Integer userId);

    /**
     * 根据状态查询用户
     * @param status 状态：0正常, 1禁用, 2注销
     * @return 用户列表
     */
    List<User> findUsersByStatus(Integer status);

    /**
     * 封禁用户
     * @param userId 用户ID
     * @return 影响的行数
     */
    int banUser(Integer userId);

    /**
     * 恢复用户
     * @param userId 用户ID
     * @return 影响的行数
     */
    int unbanUser(Integer userId);

    /**
     * 重置用户密码
     * @param userId 用户ID
     * @return 影响的行数
     */
    int resetPassword(Integer userId);

    /**
     * 根据用户ID查询权限
     * @param userId 用户ID
     * @return 用户权限对象
     */
    UserPermission findUserPermissionByUserId(Integer userId);

    /**
     * 更新用户发帖权限
     * @param userId 用户ID
     * @param canPost 是否允许发帖：1允许，0禁止
     * @param adminId 管理员ID
     * @param reason 操作原因
     * @param durationDays 封禁天数
     * @return 影响的行数
     */
    int updateUserPostPermission(Integer userId, Integer canPost, Integer adminId, String reason, Integer durationDays);

    /**
     * 更新用户评论权限
     * @param userId 用户ID
     * @param canComment 是否允许评论：1允许，0禁止
     * @param adminId 管理员ID
     * @param reason 操作原因
     * @param durationDays 封禁天数
     * @return 影响的行数
     */
    int updateUserCommentPermission(Integer userId, Integer canComment, Integer adminId, String reason, Integer durationDays);

    /**
     * 更新用户私信权限
     * @param userId 用户ID
     * @param canMessage 是否允许私信：1允许，0禁止
     * @param adminId 管理员ID
     * @param reason 操作原因
     * @param durationDays 封禁天数
     * @return 影响的行数
     */
    int updateUserMessagePermission(Integer userId, Integer canMessage, Integer adminId, String reason, Integer durationDays);

    /**
     * 查询用户的封禁历史记录（当前实现已在SQL中过滤is_active=1）
     * @param userId 用户ID
     * @return 封禁历史列表
     */
    List<UserBanHistory> findBanHistoryByUserId(Integer userId);
}
