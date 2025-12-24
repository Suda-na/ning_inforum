package com.niit.service;

import com.niit.pojo.Message;
import java.util.List;
import java.util.Map;

/**
 * 消息服务接口
 */
public interface MessageService {
    
    /**
     * 查询系统通知列表
     * @return 系统通知列表
     */
    List<Message> getSystemNotifications();
    
    /**
     * 查询有私信的用户列表（包含未读数量）
     * @return 用户列表（Map格式）
     */
    List<Map<String, Object>> getUsersWithPrivateMessages();
    
    /**
     * 查询管理员与指定用户之间的私信记录
     * 只返回一端为管理员(角色0/1)的消息
     * @param userId 普通用户ID
     * @return 私信记录列表（按时间升序）
     */
    List<Message> getMessagesBetweenAdminAndUser(Integer userId);

    /**
     * 管理员向用户发送私信
     * @param receiverId 接收者ID（普通用户）
     * @param content 消息内容
     * @param msgFormat 消息格式 0文本 1图片
     * @param imageUrl 图片地址（可选）
     * @return 是否发送成功
     */
    boolean addPrivateMessageByAdmin(Integer receiverId, String content, Integer msgFormat, String imageUrl);

    /**
     * 将管理员与指定用户之间，发给管理员且未读的私信标记为已读
     * （is_read 从 0 改为 1，read_time 写当前时间）
     * @param userId 普通用户ID
     */
    void markPrivateMessagesAsRead(Integer userId);
    
    /**
     * 添加系统通知
     * @param message 消息对象（如果receiverId为null，则发送给所有用户）
     * @return 是否成功
     */
    boolean addSystemNotification(Message message);
    
    /**
     * 查询所有用户列表（用于选择接收者）
     * @return 用户列表
     */
    List<Map<String, Object>> getAllUsers();

    /**
     * 更新系统通知
     * @param message 消息对象（需要包含 messageId，其余字段按需更新）
     * @return 是否成功
     */
    boolean updateSystemNotification(Message message);

    /**
     * 删除系统通知
     * @param messageId 通知ID
     * @return 是否成功
     */
    boolean deleteSystemNotification(Integer messageId);
    
    /**
     * 查询系统通知的接收用户列表
     * @param receiverId 接收者ID（如果为null，返回所有用户；否则返回指定用户）
     * @return 用户列表
     */
    List<Map<String, Object>> getNotificationReceivers(Integer receiverId);
    
    // ========== 多接收者支持方法 ==========
    
    /**
     * 添加系统通知（支持多接收者）
     * @param message 消息对象
     * @param receiverIds 接收者ID列表（如果为null或空，表示发送给所有用户）
     * @return 是否成功
     */
    boolean addSystemNotificationWithReceivers(Message message, List<Integer> receiverIds);
    
    /**
     * 查询某个系统通知的所有接收者
     * @param messageId 通知ID
     * @return 接收者列表
     */
    List<Map<String, Object>> getMessageReceivers(Integer messageId);
    
    /**
     * 更新系统通知的接收者（删除旧的，添加新的）
     * @param messageId 通知ID
     * @param receiverIds 新的接收者ID列表（如果为null或空，表示发送给所有用户）
     * @return 是否成功
     */
    boolean updateSystemNotificationReceivers(Integer messageId, List<Integer> receiverIds);
    
    /**
     * 为系统通知添加新的接收者
     * @param messageId 通知ID
     * @param receiverIds 要添加的接收者ID列表
     * @return 是否成功
     */
    boolean addReceiversToNotification(Integer messageId, List<Integer> receiverIds);
    
    /**
     * 从系统通知中删除接收者
     * @param messageId 通知ID
     * @param receiverIds 要删除的接收者ID列表
     * @return 是否成功
     */
    boolean removeReceiversFromNotification(Integer messageId, List<Integer> receiverIds);
}

