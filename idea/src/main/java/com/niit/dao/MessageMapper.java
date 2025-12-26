package com.niit.dao;

import com.niit.pojo.Message;
import org.apache.ibatis.annotations.Param;
import java.util.List;
import java.util.Map;

/**
 * 消息Mapper接口
 */
public interface MessageMapper {
    
    /**
     * 查询系统通知列表（message_type=1）
     * @return 系统通知列表
     */
    List<Message> selectSystemNotifications();
    
    /**
     * 根据接收者ID查询私信列表（message_type=0）
     * @param receiverId 接收者ID
     * @return 私信列表
     */
    List<Message> selectPrivateMessagesByReceiverId(@Param("receiverId") Integer receiverId);
    
    /**
     * 查询有私信的用户列表（去重，包含未读数量）
     * @return 用户列表（Map格式，包含未读消息数）
     */
    List<Map<String, Object>> selectUsersWithPrivateMessages();
    
    /**
     * 根据当前登录用户查询有私信的用户列表（去重，包含未读数量）
     * @param currentUserId 当前登录用户ID
     * @return 用户列表（Map格式，包含未读消息数）
     */
    List<Map<String, Object>> selectUsersWithPrivateMessagesByCurrentUser(@Param("currentUserId") Integer currentUserId);
    
    /**
     * 查询指定用户之间的私信记录
     * 一端必须是管理员(角色0/1)，另一端为指定用户
     * @param userId 普通用户ID
     * @return 私信记录列表（按时间升序）
     */
    List<Message> selectMessagesBetweenAdminAndUser(@Param("userId") Integer userId);
    
    /**
     * 查询当前登录用户与指定用户之间的私信记录
     * @param currentUserId 当前登录用户ID
     * @param otherUserId 对方用户ID
     * @return 私信记录列表（按时间升序）
     */
    List<Message> selectMessagesBetweenUsers(@Param("currentUserId") Integer currentUserId, 
                                             @Param("otherUserId") Integer otherUserId);
    
    /**
     * 统计用户的未读私信数量
     * @param receiverId 接收者ID
     * @return 未读数量
     */
    Integer countUnreadPrivateMessages(@Param("receiverId") Integer receiverId);
    
    /**
     * 统计当前登录用户与指定用户之间的未读私信数量
     * @param currentUserId 当前登录用户ID
     * @param otherUserId 对方用户ID
     * @return 未读数量
     */
    Integer countUnreadPrivateMessagesBetweenUsers(@Param("currentUserId") Integer currentUserId,
                                                    @Param("otherUserId") Integer otherUserId);
    
    /**
     * 插入管理员发送的私信
     * @param adminId 管理员ID
     * @param receiverId 接收者ID
     * @param content 内容
     * @param msgFormat 格式：0文本，1图片
     * @param imageUrl 图片地址
     * @return 影响的行数
     */
    int insertPrivateMessageFromAdmin(@Param("adminId") Integer adminId,
                                      @Param("receiverId") Integer receiverId,
                                      @Param("content") String content,
                                      @Param("msgFormat") Integer msgFormat,
                                      @Param("imageUrl") String imageUrl);
    
    /**
     * 查询一个可用的管理员ID（优先超级管理员）
     * @return 管理员ID
     */
    Integer selectDefaultAdminId();

    /**
     * 将指定用户发给管理员的未读私信标记为已读
     * @param adminId 管理员ID
     * @param userId 普通用户ID
     * @return 影响行数
     */
    int updatePrivateMessagesRead(@Param("adminId") Integer adminId,
                                  @Param("userId") Integer userId);
    
    /**
     * 将当前登录用户与指定用户之间的未读私信标记为已读
     * @param currentUserId 当前登录用户ID（作为接收者）
     * @param otherUserId 对方用户ID（作为发送者）
     * @return 影响行数
     */
    int updatePrivateMessagesReadBetweenUsers(@Param("currentUserId") Integer currentUserId,
                                              @Param("otherUserId") Integer otherUserId);
    
    /**
     * 插入系统通知
     * @param message 消息对象
     * @return 影响的行数
     */
    int insertSystemNotification(Message message);

    /**
     * 更新系统通知
     * @param message 消息对象（至少需要包含 messageId、title、content，可选 receiverId、imageUrl、msgFormat）
     * @return 影响的行数
     */
    int updateSystemNotification(Message message);
    
    /**
     * 查询所有用户ID列表
     * @return 用户ID列表
     */
    List<Integer> selectAllUserIds();
    
    /**
     * 查询用户列表（用于选择接收者）
     * @return 用户列表（Map格式）
     */
    List<Map<String, Object>> selectAllUsers();

    /**
     * 删除系统通知
     * @param messageId 通知ID
     * @return 影响的行数
     */
    int deleteSystemNotification(@Param("messageId") Integer messageId);
    
    /**
     * 查询系统通知的接收用户列表
     * @param receiverId 接收者ID（如果为null，返回所有用户；否则返回指定用户）
     * @return 用户列表（Map格式，包含userId、username、realName、gender、phone）
     */
    List<Map<String, Object>> selectNotificationReceivers(@Param("receiverId") Integer receiverId);
    
    // ========== 多接收者支持方法 ==========
    
    /**
     * 批量插入消息接收者关联记录
     * @param messageId 消息ID
     * @param receiverIds 接收者ID列表
     * @return 影响的行数
     */
    int insertMessageReceivers(@Param("messageId") Integer messageId,
                               @Param("receiverIds") List<Integer> receiverIds);
    
    /**
     * 查询某个消息的所有接收者
     * @param messageId 消息ID
     * @return 接收者列表（Map格式，包含userId、username、realName、gender、email、phone、isRead、readTime）
     */
    List<Map<String, Object>> selectMessageReceivers(@Param("messageId") Integer messageId);
    
    /**
     * 删除某个消息的指定接收者
     * @param messageId 消息ID
     * @param receiverIds 要删除的接收者ID列表
     * @return 影响的行数
     */
    int deleteMessageReceivers(@Param("messageId") Integer messageId,
                                @Param("receiverIds") List<Integer> receiverIds);
    
    /**
     * 为某个消息添加新的接收者
     * @param messageId 消息ID
     * @param receiverIds 要添加的接收者ID列表
     * @return 影响的行数
     */
    int addMessageReceivers(@Param("messageId") Integer messageId,
                            @Param("receiverIds") List<Integer> receiverIds);
    
    /**
     * 删除某个消息的所有接收者关联
     * @param messageId 消息ID
     * @return 影响的行数
     */
    int deleteAllMessageReceivers(@Param("messageId") Integer messageId);
}

