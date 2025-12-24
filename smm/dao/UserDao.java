package com.niit.dao;

import com.niit.pojo.User;
import com.niit.pojo.UserBanHistory;
import com.niit.pojo.UserPermission;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 用户Dao接口，定义用户相关的数据库操作
 */
@Mapper
public interface UserDao {
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
     * 插入用户
     * @param user 用户对象
     * @return 影响的行数
     */
    int insertUser(User user);

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
     * 更新用户状态
     * @param userId 用户ID
     * @param status 状态：0正常, 1禁用, 2注销
     * @return 影响的行数
     */
    int updateUserStatus(@Param("userId") Integer userId, @Param("status") Integer status);

    /**
     * 重置用户密码
     * @param userId 用户ID
     * @param password 新密码
     * @return 影响的行数
     */
    int resetPassword(@Param("userId") Integer userId, @Param("password") String password);

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
     * @return 影响的行数
     */
    int updateUserPostPermission(@Param("userId") Integer userId, @Param("canPost") Integer canPost);

    /**
     * 更新用户评论权限
     * @param userId 用户ID
     * @param canComment 是否允许评论：1允许，0禁止
     * @return 影响的行数
     */
    int updateUserCommentPermission(@Param("userId") Integer userId, @Param("canComment") Integer canComment);

    /**
     * 更新用户私信权限
     * @param userId 用户ID
     * @param canMessage 是否允许私信：1允许，0禁止
     * @return 影响的行数
     */
    int updateUserMessagePermission(@Param("userId") Integer userId, @Param("canMessage") Integer canMessage);

    /**
     * 插入封禁记录
     * @param banHistory 封禁记录对象
     * @return 影响的行数
     */
    int insertBanHistory(UserBanHistory banHistory);

    /**
     * 查询用户的封禁历史记录
     * @param userId 用户ID
     * @return 封禁历史列表
     */
    List<UserBanHistory> findBanHistoryByUserId(Integer userId);

    /**
     * 将指定用户的激活封禁记录设置为非激活状态（用于权限重复封禁时）
     * @param userId 用户ID
     * @param permissionType 权限类型：post、comment、message
     * @return 影响的行数
     */
    int deactivateActiveBanHistory(@Param("userId") Integer userId, @Param("permissionType") String permissionType);
}
